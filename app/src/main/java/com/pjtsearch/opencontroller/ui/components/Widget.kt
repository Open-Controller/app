package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.mapError
import com.pjtsearch.opencontroller.extensions.DirectionVector
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.extensions.icons
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.TextInputAction
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase
import kotlin.concurrent.thread

@Composable
fun ColumnScope.Widget(widget: WidgetOrBuilder, executor: OpenControllerLibExecutor, modifier: Modifier = Modifier, onError: (Throwable) -> Unit) {
    val view = LocalView.current
    val sizedModifier = if (widget.expand) {
        modifier.weight(1f, false)
    } else modifier
    when (widget.innerCase) {
        InnerCase.BUTTON ->
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.secondary) {
                Box(
                    sizedModifier
                        .width(65.dp)
                        .height(65.dp)
                        .padding(5.dp)
                        .clip(shapes.medium)
                        .background(
                            MaterialTheme.colors.secondary.copy(alpha = 0.07f),
                            shapes.medium
                        )
                        .clickable(role = Role.Button) {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            thread {
                                executor
                                    .executeLambda(widget.button.onClick, listOf())
                                    .mapError(onError)
                            }
                        }, Alignment.Center
                ) {
                    when (widget.button.hasIcon()) {
                        true -> OpenControllerIcon(widget.button.icon, widget.button.text)
                        false -> Text(widget.button.text)
                    }
                }
            }
        InnerCase.ROW -> Row(sizedModifier, Arrangement.SpaceBetween) {
            widget.row.childrenList.map {
                this@Widget.Widget(it, executor, onError = onError)
            }
        }
        InnerCase.COLUMN -> Column(sizedModifier, Arrangement.Top) {
            widget.column.childrenList.map {
                Widget(it, executor, onError = onError)
            }
        }
        InnerCase.ARROW_LAYOUT -> Column(sizedModifier, Arrangement.Top) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                this@Widget.Widget(widget.arrowLayout.top, executor, onError = onError)
            }
            Row {
                this@Widget.Widget(widget.arrowLayout.left, executor, onError = onError)
                this@Widget.Widget(widget.arrowLayout.center, executor, onError = onError)
                this@Widget.Widget(widget.arrowLayout.right, executor, onError = onError)
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                this@Widget.Widget(widget.arrowLayout.bottom, executor, onError = onError)
            }
        }
        InnerCase.SWIPE_PAD -> Column(
            sizedModifier.background(
                MaterialTheme.colors.secondary.copy(alpha = 0.07f), shapes.small
            )
        ) {
            SwipePad(
                if (widget.expand)
                    Modifier.weight(1f, true)
                else Modifier.defaultMinSize(200.dp, 200.dp)
            ) {
                val lambda = when (it) {
                    is DirectionVector.Down -> widget.swipePad.onSwipeDown
                    is DirectionVector.Left -> widget.swipePad.onSwipeLeft
                    is DirectionVector.Right -> widget.swipePad.onSwipeRight
                    is DirectionVector.Up -> widget.swipePad.onSwipeUp
                    DirectionVector.Zero -> widget.swipePad.onClick
                }
                thread {
                    executor
                        .executeLambda(lambda, listOf())
                        .mapError(onError)
                }
            }
            if (widget.swipePad.hasOnBottomDecrease() && widget.swipePad.hasOnBottomIncrease()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = {
                        thread {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            executor.executeLambda(widget.swipePad.onBottomDecrease, listOf())
                                .mapError(onError)
                        }
                    }) {
                        OpenControllerIcon(widget.swipePad.bottomDecreaseIcon, "Decrease")
                    }
                    IconButton(onClick = {
                        thread {
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
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
        InnerCase.TEXT_INPUT -> BasicTextField("", {
            thread {
                executor.executeLambda(
                    widget.textInput.onInput,
                    listOf(TextInputAction.newBuilder().setChar(it.last().toString()).build())
                )
            }
        })
    }
}