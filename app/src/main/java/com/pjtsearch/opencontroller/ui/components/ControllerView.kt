package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.home.ControllerMenuState

@ExperimentalComposeUiApi
@Composable
fun ControllerView(
    controller: Controller,
    onError: (Throwable) -> Unit,
    menuState: ControllerMenuState,
    onInteractMenu: (open: Boolean, items: List<Widget>) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(0.1f)) {
            controller.displayInterface?.widgets?.map {
                Widget(
                    it,
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    { items -> onInteractMenu(menuState is ControllerMenuState.Closed, items) },
                    onError
                )
            }
        }
        AnimatedVisibility(visible = menuState is ControllerMenuState.Open) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.weight(1f)
            ) {
                menuState.items.map {
                    Widget(
                        it,
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        { items -> onInteractMenu(true, items) },
                        onError
                    )
                }
            }
        }
    }
}