package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.pjtsearch.opencontroller_lib.Controller
import com.pjtsearch.opencontroller_lib.OpenController

@Composable
fun ControllerView(controller: Controller, instance: OpenController) =
    controller.widgets.map {
        Widget(it, instance)
    }