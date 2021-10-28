package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.Device
import com.pjtsearch.opencontroller.Fn
import com.pjtsearch.opencontroller.Widget
import com.pjtsearch.opencontroller.extensions.DirectionVector
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.ui.theme.shapes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun ColumnScope.Widget(
    widget: Widget,
    houseScope: Map<String, Device>,
    modifier: Modifier = Modifier,
    onOpenMenu: (List<Widget>) -> Unit,
    onError: (Throwable) -> Unit
) {
    fun callParam(paramName: String, vararg params: Any?) {
        GlobalScope.launch {
            (widget.params[paramName] as Fn)(params.toList(), houseScope)
        }
    }

    val view = LocalView.current
    val sizedModifier = if (widget.expand) {
        modifier.weight(1f, false)
    } else modifier
    when (widget.widgetType) {
        "button" ->
            OpenControllerButton(
                sizedModifier,
                widget.params["text"] as String,
                widget.params["icon"] as String?,
                widget.params["size"] as Int?
            ) {
                callParam("onClick")
            }
        "row" -> Row(sizedModifier, Arrangement.SpaceBetween) {
            widget.children.map {
                this@Widget.Widget(it, houseScope, onOpenMenu = onOpenMenu, onError = onError)
            }
        }
        "column" -> Column(sizedModifier, Arrangement.Top) {
            widget.children.map {
                Widget(it, houseScope, onOpenMenu = onOpenMenu, onError = onError)
            }
        }
        "arrowlayout" -> Column(sizedModifier, Arrangement.Top) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                this@Widget.Widget(
                    widget.params["top"] as Widget,
                    houseScope,
                    onOpenMenu = onOpenMenu,
                    onError = onError
                )
            }
            Row {
                this@Widget.Widget(
                    widget.params["left"] as Widget,
                    houseScope,
                    onOpenMenu = onOpenMenu,
                    onError = onError
                )
                this@Widget.Widget(
                    widget.params["center"] as Widget,
                    houseScope,
                    onOpenMenu = onOpenMenu,
                    onError = onError
                )
                this@Widget.Widget(
                    widget.params["right"] as Widget,
                    houseScope,
                    onOpenMenu = onOpenMenu,
                    onError = onError
                )
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                this@Widget.Widget(
                    widget.params["bottom"] as Widget,
                    houseScope,
                    onOpenMenu = onOpenMenu,
                    onError = onError
                )
            }
        }
        "swipepad" -> Column(
            sizedModifier.background(
                MaterialTheme.colors.secondary.copy(alpha = 0.07f), shapes.large
            )
        ) {
            SwipePad(
                if (widget.expand)
                    Modifier
                        .weight(1f, true)
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
                            .padding(8.dp)
                            .then(
                                if (widget.expand) Modifier.fillMaxWidth() else Modifier.defaultMinSize(
                                    200.dp,
                                    10.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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
        "space" -> Spacer(sizedModifier)
        "menubutton" ->
            OpenControllerButton(
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
            callParam("onInput")
        }
        null -> Text("Widget type must be set")
    }
}