package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase
import kotlin.concurrent.thread

@Composable
fun Widget(widget: WidgetOrBuilder, executor: OpenControllerLibExecutor, modifier: Modifier = Modifier) {
    when (widget.innerCase) {
        InnerCase.BUTTON -> Button(
                modifier = modifier,
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
                val unsub = executor.subscribeDynamicValue(widget.dynamicText.value) { value = it }
                onDispose { unsub() }
            }
            Text(value.toString(), modifier)
        }
        InnerCase.ROW -> Row(modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            widget.row.childList.map {
                Widget(it, executor)
            }
        }
        InnerCase.COLUMN -> TODO()
        InnerCase.ARROW_LAYOUT -> TODO()
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
    }
}