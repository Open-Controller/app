package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.ControllerOrBuilder

@Composable
fun ControllerView(controller: ControllerOrBuilder, executor: OpenControllerLibExecutor, onError: (Throwable) -> Unit) =
    Column(Modifier.fillMaxSize()) {
        controller.widgetsList.map {
            Widget(it, executor, Modifier.fillMaxWidth().padding(bottom = 10.dp), onError)
        }
    }