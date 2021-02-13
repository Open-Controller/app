package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.gson.JsonObject
import com.pjtsearch.opencontroller_lib.OpenController

@Composable
fun Widget(widget: JsonObject, instance: OpenController) =
    when (widget["type"].asString) {
        "Button" -> Button(
            onClick = {
                instance.executeAction(
                    widget["action"].asJsonObject["device"].asString,
                    widget["action"].asJsonObject["action"].asString
                )
            }) {
            Text(widget["text"].asString)
        }
        else -> Text("Widget type " + widget["type"].asString + " not supported")
    }