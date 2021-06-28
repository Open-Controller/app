package com.pjtsearch.opencontroller

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.backpress.LocalBackPressHandler
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.extensions.SettingsSerializer
import com.pjtsearch.opencontroller.extensions.copy
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.ui.components.*
import com.pjtsearch.opencontroller.components.DialogSheet
import com.pjtsearch.opencontroller.const.*
import com.pjtsearch.opencontroller_lib_proto.*
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
            CompositionLocalProvider(LocalBackPressHandler provides backPressHandler) {
                Navigator(
                    "Page",
                    PageState(
                        FrontPage.EmptyGreeter,
                        BackgroundPage.Homes,
                        BottomSheetPage.Widgets(listOf()),
                        BackdropValue.Concealed,
                        ModalBottomSheetValue.Hidden
                    )
                ) {
                    SystemUi(this.window) {
                        MainActivityView()
                    }
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
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val (page, pageBackStack) = LocalRoute.current
    val houseRefs = ctx.settingsDataStore.data.map {
        it.houseRefsList
    }.collectAsState(listOf())

    val menuState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
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
            pageBackStack.push(page.copy(
                bottomSheetPage = BottomSheetPage.Widgets(widgets),
                bottomSheetValue = ModalBottomSheetValue.HalfExpanded
            ))
        }
    }

    val onAddHouseRef = { ref: HouseRef ->
        scope.launch {
            ctx.settingsDataStore.updateData { settings ->
                settings.toBuilder()
                    .addHouseRefs(ref).build()
            }
            sheetState.hide()
        }
    }

    val onEditHouseRef = { ref: HouseRef, index: Int ->
        scope.launch {
            ctx.settingsDataStore.updateData { settings ->
                settings.toBuilder()
                    .removeHouseRefs(index)
                    .addHouseRefs(ref).build()
            }
            sheetState.hide()
        }
    }

    val onAddHome = {
        scope.launch {
            pageBackStack.push(page.copy(
                bottomSheetPage = BottomSheetPage.AddHouseRef(mutableStateOf(HouseRef.getDefaultInstance())),
                bottomSheetValue = ModalBottomSheetValue.HalfExpanded
            ))
        }
    }

    val onExitHome = {
        scope.launch {
            pageBackStack.push(
                page.copy(FrontPage.EmptyGreeter, BackgroundPage.Homes, backdropValue = BackdropValue.Revealed)
            )
        }
    }

    val onRevealMenu = {
        scope.launch { menuState.reveal() }
    }

    val onBottomSheetPage = { p: BottomSheetPage ->
        scope.launch {
            pageBackStack.push(
                page.copy(bottomSheetPage = p, bottomSheetValue = ModalBottomSheetValue.HalfExpanded)
            )
        }
    }

    DisposableEffect(page) {
        scope.launch { menuState.animateTo(page.backdropValue) }
        scope.launch { sheetState.animateTo(page.bottomSheetValue) }
        onDispose {  }
    }

    // TODO: Refactor
    DisposableEffect(menuState.targetValue) {
        if (menuState.targetValue != page.backdropValue) {
            pageBackStack.push(page.copy(backdropValue = menuState.targetValue))
        }
        onDispose {  }
    }

    // TODO: Refactor
    DisposableEffect(sheetState.targetValue) {
        if (sheetState.targetValue != page.bottomSheetValue) {
            pageBackStack.push(page.copy(bottomSheetValue = sheetState.targetValue))
        }
        onDispose {  }
    }

    DialogSheet(state = dialogState) {
        BottomSheet(
            sheetState,
            page,
            onOpenMenu = { onOpenMenu(it) },
            onError = { onError(it) },
            onAddHouseRef = { onAddHouseRef(it) },
            onEditHouseRef = { h, i -> onEditHouseRef(h, i) }
        ) {
            Backdrop(
                menuState,
                page,
                pageBackStack,
                houseRefs = houseRefs.value,
                onOpenMenu = { onOpenMenu(it) },
                onError = { onError(it) },
                onAddHome = { onAddHome() },
                onExitHome = { onExitHome() },
                onRevealMenu = { onRevealMenu() },
                onBottomSheetPage = { onBottomSheetPage(it) }
            )
        }
    }
}
