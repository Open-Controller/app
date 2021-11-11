package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.Controller
import com.pjtsearch.opencontroller.Device

@ExperimentalComposeUiApi
@Composable
fun ControllerView(
    controller: Controller,
    onError: (Throwable) -> Unit
) =
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        controller.displayInterface?.widgets?.map {
            Widget(
                it,
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                onError
            )
        }
    }