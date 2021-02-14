package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
    }