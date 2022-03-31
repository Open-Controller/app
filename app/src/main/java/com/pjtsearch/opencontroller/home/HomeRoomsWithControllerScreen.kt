package com.pjtsearch.opencontroller.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.ui.components.ControllerView

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun HomeRoomsWithControllerScreen(
    uiState: HomeUiState,
    onSelectController: (Pair<String, String>) -> Unit,
    onInteractWithControllerMenu: (open: Boolean, items: List<Widget>) -> Unit,
    onExit: () -> Unit,
    onError: (Throwable) -> Unit
) {
    Row(
        Modifier.padding(
            WindowInsets.systemBars.asPaddingValues()
        )
    ) {
        Surface(
            modifier = Modifier
                .weight(3f)
                .padding(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(25.dp)
        ) {
            Column(Modifier.padding(10.dp)) {
                IconButton(
                    onClick = { onExit() },
                    modifier = Modifier.padding(5.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                ) {
                    Icon(Icons.Outlined.ArrowBack, "Exit Home")
                }
                RoomControllerPicker(
                    uiState.house?.rooms,
                    modifier = Modifier
                        .fillMaxHeight(),
                    onSelectController = onSelectController
                )
            }
        }
        Column(Modifier.weight(5f)) {
            if (uiState is HomeUiState.HasController) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(uiState.selectedController.displayName) },
                        )
                    },
                    content = { innerPadding ->
                        Column(
                            Modifier
                                .padding(innerPadding)
                        ) {
                            ControllerView(
                                uiState.selectedController, onError = onError,
                                menuState = uiState.controllerMenuState,
                                onInteractMenu = onInteractWithControllerMenu
                            )
                        }
                    }
                )
            } else {
                Text(
                    text = "No Controller",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}