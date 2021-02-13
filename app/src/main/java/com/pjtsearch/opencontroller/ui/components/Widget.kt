package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.gson.JsonObject
import com.pjtsearch.opencontroller_lib_proto.WidgetOrBuilder
import com.pjtsearch.opencontroller_lib_proto.Widget.InnerCase

@Composable
fun Widget(widget: WidgetOrBuilder) =
    when (widget.innerCase) {
        InnerCase.BUTTON -> Button(
            onClick = {
//                instance.executeAction(
//                    widget["action"].asJsonObject["device"].asString,
//                    widget["action"].asJsonObject["action"].asString
//                )
            }) {
            Text(widget.button.text)
        }
        InnerCase.INNER_NOT_SET -> Text("Widget type must be set")
    }