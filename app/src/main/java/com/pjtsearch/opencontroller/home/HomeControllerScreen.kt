package com.pjtsearch.opencontroller.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.CenterAlignedTopAppBarWithPadding
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.ui.components.ControllerView

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeControllerScreen(
    controller: Controller,
    onBack: () -> Unit,
    onError: (Throwable) -> Unit,
    onInteractWithControllerMenu: (open: Boolean, items: List<Widget>) -> Unit,
    controllerMenuState: ControllerMenuState
) =
    Scaffold(
        topBar = {
            CenterAlignedTopAppBarWithPadding(
                title = { Text(controller.displayName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Exit house"
                        )
                    }
                },
                contentPadding = WindowInsets.statusBars.exclude(
                    WindowInsets.statusBars.only(
                        WindowInsetsSides.Bottom
                    )
                ).asPaddingValues()
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .padding(start = 8.dp, end = 8.dp)
                    .padding(
                        bottom = WindowInsets.navigationBars
                            .only(WindowInsetsSides.Bottom)
                            .asPaddingValues()
                            .calculateBottomPadding()
                    )) {
                ControllerView(
                    controller,
                    onError = onError,
                    menuState = controllerMenuState,
                    onInteractMenu = onInteractWithControllerMenu
                )
            }
        }
    )

