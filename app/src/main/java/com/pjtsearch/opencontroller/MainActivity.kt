package com.pjtsearch.opencontroller

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.extensions.SettingsSerializer
import com.pjtsearch.opencontroller.extensions.copy
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.ui.components.*
import com.pjtsearch.opencontroller.ui.components.Widget as WidgetDisplay
import com.google.accompanist.systemuicontroller.LocalSystemUiController
import com.google.accompanist.systemuicontroller.rememberAndroidSystemUiController
import com.pjtsearch.opencontroller.components.PagedBackdrop
import com.pjtsearch.opencontroller.components.PagedBottomSheet
import com.pjtsearch.opencontroller.const.BackgroundPage
import com.pjtsearch.opencontroller.const.BottomSheetPage
import com.pjtsearch.opencontroller.const.Page
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "Settings.proto",
    serializer = SettingsSerializer
)

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val controller = rememberAndroidSystemUiController()
            CompositionLocalProvider(LocalSystemUiController provides controller) {
                SystemUi(this.window) {
                    MainActivityView()
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainActivityView() {
    var backgroundPage: BackgroundPage by rememberSaveable(
        saver = Saver({it.value.serialize()}, {mutableStateOf(BackgroundPage.deserialize(it))})
    ) {
        mutableStateOf(BackgroundPage.Homes)
    }
    var page: Page by rememberSaveable(
        saver = Saver({it.value.serialize()}, {mutableStateOf(Page.deserialize(it))})
    ) { mutableStateOf(Page.EmptyGreeter) }
    val menuState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val houseRefs = ctx.settingsDataStore.data.map {
        it.houseRefsList
    }.collectAsState(listOf())

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var sheetPage: BottomSheetPage by rememberSaveable(
        saver = Saver({it.value.serialize()}, {mutableStateOf(BottomSheetPage.deserialize(it))})
    ) {
        mutableStateOf(BottomSheetPage.Widgets(listOf()))
    }

    val onError = { err: Throwable ->
        scope.launch {
            err.printStackTrace()
            val result = menuState.snackbarHostState.showSnackbar(err.message ?: "Unknown error occurred", "Copy")
            if (result == SnackbarResult.ActionPerformed) {
                copy(err.localizedMessage ?: "Unknown error", err.toString(), ctx)
            }
        }
    }
    val onOpenMenu = { widgets: List<Widget> ->
        scope.launch {
            sheetPage = BottomSheetPage.Widgets(widgets)
            sheetState.show()
        }
    }
    PagedBottomSheet(
        state = sheetState,
        page = sheetPage,
        sheetContent = { pg ->
            val bgPage = backgroundPage
            when (pg) {
                is BottomSheetPage.Widgets -> if (bgPage is BackgroundPage.Rooms) {
                    pg.widgets.map { w -> WidgetDisplay(
                        w,
                        bgPage.executor,
                        Modifier.fillMaxWidth(),
                        onOpenMenu = { onOpenMenu(it) },
                        onError = { onError(it) }
                    ) }
                }
                is BottomSheetPage.AddHouseRef -> ModifyHouseRef(pg.houseRef.value, {pg.houseRef.value = it}) {
                    scope.launch {
                        ctx.settingsDataStore.updateData { settings ->
                            settings.toBuilder()
                                .addHouseRefs(it).build()
                        }
                        sheetState.hide()
                    }
                }
                is BottomSheetPage.EditHouseRef -> ModifyHouseRef(pg.houseRef.value, {pg.houseRef.value = it}) {
                    scope.launch {
                        ctx.settingsDataStore.updateData { settings ->
                            settings.toBuilder()
                                .removeHouseRefs(pg.index)
                                .addHouseRefs(it).build()
                        }
                        sheetState.hide()
                    }
                }
            }
        }
    ) {
        PagedBackdrop(
            menuState = menuState,
            page = page,
            backgroundPage = backgroundPage,
            backLayerContent = { pg ->
                Row(
                    Modifier.padding(start = 8.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button({ page = Page.Settings; scope.launch { menuState.conceal() } }) {
                        Text("Settings")
                    }
                    Crossfade(pg) {
                        if (it is BackgroundPage.Rooms) {
                            Button({
                                backgroundPage = BackgroundPage.Homes; page = Page.EmptyGreeter
                            }) {
                                Text("Exit house")
                            }
                        }
                    }
                }
                Crossfade(pg) {
                    when (pg) {
                        is BackgroundPage.Homes -> HousesMenu(houseRefs.value,
                            { e -> onError(e) }) { newHouse ->
                            backgroundPage = BackgroundPage.Rooms(
                                newHouse,
                                OpenControllerLibExecutor(newHouse)
                            )
                            page = Page.HomeGreeter(newHouse)
                        }
                        is BackgroundPage.Rooms -> RoomsMenu(pg.house) {
                            page = Page.Controller(it)
                            scope.launch { menuState.conceal() }
                        }
                    }
                }
            },
            frontLayerContent = {
                when (it) {
                    is Page.EmptyGreeter -> EmptyGreeterView(
                        onRevealMenu = { scope.launch { menuState.reveal() } },
                        onAddHome = { scope.launch {
                            sheetPage = BottomSheetPage.AddHouseRef(mutableStateOf(HouseRef.getDefaultInstance()))
                            sheetState.show()
                        }}
                    )
                    is Page.HomeGreeter -> HomeGreeterView(
                        house = it.house,
                        onRevealMenu = { scope.launch { menuState.reveal() } },
                        onExitHome = {
                            scope.launch {
                                page = Page.EmptyGreeter
                                menuState.reveal()
                                backgroundPage = BackgroundPage.Homes
                            }
                        }
                    )
                    is Page.Settings -> SettingsView(
                        onBottomSheetPage = { p ->
                            scope.launch {
                                sheetPage = p
                                sheetState.show()
                            }
                        }
                    )
                    is Page.Controller -> ControllerView(
                        it.controller,
                        (backgroundPage as BackgroundPage.Rooms).executor!!,
                        onOpenMenu = { w -> onOpenMenu(w) },
                        onError = { e -> onError(e) }
                    )
                }
            }
        )
    }
}