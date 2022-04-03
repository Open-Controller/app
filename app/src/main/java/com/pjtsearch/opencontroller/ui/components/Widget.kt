package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.pjtsearch.opencontroller.executor.Fn
import com.pjtsearch.opencontroller.executor.Panic
import com.pjtsearch.opencontroller.executor.StackCtx
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.extensions.DirectionVector
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
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
        "swipepad" -> Surface(
            modifier = sizedModifier.weight(1f, true),
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(40.dp)
        ) {
            Box {
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
                if (widget.params["onBottomIncrease"] != null && widget.params["onBottomDecrease"] != null) {
                    Row(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(10.dp)
                            .then(
                                if (widget.params["expand"] as Boolean? == true) Modifier.fillMaxWidth() else Modifier.defaultMinSize(
                                    200.dp,
                                    10.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                            Box(
                                modifier = Modifier
                                    .combinedClickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        onLongClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                                            callParam("onBottomHold")
                                        },
                                        onClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                            callParam("onBottomDecrease")
                                        },
                                        indication = rememberRipple(true, 32.dp)
                                    )
                                    .clip(CircleShape)
                                    .size(64.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OpenControllerIcon(
                                    widget.params["bottomDecreaseIcon"] as String,
                                    "Decrease",
                                    1
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .combinedClickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                            callParam("onBottomIncrease")
                                        },
                                        indication = rememberRipple(true, 32.dp)
                                    )
                                    .clip(CircleShape)
                                    .size(64.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OpenControllerIcon(
                                    widget.params["bottomIncreaseIcon"] as String,
                                    "Increase",
                                    1
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