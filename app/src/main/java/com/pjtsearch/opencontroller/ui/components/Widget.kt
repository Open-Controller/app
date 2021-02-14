package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
            remember { executor.subscribeDynamicValue(widget.dynamicText.value) {value = it} }
            Text(value.toString())
        }
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
    }