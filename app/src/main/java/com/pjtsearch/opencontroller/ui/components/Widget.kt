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
import com.pjtsearch.opencontroller.extensions.icons
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.TextInputAction
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase
import kotlin.concurrent.thread

@Composable
fun Widget(widget: WidgetOrBuilder, executor: OpenControllerLibExecutor, modifier: Modifier = Modifier, onError: (Throwable) -> Unit) {
    val view = LocalView.current
    when (widget.innerCase) {
        InnerCase.BUTTON ->
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {
                Box(
                    Modifier
                        .widthIn(65.dp, 70.dp)
                        .heightIn(65.dp, 75.dp)
                        .padding(5.dp)
                        .clip(shapes.medium)
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.07f), shapes.medium)
                        .clickable(role = Role.Button) {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            thread {
                                executor
                                    .executeLambda(widget.button.onClick, listOf())
                                    .mapError(onError)
                            }
                        }, Alignment.Center) {
                    when (widget.button.hasIcon()) {
                        true -> when (val icon = icons[widget.button.icon] ?: throw Error("Could not find icon " + widget.button.icon.toString())) {
                            is Int -> Icon(
                                painterResource(icon),
                                widget.button.text
                            )
                            is ImageVector -> Icon(
                                icon,
                                widget.button.text
                            )
                        }
                        false -> Text(widget.button.text)
                    }
                }
            }
        InnerCase.ROW -> Row(modifier, Arrangement.SpaceBetween) {
            widget.row.childrenList.map {
                Widget(it, executor, onError = onError)
            }
        }
        InnerCase.COLUMN -> Column(modifier, Arrangement.Top) {
            widget.column.childrenList.map {
                Widget(it, executor, onError = onError)
            }
        }
        InnerCase.ARROW_LAYOUT -> Column(modifier, Arrangement.Top) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Widget(widget.arrowLayout.top, executor, onError = onError)
            }
            Row {
                Widget(widget.arrowLayout.left, executor, onError = onError)
                Widget(widget.arrowLayout.center, executor, onError = onError)
                Widget(widget.arrowLayout.right, executor, onError = onError)
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Widget(widget.arrowLayout.bottom, executor, onError = onError)
            }
        }
        InnerCase.SPACE -> Spacer(modifier)
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
        InnerCase.TEXT_INPUT -> BasicTextField("", {
            thread {
                executor.executeLambda(widget.textInput.onInput,
                    listOf(TextInputAction.newBuilder().setChar(it.last().toString()).build()))
            }
        })
    }
}