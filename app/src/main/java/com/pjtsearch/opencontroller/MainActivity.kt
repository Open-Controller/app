package com.pjtsearch.opencontroller

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.pjtsearch.opencontroller.components.ExpandableListItem
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.ui.theme.OpenControllerTheme
import com.pjtsearch.opencontroller.ui.theme.typography
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import com.pjtsearch.opencontroller_lib.OpenController;
import com.pjtsearch.opencontroller.extensions.toList
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller.ui.components.AppBar
import com.pjtsearch.opencontroller.ui.components.RoomsMenu
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder
import java.util.stream.Stream

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = OpenController("""{ "name": "Test house", "rooms": [ { "name": "Family Room", "controllers":[] }, { "name": "Test room", "controllers": [ { "name": "test", "widgets": [ { "type": "Button", "action": { "device": "test", "action": "Test" }, "icon": "icon", "text": "text" } ] } ] } ], "devices": [ { "id": "test", "actions": [ { "type": "HttpAction", "url": "http://example.com", "id": "Test", "method": "GET" }, { "type": "TcpAction", "address": "localhost:2000", "id": "TCP", "command": "test" } ], "dynamic_values": [ { "id": "Test", "resources": [ { "type": "Date" } ], "script": "date + 2" } ] } ] }""")
        val house = Parser.default().parse(rawValue = StringBuilder(controller.toJson())) as JsonObject;
        setContent {
            SystemUi(this.window) {
                MainActivityView(house)
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainActivityView(house: JsonObject) {
    var selectedController: Pair<String, String>? by remember { mutableStateOf(null) }
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
                    concealedTitle = { selectedController?.let { Text(it.second, style = typography.h5) }
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
                        BackdropValue.Concealed -> Text("Controller", style = typography.h5)
                        BackdropValue.Revealed -> {
                            selectedController?.let { Text(it.second, style = typography.h5) }
                                    ?: Text("Home", style = typography.h5)
                        }
                    }
                }
            }
    )
}