package com.pjtsearch.opencontroller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.ui.theme.typography
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import com.pjtsearch.opencontroller_lib.OpenController
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller.ui.components.AppBar
import com.pjtsearch.opencontroller.ui.components.ControllerView
import com.pjtsearch.opencontroller.ui.components.RoomsMenu
import com.pjtsearch.opencontroller_lib.Controller
import com.pjtsearch.opencontroller_lib.House

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val instance = OpenController("""{ "name": "Test house", "rooms": [ { "name": "Family Room", "controllers":[] }, { "name": "Test room", "controllers": [ { "name": "test", "widgets": [ { "type": "Button", "action": { "device": "test", "action": "Test" }, "icon": "icon", "text": "text" } ] } ] } ], "devices": [ { "id": "test", "actions": [ { "type": "HttpAction", "url": "http://example.com", "id": "Test", "method": "GET" }, { "type": "TcpAction", "address": "localhost:2000", "id": "TCP", "command": "test" } ], "dynamic_values": [ { "id": "Test", "resources": [ { "type": "Date" } ], "script": "date + 2" } ] } ] }""")
        val house = instance.getHouse()
        setContent {
            SystemUi(this.window) {
                MainActivityView(house!!, instance)
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainActivityView(house: House, instance: OpenController) {
    var selectedController: Controller? by remember { mutableStateOf(null) }
    var menuState by mutableStateOf(rememberBackdropScaffoldState(BackdropValue.Concealed))
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
                    concealedTitle = { selectedController?.let { Text(it.name, style = typography.h5) }
                            ?: Text("Home", style = typography.h5) },
                    revealedTitle = { Text("Menu", style = typography.h5) }
                )
            },
            backLayerContent = {
                RoomsMenu(house) {
                    selectedController = it
                    menuState.conceal()
                }
            },
            frontLayerContent = {
                Crossfade(current = menuState.targetValue) {
                    when (it) {
                        BackdropValue.Concealed ->
                            selectedController?.let { it1 -> ControllerView(it1, instance) }
                                ?:Text("Home", style = typography.h5)
                        BackdropValue.Revealed ->
                            selectedController?.let { controller -> Text(controller.name, style = typography.h5) }
                                    ?: Text("Home", style = typography.h5)
                    }
                }
            }
    )
}