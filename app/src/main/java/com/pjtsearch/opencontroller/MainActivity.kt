package com.pjtsearch.opencontroller

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.kittinunf.fuel.httpGet
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.extensions.HouseRef
import com.pjtsearch.opencontroller.extensions.copy
import com.pjtsearch.opencontroller.ui.theme.typography
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller.ui.components.AppBar
import com.pjtsearch.opencontroller.ui.components.ControllerView
import com.pjtsearch.opencontroller.ui.components.HousesMenu
import com.pjtsearch.opencontroller.ui.components.RoomsMenu
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.*
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
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

sealed class Page {
    object Home : Page()
    data class Controller(val controller: ControllerOrBuilder) : Page()
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainActivityView() {
    var house: HouseOrBuilder? by remember { mutableStateOf(null) }
    var page: Page by remember { mutableStateOf(Page.Home) }
    val menuState by mutableStateOf(rememberBackdropScaffoldState(BackdropValue.Concealed))
    val executor = remember(house) { house?.let { OpenControllerLibExecutor(it) } }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val houseRefs = listOf(HouseRef("http://10.0.2.105:3612/"))
    val onError = { err: Throwable ->
        scope.launch {
            val result = menuState.snackbarHostState.showSnackbar(err.message ?: "Unknown error occurred", "Copy")
            if (result == SnackbarResult.ActionPerformed) {
                copy(err.localizedMessage ?: "Unknown error", err.toString(), ctx)
            }
        }
    }
    BackdropScaffold(
        scaffoldState = menuState,
        headerHeight = 100.dp,
        modifier = Modifier.statusBarsPadding(),
        backLayerBackgroundColor = MaterialTheme.colors.background,
        frontLayerElevation = if (MaterialTheme.colors.isLight) 18.dp else 1.dp,
        frontLayerShape = shapes.large,
        appBar = {
            AppBar(
                menuState = menuState,
                concealedTitle = {
                    when (page) {
                        is Page.Home -> Text("Home", style = typography.h5)
                        is Page.Controller -> Text(
                            (page as Page.Controller).controller.name,
                            style = typography.h5
                        )
                    }
                },
                revealedTitle = { Text("Menu", style = typography.h5) }
            )
        },
        backLayerContent = {
            Box(
                Modifier
                    .padding(10.dp)
                    .padding(bottom = 20.dp)) {
                house?.let { house ->
                    RoomsMenu(house) {
                        page = Page.Controller(it)
                        menuState.conceal()
                    }
                } ?: HousesMenu(houseRefs, { onError(it) }) { house = it }
            }
        },
        frontLayerContent = {
            Box(Modifier.padding(20.dp)) {
                Crossfade(menuState.targetValue) {
                    when (it) {
                        BackdropValue.Concealed -> when (page) {
                            is Page.Home -> Text("Home", style = typography.h5)
                            is Page.Controller -> ControllerView(
                                    (page as Page.Controller).controller,
                                    executor!!,
                                    onError = { e -> onError(e) }
                                )
                        }
                        BackdropValue.Revealed -> Box(Modifier.fillMaxWidth()) {
                            when (page) {
                                is Page.Home -> Text("Home", style = typography.h5)
                                is Page.Controller -> Text(
                                    (page as Page.Controller).controller.name,
                                    style = typography.h5
                                )
                            }
                            Icon(
                                Icons.Outlined.KeyboardArrowUp,
                                "Close menu",
                                modifier = Modifier.align(alignment = Alignment.CenterEnd))
                        }
                    }
                }
            }
        }
    )
}