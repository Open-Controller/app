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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.backpress.LocalBackPressHandler
import com.github.zsoltk.compose.router.Router
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.extensions.SettingsSerializer
import com.pjtsearch.opencontroller.extensions.copy
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.ui.components.*
import com.pjtsearch.opencontroller.ui.components.Widget as WidgetDisplay
import com.google.accompanist.systemuicontroller.LocalSystemUiController
import com.google.accompanist.systemuicontroller.rememberAndroidSystemUiController
import com.pjtsearch.opencontroller.components.DialogSheet
import com.pjtsearch.opencontroller.components.PagedBackdrop
import com.pjtsearch.opencontroller.components.PagedBottomSheet
import com.pjtsearch.opencontroller.const.*
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "Settings.proto",
    serializer = SettingsSerializer
)

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    private val backPressHandler = BackPressHandler()

    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val controller = rememberAndroidSystemUiController()
            CompositionLocalProvider(LocalSystemUiController provides controller,
                LocalBackPressHandler provides backPressHandler) {
                SystemUi(this.window) {
                    MainActivityView()
                }
            }
        }
    }
    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainActivityView() {
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

    val dialogState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

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

    DialogSheet(state = dialogState) {
        Router("Page",
            PageState(FrontPage.EmptyGreeter, BackgroundPage.Homes, true)) { pageBackStack ->
            val page = pageBackStack.last()


            DisposableEffect(page) {
                if (page.concealed)
                    scope.launch { menuState.conceal() }
                else
                    scope.launch { menuState.reveal() }
                onDispose {  }
            }

            // TODO: Refactor
            DisposableEffect(menuState.targetValue) {
                if (menuState.targetValue.equals(BackdropValue.Concealed) != page.concealed) {
                    pageBackStack.push(page.copy(concealed = menuState.targetValue.equals(BackdropValue.Concealed)))
                }
                onDispose {  }
            }
            PagedBottomSheet(
                state = sheetState,
                page = sheetPage,
                sheetContent = { pg ->
                    val bgPage = page.backgroundPage
                    when (pg) {
                        is BottomSheetPage.Widgets -> if (bgPage is BackgroundPage.Rooms) {
                            pg.widgets.map { w ->
                                WidgetDisplay(
                                    w,
                                    bgPage.executor,
                                    Modifier.fillMaxWidth(),
                                    onOpenMenu = { onOpenMenu(it) },
                                    onError = { onError(it) }
                                )
                            }
                        }
                        is BottomSheetPage.AddHouseRef -> ModifyHouseRef(
                            pg.houseRef.value,
                            { pg.houseRef.value = it }) {
                            scope.launch {
                                ctx.settingsDataStore.updateData { settings ->
                                    settings.toBuilder()
                                        .addHouseRefs(it).build()
                                }
                                sheetState.hide()
                            }
                        }
                        is BottomSheetPage.EditHouseRef -> ModifyHouseRef(
                            pg.houseRef.value,
                            { pg.houseRef.value = it }) {
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
                    backLayerContent = {
                        Row(
                            Modifier.padding(start = 8.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button({
                                pageBackStack.push(page.copy(frontPage = FrontPage.Settings, concealed = true))
                            }) {
                                Text("Settings")
                            }
                            Button({
                                pageBackStack.pop()
                            }) {
                                Text("Back")
                            }
                            Crossfade(page.backgroundPage) {
                                if (it is BackgroundPage.Rooms) {
                                    Button({
                                        pageBackStack.push(page.copy(FrontPage.EmptyGreeter, BackgroundPage.Homes))
                                    }) {
                                        Text("Exit house")
                                    }
                                }
                            }
                        }
                        Crossfade(page.backgroundPage) {
                            when (it) {
                                is BackgroundPage.Homes -> HousesMenu(houseRefs.value,
                                    { e -> onError(e) }) { newHouse ->
                                    pageBackStack.push(page.copy(FrontPage.HomeGreeter(newHouse), BackgroundPage.Rooms(
                                        newHouse,
                                        OpenControllerLibExecutor(newHouse)
                                    )))
                                }
                                is BackgroundPage.Rooms -> RoomsMenu(it.house) { controller ->
                                    pageBackStack.push(page.copy(FrontPage.Controller(controller), concealed = true))
                                }
                            }
                        }
                    },
                    frontLayerContent = {
                        when (val it = page.frontPage) {
                            is FrontPage.EmptyGreeter -> EmptyGreeterView(
                                onRevealMenu = { scope.launch { menuState.reveal() } },
                                onAddHome = {
                                    scope.launch {
                                        sheetPage =
                                            BottomSheetPage.AddHouseRef(mutableStateOf(HouseRef.getDefaultInstance()))
                                        sheetState.show()
                                    }
                                }
                            )
                            is FrontPage.HomeGreeter -> HomeGreeterView(
                                house = it.house,
                                onRevealMenu = { scope.launch { menuState.reveal() } },
                                onExitHome = {
                                    scope.launch {
                                        pageBackStack.push(
                                            page.copy(FrontPage.EmptyGreeter, BackgroundPage.Homes, false)
                                        )
                                    }
                                }
                            )
                            is FrontPage.Settings -> SettingsView(
                                onBottomSheetPage = { p ->
                                    scope.launch {
                                        sheetPage = p
                                        sheetState.show()
                                    }
                                }
                            )
                            is FrontPage.Controller -> ControllerView(
                                it.controller,
                                (page.backgroundPage as BackgroundPage.Rooms).executor,
                                onOpenMenu = { w -> onOpenMenu(w) },
                                onError = { e -> onError(e) }
                            )
                        }
                    }
                )
            }
        }
    }
}