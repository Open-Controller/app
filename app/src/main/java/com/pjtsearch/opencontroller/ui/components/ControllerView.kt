package com.pjtsearch.opencontroller.ui.components

import androidx.compose.runtime.Composable
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.ControllerOrBuilder

@Composable
fun ControllerView(controller: ControllerOrBuilder, executor: OpenControllerLibExecutor) =
    controller.widgetList.map {
        Widget(it, executor)
    }