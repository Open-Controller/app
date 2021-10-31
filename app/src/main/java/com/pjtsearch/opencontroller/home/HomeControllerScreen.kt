package com.pjtsearch.opencontroller.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.pjtsearch.opencontroller.Controller
import com.pjtsearch.opencontroller.Device
import com.pjtsearch.opencontroller.ui.components.ControllerView

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeControllerScreen(
    controller: Controller,
    houseScope: Map<String, Device>,
    isExpandedScreen: Boolean,
    onBack: () -> Unit
) = Column(modifier = Modifier.systemBarsPadding()) {
    ControllerView(
        controller,
        houseScope,
        onError = { TODO() }
    )
}