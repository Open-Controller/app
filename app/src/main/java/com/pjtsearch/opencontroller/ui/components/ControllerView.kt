package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.Controller
import com.pjtsearch.opencontroller.Device
import com.pjtsearch.opencontroller.Widget

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalComposeUiApi
@Composable
fun ControllerView(
    controller: Controller,
    onError: (Throwable) -> Unit
) {
    var menuItems: List<Widget> by remember { mutableStateOf(listOf()) }
    var menuOpen by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(0.1f)) {
            controller.displayInterface?.widgets?.map {
                Widget(
                    it,
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    { items -> menuOpen = !menuOpen; menuItems = items },
                    onError
                )
            }
        }
        AnimatedVisibility(visible = menuOpen) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(1f)) {
                menuItems.map {
                    Widget(
                        it,
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        { items -> menuItems = items },
                        onError
                    )
                }
            }
        }
    }
}