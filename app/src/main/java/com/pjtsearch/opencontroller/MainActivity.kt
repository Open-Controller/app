package com.pjtsearch.opencontroller

import android.os.Bundle
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
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import com.github.kittinunf.fuel.httpGet
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.ui.theme.typography
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller.ui.components.AppBar
import com.pjtsearch.opencontroller.ui.components.ControllerView
import com.pjtsearch.opencontroller.ui.components.RoomsMenu
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.*
import kotlin.concurrent.thread

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btn = Widget.newBuilder().setButton(Button.newBuilder()
                .setText("a")
                .setIcon(Icon.STAR)
                .setAction(ActionRef.newBuilder().setDevice("Test").setAction("macro"))
        )
//        val instance = OpenController("""{ "name": "Test house", "rooms": [ { "name": "Family Room", "controllers":[] }, { "name": "Test room", "controllers": [ { "name": "test", "widgets": [ { "type": "Button", "action": { "device": "test", "action": "Test" }, "icon": "icon", "text": "text" } ] } ] } ], "devices": [ { "id": "test", "actions": [ { "type": "HttpAction", "url": "http://example.com", "id": "Test", "method": "GET" }, { "type": "TcpAction", "address": "localhost:2000", "id": "TCP", "command": "test" } ], "dynamic_values": [ { "id": "Test", "resources": [ { "type": "Date" } ], "script": "date + 2" } ] } ] }""")
//        val house = instance.getHouse()

//        val house = House.newBuilder()
//                    .setName("Test house")
//                    .addRoom(Room.newBuilder()
//                        .setName("Test room")
//                        .addController(Controller.newBuilder()
//                            .setName("test")
//                            .addWidget(Widget.newBuilder().setRow(Row.newBuilder()
//                                    .addChild(btn)
//                                    .addChild(Widget.newBuilder().setRow(Row.newBuilder()
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                    ))
//                            ))
//                            .addWidget(Widget.newBuilder().setRow(Row.newBuilder()
//                                    .addChild(Widget.newBuilder().setColumn(Column.newBuilder()
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                    ))
//                                    .addChild(Widget.newBuilder().setArrowLayout(ArrowLayout.newBuilder()
//                                            .setTop(btn)
//                                            .setBottom(btn)
//                                            .setLeft(btn)
//                                            .setRight(btn)
//                                            .setCenter(btn)
//                                    ))
//                                    .addChild(Widget.newBuilder().setColumn(Column.newBuilder()
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                    ))
//                            ))
//                            .addWidget(Widget.newBuilder().setRow(Row.newBuilder()
//                                    .addChild(Widget.newBuilder().setColumn(Column.newBuilder()
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                    ))
//                                    .addChild(Widget.newBuilder().setColumn(Column.newBuilder()
//                                            .addChild(Widget.newBuilder().setRow(Row.newBuilder()
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                            ))
//                                            .addChild(Widget.newBuilder().setRow(Row.newBuilder()
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                            ))
//                                    ))
//                                    .addChild(Widget.newBuilder().setColumn(Column.newBuilder()
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                    ))
//                            ))
//                            .addWidget(Widget.newBuilder().setRow(Row.newBuilder()
//                                    .addChild(Widget.newBuilder().setColumn(Column.newBuilder()
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                    ))
//                                    .addChild(Widget.newBuilder().setColumn(Column.newBuilder()
//                                            .addChild(Widget.newBuilder().setRow(Row.newBuilder()
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                            ))
//                                            .addChild(Widget.newBuilder().setRow(Row.newBuilder()
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                            ))
//                                            .addChild(Widget.newBuilder().setRow(Row.newBuilder()
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                            ))
//                                            .addChild(Widget.newBuilder().setRow(Row.newBuilder()
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                                    .addChild(btn)
//                                            ))
//                                    ))
//                                    .addChild(Widget.newBuilder().setColumn(Column.newBuilder()
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                            .addChild(btn)
//                                    ))
//                            ))
//                        ))
//                    .addDevice(Device.newBuilder()
//                        .setId("Test")
//                        .addAction(Action.newBuilder()
//                            .setId("test")
//                            .setHttpAction(HttpAction.newBuilder()
//                            .setMethod(HttpMethod.GET)
//                            .setUrl("https://example.com")
//                        ))
//                        .addAction(Action.newBuilder()
//                            .setId("tcp")
//                            .setTcpAction(TCPAction.newBuilder()
//                                .setAddress("10.0.2.105:2000")
//                                .setCommand("test")
//                        ))
//                        .addAction(Action.newBuilder()
//                            .setId("tcpDelay")
//                            .setDelayAction(DelayAction.newBuilder().setTime(3000)))
//                        .addAction(Action.newBuilder()
//                            .setId("macro")
//                            .setMacroAction(MacroAction.newBuilder()
//                                .addAction(ActionRef.newBuilder().setDevice("Test").setAction("tcpDelay"))
//                                .addAction(ActionRef.newBuilder().setDevice("Test").setAction("tcp"))
//                            ))
//                        .addDynamicValue(DynamicValue.newBuilder()
//                            .setId("test")
//                            .addDynamicResource(DynamicResource.newBuilder()
//                                .setDateResource(DateResource.newBuilder()))
//                            .setScript("return date .. ':test'")
//                        )
//                    )
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
    var menuState by mutableStateOf(rememberBackdropScaffoldState(BackdropValue.Concealed))
    var executor = remember(house) { house?.let { OpenControllerLibExecutor(it) } }
    SideEffect { thread {
        house = House.parseFrom("http://10.0.2.105:3612/".httpGet().response().third.get())
    }}
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
                house?.let {
                    RoomsMenu(it) {
                        page = Page.Controller(it)
                        menuState.conceal()
                    }
                }
            }
        },
        frontLayerContent = {
            Box(Modifier.padding(20.dp)) {
                Crossfade(current = menuState.targetValue) {
                    when (it) {
                        BackdropValue.Concealed -> when (page) {
                            is Page.Home -> Text("Home", style = typography.h5)
                            is Page.Controller -> ControllerView(
                                    (page as Page.Controller).controller,
                                    executor!!
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