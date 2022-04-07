package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.github.michaelbull.result.Err
import com.pjtsearch.opencontroller.executor.Fn
import com.pjtsearch.opencontroller.executor.Panic
import com.pjtsearch.opencontroller.executor.Widget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@ExperimentalComposeUiApi
@Composable
fun ColumnScope.Widget(
    widget: Widget,
    modifier: Modifier = Modifier,
    onOpenMenu: (List<Widget>) -> Unit,
    onError: (Throwable) -> Unit
) {
    val scope = rememberCoroutineScope()
    fun callParam(paramName: String, vararg params: Any) {
        scope.launch(Dispatchers.IO) {
            val param = widget.params[paramName] as Fn?
            if (param != null) {
                when (val result = param(params.toList())) {
                    is Err -> onError(result.error.asThrowable())
                    else -> {}
                }
            } else {
//                TODO: make a message
                onError(Panic.Type(listOf()).asThrowable())
            }
        }
    }

    val sizedModifier = if (widget.params["expand"] as Boolean? == true) {
        modifier.weight(1f, false)
    } else modifier
    when (widget.widgetType) {
        "reactive" -> {
            val state by (widget.params["observe"] as Flow<*>)
                .collectAsState(initial = null)
            state?.let {
                Column(sizedModifier, Arrangement.Top) {
                    this@Widget.Widget(
                        (widget.params["child"] as Fn)(listOf(it)) as Widget,
                        onError = onError,
                        onOpenMenu = onOpenMenu
                    )
                }
            }
        }
        "buttongroup" ->
            ControllerButtonGroup(
                modifier = sizedModifier,
                size = widget.params["size"] as Int?,
                buttons = widget.children.map { btn ->
                    check(btn.widgetType == "button")
                    ButtonItemParams(
                        btn.params["text"] as String,
                        btn.params["icon"] as String?,
                    ) {
                        GlobalScope.launch {
                            (btn.params["onClick"] as Fn)(listOf())
                        }
                    }
                }
            )
        "button" ->
            ControllerButton(
                sizedModifier,
                widget.params["text"] as String,
                widget.params["icon"] as String?,
                widget.params["size"] as Int?
            ) {
                callParam("onClick")
            }
        "row" -> Row(sizedModifier, Arrangement.SpaceBetween) {
            widget.children.map {
                this@Widget.Widget(it, onError = onError, onOpenMenu = onOpenMenu)
            }
        }
        "column" -> Column(sizedModifier, Arrangement.Top) {
            widget.children.map {
                Widget(it, onError = onError, onOpenMenu = onOpenMenu)
            }
        }
        "arrowlayout" -> Column(sizedModifier, Arrangement.Top) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                this@Widget.Widget(
                    widget.params["top"] as Widget,
                    onError = onError,
                    onOpenMenu = onOpenMenu
                )
            }
            Row {
                this@Widget.Widget(
                    widget.params["left"] as Widget,
                    onError = onError,
                    onOpenMenu = onOpenMenu
                )
                this@Widget.Widget(
                    widget.params["center"] as Widget,
                    onError = onError,
                    onOpenMenu = onOpenMenu
                )
                this@Widget.Widget(
                    widget.params["right"] as Widget,
                    onError = onError,
                    onOpenMenu = onOpenMenu
                )
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                this@Widget.Widget(
                    widget.params["bottom"] as Widget,
                    onError = onError,
                    onOpenMenu = onOpenMenu
                )
            }
        }
        "swipepad" -> ControllerSwipePad(
            modifier = sizedModifier.weight(1f, true),
            expand = widget.params["expand"] as Boolean?,
            onBottomDecrease = widget.params["onBottomDecrease"]?.let {
                { callParam("onBottomDecrease") }
            },
            onBottomIncrease = widget.params["onBottomIncrease"]?.let {
                { callParam("onBottomIncrease") }
            },
            onBottomHold = { callParam("onBottomHold") },
            onSwipeDown = { callParam("onSwipeDown") },
            onSwipeUp = { callParam("onSwipeUp") },
            onSwipeLeft = { callParam("onSwipeLeft") },
            onSwipeRight = { callParam("onSwipeRight") },
            onClick = { callParam("onClick") },
            bottomDecreaseIcon = widget.params["bottomDecreaseIcon"] as String,
            bottomIncreaseIcon = widget.params["bottomIncreaseIcon"] as String,
        )
        "space" -> Spacer(sizedModifier)
        "menubutton" ->
            ControllerButton(
                sizedModifier,
                widget.params["text"] as String,
                widget.params["icon"] as String?,
                widget.params["size"] as Int?
            ) {
                onOpenMenu(widget.children)
            }
        "textinput" -> TextInput(
            sizedModifier,
            widget.params["text"] as String,
            widget.params["icon"] as String,
            widget.params["size"] as Int
        ) {
            println(it)
            callParam("onInput", it)
        }
        null -> Text("Widget type must be set")
    }
}