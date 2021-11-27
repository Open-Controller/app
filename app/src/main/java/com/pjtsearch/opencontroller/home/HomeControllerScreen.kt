package com.pjtsearch.opencontroller.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.pjtsearch.opencontroller.Controller
import com.pjtsearch.opencontroller.Device
import com.pjtsearch.opencontroller.components.CenterBar
import com.pjtsearch.opencontroller.components.SmallIconButton
import com.pjtsearch.opencontroller.ui.components.ControllerView

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeControllerScreen(
    controller: Controller,
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
    onError: (Throwable) -> Unit
) = Scaffold(
        topBar = {
            CenterBar(
                title = { Text(controller.displayName) },
                navigationIcon = {
                    SmallIconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Exit house"
                        )
                    }
                },
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.statusBars,
                    applyStart = true,
                    applyTop = true,
                    applyEnd = true,
                )
            )
        },
        content = { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                ControllerView(
                    controller,
                    onError = onError
                )
            }
        }
    )
