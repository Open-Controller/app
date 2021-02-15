package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientView
import androidx.compose.ui.platform.ContextAmbient
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase
import kotlin.concurrent.thread

@Composable
fun Widget(widget: WidgetOrBuilder, executor: OpenControllerLibExecutor, modifier: Modifier = Modifier) {
    val view = AmbientView.current
    when (widget.innerCase) {
        InnerCase.BUTTON -> Button(
                modifier = modifier,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    thread {
                        executor.executeAction(widget.button.action)
                    }
                }) {
            Text(widget.button.text)
        }
        InnerCase.DYNAMIC_TEXT -> {
            var value: Any? by remember { mutableStateOf(null) }
            DisposableEffect(widget.dynamicText.value) {
                val unsub = executor.subscribeDynamicValue(widget.dynamicText.value) { value = it }
                onDispose { unsub() }
            }
            Text(value.toString(), modifier)
        }
        InnerCase.ROW -> Row(modifier, Arrangement.SpaceBetween) {
            widget.row.childList.map {
                Widget(it, executor)
            }
        }
        InnerCase.COLUMN -> Column(modifier, Arrangement.Top) {
            widget.column.childList.map {
                Widget(it, executor)
            }
        }
        InnerCase.ARROW_LAYOUT -> Column(modifier, Arrangement.Top) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Widget(widget.arrowLayout.top, executor)
            }
            Row {
                Widget(widget.arrowLayout.left, executor)
                Widget(widget.arrowLayout.center, executor)
                Widget(widget.arrowLayout.right, executor)
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Widget(widget.arrowLayout.bottom, executor)
            }
        }
        InnerCase.SPACE -> Spacer(modifier)
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
    }
}