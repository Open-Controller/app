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
import com.pjtsearch.opencontroller.components.ExpandableListItem
import com.pjtsearch.opencontroller.ui.theme.OpenControllerTheme
import com.pjtsearch.opencontroller.ui.theme.typography
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import com.pjtsearch.opencontroller_lib.OpenController;
import com.pjtsearch.opencontroller.extensions.toList
import org.json.JSONArray
import org.json.JSONObject
import java.util.stream.Stream

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = OpenController("""{ "name": "Test house", "rooms": [ { "name": "Family Room", "controllers":[] }, { "name": "Test room", "controllers": [ { "name": "test", "widgets": [ { "type": "Button", "action": { "device": "test", "action": "Test" }, "icon": "icon", "text": "text" } ] } ] } ], "devices": [ { "id": "test", "actions": [ { "type": "HttpAction", "url": "http://example.com", "id": "Test", "method": "GET" }, { "type": "TcpAction", "address": "localhost:2000", "id": "TCP", "command": "test" } ], "dynamic_values": [ { "id": "Test", "resources": [ { "type": "Date" } ], "script": "date + 2" } ] } ] }""")
        val house = JSONObject(controller.toJson());
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.window.setDecorFitsSystemWindows(true)
            this.window.statusBarColor = Color.TRANSPARENT
            this.window.navigationBarColor = Color.TRANSPARENT
            this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            //if (MaterialTheme.colors.surface.luminance() > 0.5f) {
                this.window.decorView.systemUiVisibility = this.window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            //}

            //if (MaterialTheme.colors.surface.luminance() > 0.5f) {
                this.window.decorView.systemUiVisibility = this.window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            //}
        };*/
        setContent {
            SystemUi(this.window, house)
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun SystemUi(windows: Window, house: JSONObject) =
    OpenControllerTheme {
        windows.statusBarColor = Color.TRANSPARENT
        windows.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windows.setDecorFitsSystemWindows(false)
        }

        @Suppress("DEPRECATION")
        if (MaterialTheme.colors.background.luminance() > 0.5f) {
            windows.decorView.systemUiVisibility = windows.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        @Suppress("DEPRECATION")
        if (MaterialTheme.colors.background.luminance() > 0.5f) {
            windows.decorView.systemUiVisibility = windows.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        var room: String? by remember { mutableStateOf(null) }
        var menuOpen by mutableStateOf(rememberBackdropScaffoldState(BackdropValue.Concealed))
        ProvideWindowInsets {
            BackdropScaffold(
                    scaffoldState = menuOpen,
                    headerHeight = 100.dp,
                    modifier = Modifier.statusBarsPadding(),
                    backLayerBackgroundColor = MaterialTheme.colors.background,
                    frontLayerElevation = if (MaterialTheme.colors.isLight) 18.dp else 1.dp,
                    appBar = {
                        TopAppBar(
                                backgroundColor = MaterialTheme.colors.background,
                                elevation = 0.dp,
                                title = {
                                    Crossfade(current = menuOpen.isConcealed) {
                                        when (it) {
                                            true -> room?.let { Text(it, style = typography.h5) } ?: Text("Home", style = typography.h5)
                                            false -> Text("Menu", style = typography.h5)
                                        }
                                    }
                                }
                        )
                    },
                    backLayerContent = {
                        Column(modifier = Modifier.padding(10.dp).padding(bottom = 20.dp)) {
                            house.getJSONArray("rooms").toList()
                                ?.map { it.getString("name") }
                                ?.map { name ->
                                    ExpandableListItem(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp)
                                                .padding(start = 10.dp),
                                        text = { Text(name) }) {
                                        ListItem(text = { Text("test") }, modifier = Modifier.height(30.dp))
                                    }
                                }
                        }
                    },
                    frontLayerContent = {
                        Text(menuOpen.isConcealed.toString())
                    }
            )
        }
    }

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OpenControllerTheme {
        Greeting("Android")
    }
}