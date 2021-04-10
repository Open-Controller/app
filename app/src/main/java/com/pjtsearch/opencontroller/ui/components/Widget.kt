package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.mapError
import com.pjtsearch.opencontroller.extensions.DirectionVector
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.Widget
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.Widget(
    widget: WidgetOrBuilder,
    executor: OpenControllerLibExecutor,
    modifier: Modifier = Modifier,
    onOpenMenu: (List<Widget>) -> Unit,
    onError: (Throwable) -> Unit
) {
    val view = LocalView.current
    val sizedModifier = if (widget.expand) {
        modifier.weight(1f, false)
    } else modifier
    when (widget.innerCase) {
        InnerCase.BUTTON ->
            OpenControllerButton(sizedModifier, widget.button.text, if (widget.button.hasIcon()) widget.button.icon else null, widget.button.size) {
                GlobalScope.launch {
                    executor
                        .executeLambda(widget.button.onClick, listOf())
                        .mapError(onError)
                }
            }
        InnerCase.ROW -> Row(sizedModifier, Arrangement.SpaceBetween) {
            widget.row.childrenList.map {
                this@Widget.Widget(it, executor, onOpenMenu = onOpenMenu, onError = onError)
            }
        }
        InnerCase.COLUMN -> Column(sizedModifier, Arrangement.Top) {
            widget.column.childrenList.map {
                Widget(it, executor, onOpenMenu = onOpenMenu, onError = onError)
            }
        }
        InnerCase.ARROW_LAYOUT -> Column(sizedModifier, Arrangement.Top) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                this@Widget.Widget(widget.arrowLayout.top, executor, onOpenMenu = onOpenMenu, onError = onError)
            }
            Row {
                this@Widget.Widget(widget.arrowLayout.left, executor, onOpenMenu = onOpenMenu, onError = onError)
                this@Widget.Widget(widget.arrowLayout.center, executor, onOpenMenu = onOpenMenu, onError = onError)
                this@Widget.Widget(widget.arrowLayout.right, executor, onOpenMenu = onOpenMenu, onError = onError)
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                this@Widget.Widget(widget.arrowLayout.bottom, executor, onOpenMenu = onOpenMenu, onError = onError)
            }
        }
        InnerCase.SWIPE_PAD -> Column(
            sizedModifier.background(
                MaterialTheme.colors.secondary.copy(alpha = 0.07f), shapes.large
            )
        ) {
            SwipePad(
                if (widget.expand)
                    Modifier.weight(1f, true).fillMaxWidth()
                else Modifier.defaultMinSize(200.dp, 200.dp)
            ) {
                val lambda = when (it) {
                    is DirectionVector.Down -> widget.swipePad.onSwipeDown
                    is DirectionVector.Left -> widget.swipePad.onSwipeLeft
                    is DirectionVector.Right -> widget.swipePad.onSwipeRight
                    is DirectionVector.Up -> widget.swipePad.onSwipeUp
                    DirectionVector.Zero -> widget.swipePad.onClick
                }
                GlobalScope.launch {
                    executor
                        .executeLambda(lambda, listOf())
                        .mapError(onError)
                }
            }
            if (widget.swipePad.hasOnBottomDecrease() && widget.swipePad.hasOnBottomIncrease()) {
                Row(Modifier.padding(8.dp)
                        .then(if (widget.expand) Modifier.fillMaxWidth() else Modifier.defaultMinSize(200.dp, 10.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = {
                        GlobalScope.launch {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            executor.executeLambda(widget.swipePad.onBottomDecrease, listOf())
                                .mapError(onError)
                        }
                    }) {
                        OpenControllerIcon(widget.swipePad.bottomDecreaseIcon, "Decrease")
                    }
                    IconButton(onClick = {
                        GlobalScope.launch {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            executor.executeLambda(widget.swipePad.onBottomIncrease, listOf())
                                .mapError(onError)
                        }
                    }) {
                        OpenControllerIcon(widget.swipePad.bottomIncreaseIcon, "Increase")
                    }
                }
            }
        }
        InnerCase.SPACE -> Spacer(sizedModifier)
        InnerCase.MENU_BUTTON ->
            OpenControllerButton(sizedModifier, widget.menuButton.text,
            if (widget.menuButton.hasIcon()) widget.menuButton.icon else null, widget.menuButton.size) {
                onOpenMenu(widget.menuButton.contentList)
            }
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
        InnerCase.TEXT_INPUT -> TextInput {
            GlobalScope.launch {
                executor.executeLambda(
                    widget.textInput.onInput,
                    listOf(it)
                )
            }
        }
    }
}