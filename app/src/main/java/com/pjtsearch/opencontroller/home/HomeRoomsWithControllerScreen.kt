package com.pjtsearch.opencontroller.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.pjtsearch.opencontroller.components.ControlledExpandableListItem
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
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
//    TODO: add appbar, move rooms column to shared component
    Row(
        Modifier.padding(
            top = WindowInsets.statusBars.only(WindowInsetsSides.Top).asPaddingValues()
                .calculateTopPadding()
        )
    ) {
        RoomControllerPicker(
            uiState.house?.rooms,
            modifier = Modifier.weight(3f),
            onSelectController = onSelectController
        )

        Column(
            Modifier
                .padding(
                    bottom = WindowInsets.navigationBars
                        .only(WindowInsetsSides.Bottom)
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .weight(5f)
        ) {
            if (uiState is HomeUiState.HasController) {
                ControllerView(
                    uiState.selectedController, onError = onError,
                    menuState = uiState.controllerMenuState,
                    onInteractMenu = onInteractWithControllerMenu
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