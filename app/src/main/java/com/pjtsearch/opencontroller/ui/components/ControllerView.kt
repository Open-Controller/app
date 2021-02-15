package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.ControllerOrBuilder

@Composable
fun ControllerView(controller: ControllerOrBuilder, executor: OpenControllerLibExecutor) =
    Column(Modifier.fillMaxWidth()) {
        controller.widgetList.map {
            Widget(it, executor, Modifier.fillMaxWidth())
        }
    }