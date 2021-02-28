package com.pjtsearch.opencontroller

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.kittinunf.fuel.httpGet
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.extensions.HouseRef
import com.pjtsearch.opencontroller.extensions.NetworkHouseRef
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
import com.pjtsearch.opencontroller_lib_proto.Controller as ProtoController

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
    abstract val title: String

    object Home : Page() { override val title = "Home"}
    object Settings : Page() { override val title = "Settings"}
    data class Controller(val controller: ProtoController) : Page() { override val title: String = controller.name }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainActivityView() {
    var house: House? by rememberSaveable(
        saver = Saver({it.value?.toByteArray()}, {mutableStateOf(House.parseFrom(it))})
    ) { mutableStateOf(null) }
    var page: Page by rememberSaveable(
        saver = Saver({
          when (val page = it.value) {
              is Page.Home -> listOf("Home")
              is Page.Settings -> listOf("Settings")
              is Page.Controller -> listOf("Controller", page.controller.toByteArray())
          }
        }, {
            when (it[0]) {
                "Home" -> mutableStateOf(Page.Home)
                "Controller" -> mutableStateOf(Page.Controller(ProtoController.parseFrom(it[1] as ByteArray)))
                "Settings" -> mutableStateOf(Page.Settings)
                else -> mutableStateOf(Page.Home)
            }
        })
    ) { mutableStateOf(Page.Home) }
    val menuState by mutableStateOf(rememberBackdropScaffoldState(BackdropValue.Concealed))
    val executor = remember(house) { house?.let { OpenControllerLibExecutor(it) } }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val houseRefs = listOf(NetworkHouseRef("Home","http://10.0.2.105:3612/"))
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
                concealedTitle = { Text(page.title, style = typography.h5) },
                revealedTitle = { Text("Menu", style = typography.h5) }
            )
        },
        backLayerContent = {
            Column(
                Modifier
                    .padding(10.dp)
                    .padding(bottom = 20.dp)) {
                Row(Modifier.padding(start = 8.dp, bottom = 20.dp)) {
                    Button({ page = Page.Settings; menuState.conceal() }) {
                        Text("Settings")
                    }
                    Crossfade(house) {
                        if (it != null) {
                            Button({ house = null; page = Page.Home }, Modifier.padding(start = 8.dp)) {
                                Text("Exit house")
                            }
                        }
                    }
                }
                Crossfade(house) {
                    it?.let { house ->
                        RoomsMenu(house) {
                            page = Page.Controller(it)
                            menuState.conceal()
                        }
                    } ?: HousesMenu(houseRefs, { e -> onError(e) }) { newHouse -> house = newHouse }
                }
            }
        },
        frontLayerContent = {
            Box(Modifier.padding(20.dp)) {
                Crossfade(menuState.targetValue) {
                    when (it) {
                        BackdropValue.Concealed -> when (val page = page) {
                            is Page.Home -> Text("Home", style = typography.h5)
                            is Page.Settings -> Text("Settings", style = typography.h5)
                            is Page.Controller -> ControllerView(
                                    page.controller,
                                    executor!!,
                                    onError = { e -> onError(e) }
                                )
                        }
                        BackdropValue.Revealed -> Box(Modifier.fillMaxWidth()) {
                            Text(page.title, style = typography.h5)
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