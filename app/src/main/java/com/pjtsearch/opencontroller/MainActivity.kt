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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.extensions.SettingsSerializer
import com.pjtsearch.opencontroller.extensions.copy
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.ui.components.*
import com.pjtsearch.opencontroller.const.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "Settings.proto",
    serializer = SettingsSerializer
)

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SystemUi(this.window) {
                MainActivityView()
            }
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
    val navController = rememberNavController()
    val houseRefs = ctx.settingsDataStore.data.map {
        it.houseRefsList
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: Destinations.HOUSES_ROUTE

    val isExpandedScreen = false

    val onError = { err: Throwable ->
        scope.launch {
            err.printStackTrace()
//            val result = menuState.snackbarHostState.showSnackbar(err.message ?: "Unknown error occurred", "Copy")
//            if (result == SnackbarResult.ActionPerformed) {
                copy(err.localizedMessage ?: "Unknown error", err.stackTraceToString(), ctx)
//            }
        }
    }

    val onOpenMenu = { widgets: List<Widget> ->
        scope.launch {
//            pageBackStack.push(page.copy(
//                bottomSheetPage = BottomSheetPage.Widgets(widgets),
//                bottomSheetValue = ModalBottomSheetValue.HalfExpanded
//            ))
        }
    }

    val onAddHouseRef = { ref: HouseRef ->
        scope.launch {
            ctx.settingsDataStore.updateData { settings ->
                settings.toBuilder()
                    .addHouseRefs(ref).build()
            }
//            sheetState.hide()
        }
    }

    val onEditHouseRef = { ref: HouseRef, index: Int ->
        scope.launch {
            ctx.settingsDataStore.updateData { settings ->
                settings.toBuilder()
                    .removeHouseRefs(index)
                    .addHouseRefs(ref).build()
            }
//            sheetState.hide()
        }
    }

    val onAddHome = {
        scope.launch {
//            pageBackStack.push(page.copy(
//                bottomSheetPage = BottomSheetPage.AddHouseRef(mutableStateOf(HouseRef.getDefaultInstance())),
//                bottomSheetValue = ModalBottomSheetValue.HalfExpanded
//            ))
        }
    }

    val onExitHome = {
        scope.launch {
//            pageBackStack.push(
//                page.copy(FrontPage.EmptyGreeter, BackgroundPage.Homes, backdropValue = BackdropValue.Revealed)
//            )
        }
    }

    val onRevealMenu = {
//        scope.launch { menuState.reveal() }
    }

    val onBottomSheetPage = { p: BottomSheetPage ->
        scope.launch {
//            pageBackStack.push(
//                page.copy(bottomSheetPage = p, bottomSheetValue = ModalBottomSheetValue.HalfExpanded)
//            )
        }
    }

//    DisposableEffect(page) {
//        scope.launch { menuState.animateTo(page.backdropValue) }
//        scope.launch { sheetState.animateTo(page.bottomSheetValue) }
//        onDispose {  }
//    }
//
//    // TODO: Refactor
//    DisposableEffect(menuState.targetValue) {
//        if (menuState.targetValue != page.backdropValue) {
//            pageBackStack.push(page.copy(backdropValue = menuState.targetValue))
//        }
//        onDispose {  }
//    }
//
//    // TODO: Refactor
//    DisposableEffect(sheetState.targetValue) {
//        if (sheetState.targetValue != page.bottomSheetValue) {
//            pageBackStack.push(page.copy(bottomSheetValue = sheetState.targetValue))
//        }
//        onDispose {  }
//    }

//    DialogSheet(state = dialogState) {
//        BottomSheet(
//            sheetState,
//            page,
//            onOpenMenu = { onOpenMenu(it) },
//            onError = { onError(it) },
//            onAddHouseRef = { onAddHouseRef(it) },
//            onEditHouseRef = { h, i -> onEditHouseRef(h, i) }
//        ) {
//            Backdrop(
//                menuState,
//                page,
//                pageBackStack,
//                houseRefs = houseRefs.value,
//                onOpenMenu = { onOpenMenu(it) },
//                onError = { onError(it) },
//                onAddHome = { onAddHome() },
//                onExitHome = { onExitHome() },
//                onRevealMenu = { onRevealMenu() },
//                onBottomSheetPage = { onBottomSheetPage(it) }
//            )
//        }
//    }

    NavigationGraph(
        isExpandedScreen = isExpandedScreen,
        navController = navController,
//        openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
    )
}
