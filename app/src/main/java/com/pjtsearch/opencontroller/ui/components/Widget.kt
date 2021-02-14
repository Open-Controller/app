package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase
import kotlin.concurrent.thread

@Composable
fun Widget(widget: WidgetOrBuilder, executor: OpenControllerLibExecutor) =
    when (widget.innerCase) {
        InnerCase.BUTTON -> Button(
            onClick = {
                thread {
                    executor.executeAction(widget.button.action)
                }
            }) {
            Text(widget.button.text)
        }
        InnerCase.DYNAMIC_TEXT -> {
            var value: Any? by remember { mutableStateOf(null) }
            DisposableEffect(widget.dynamicText.value) {
                val unsub = executor.subscribeDynamicValue(widget.dynamicText.value) {value = it}
                onDispose { unsub() }
            }
            Text(value.toString())
        }
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
    }