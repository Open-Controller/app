package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.pjtsearch.opencontroller_lib_android.executeAction
import com.pjtsearch.opencontroller_lib_proto.HouseOrBuilder
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

@Composable
fun Widget(widget: WidgetOrBuilder, house: HouseOrBuilder) =
    when (widget.innerCase) {
        InnerCase.BUTTON -> Button(
            onClick = {
                thread {
                    executeAction(widget.button.action, house)
                }
            }) {
            Text(widget.button.text)
        }
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
    }