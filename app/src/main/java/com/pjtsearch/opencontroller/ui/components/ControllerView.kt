package com.pjtsearch.opencontroller.ui.components

import androidx.compose.runtime.Composable
import com.pjtsearch.opencontroller_lib.OpenController
import com.pjtsearch.opencontroller_lib_proto.ControllerOrBuilder

@Composable
fun ControllerView(controller: ControllerOrBuilder) =
    controller.widgetsList.map {
        Widget(it)
    }