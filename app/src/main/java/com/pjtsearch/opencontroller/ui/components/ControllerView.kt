package com.pjtsearch.opencontroller.ui.components

import androidx.compose.runtime.Composable
import com.pjtsearch.opencontroller_lib_proto.ControllerOrBuilder
import com.pjtsearch.opencontroller_lib_proto.HouseOrBuilder

@Composable
fun ControllerView(controller: ControllerOrBuilder, house: HouseOrBuilder) =
    controller.widgetsList.map {
        Widget(it, house)
    }