package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
        AnimatedVisibility(
            visible = menuState is ControllerMenuState.Open,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Surface(
                modifier = Modifier.weight(1f).padding(5.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(25.dp),
                tonalElevation = 1.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    menuState.items.map {
                        Widget(
                            it,
                            Modifier.fillMaxWidth(),
                            { items -> onInteractMenu(true, items) },
                            onError
                        )
                    }
                }
            }
        }
    }
}