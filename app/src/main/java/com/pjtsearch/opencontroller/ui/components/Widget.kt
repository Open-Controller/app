package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.executor.Fn
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.extensions.DirectionVector
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
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
    fun callParam(paramName: String, vararg params: Any) {
        GlobalScope.launch {
            (widget.params[paramName] as Fn)(params.toList())
        }
    }
    val view = LocalView.current
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
        "swipepad" -> Box(
            sizedModifier
                .padding(0.dp)
                .weight(1f, true)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(40.dp)
                )
        ) {
            SwipePad(
                if (widget.params["expand"] as Boolean? == true)
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                else Modifier.defaultMinSize(200.dp, 200.dp)
            ) {
                callParam(
                    when (it) {
                        is DirectionVector.Down -> "onSwipeDown"
                        is DirectionVector.Left -> "onSwipeLeft"
                        is DirectionVector.Right -> "onSwipeRight"
                        is DirectionVector.Up -> "onSwipeUp"
                        DirectionVector.Zero -> "onClick"
                    }
                )
            }
            widget.params["onBottomDecrease"]?.let {
                widget.params["onBottomIncrease"]?.let {
                    Row(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(15.dp)
                            .then(
                                if (widget.params["expand"] as Boolean? == true) Modifier.fillMaxWidth() else Modifier.defaultMinSize(
                                    200.dp,
                                    10.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer) {
                            IconButton(onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                callParam("onBottomDecrease")
                            }) {
                                OpenControllerIcon(
                                    widget.params["bottomDecreaseIcon"] as String,
                                    "Decrease"
                                )
                            }
                            IconButton(onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                callParam("onBottomIncrease")
                            }) {
                                OpenControllerIcon(
                                    widget.params["bottomIncreaseIcon"] as String,
                                    "Increase"
                                )
                            }
                        }
                    }
                }
            }
        }
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
//        FIXME: readd
//        "textinput" -> TextInput(
//            sizedModifier,
//            widget.params["text"] as String,
//            widget.params["icon"] as String,
//            widget.params["size"] as Int
//        ) {
//            callParam("onInput")
//        }
        null -> Text("Widget type must be set")
    }
}