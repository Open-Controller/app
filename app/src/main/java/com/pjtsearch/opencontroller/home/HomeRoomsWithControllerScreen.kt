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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.pjtsearch.opencontroller.components.ControlledExpandableListItem
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
    onExit: () -> Unit,
    onError: (Throwable) -> Unit
) {
//    TODO: add appbar
    Row(
        Modifier.padding(
            top = WindowInsets.statusBars.only(WindowInsetsSides.Top).asPaddingValues()
                .calculateTopPadding()
        )
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.house?.rooms?.map { (roomId, room) ->
                item {
                    ControlledExpandableListItem(Modifier.padding(5.dp),
                        { Text(room.displayName) },
                        { OpenControllerIcon(room.icon, room.displayName) }) {
                        Row(
                            Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            room.controllers.map { (controllerId, controller) ->
                                ControllerButton(
                                    controller, controllerId, roomId, onSelectController
                                )
                            }
                        }
                    }
                }
            } ?: items(5) {
                ControlledExpandableListItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .placeholder(
                            visible = true,
                            color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f),
                            shape = CircleShape,
                            highlight = PlaceholderHighlight.shimmer(MaterialTheme.colorScheme.surfaceVariant),
                        ), { Text("Loading") }, { Text("Loading") }) {}
            }
        }

        Column(
            Modifier
                .padding(
                    bottom = WindowInsets.navigationBars
                        .only(WindowInsetsSides.Bottom)
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .weight(2f)
        ) {
            if (uiState is HomeUiState.HasController) {
                ControllerView(
                    uiState.selectedController, onError = onError
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