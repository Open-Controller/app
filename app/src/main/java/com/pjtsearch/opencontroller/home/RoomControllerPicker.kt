/*
 * Copyright (c) 2022 PJTSearch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.pjtsearch.opencontroller.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.pjtsearch.opencontroller.components.ControlledExpandableListItem
import com.pjtsearch.opencontroller.components.LargeTabButton
import com.pjtsearch.opencontroller.components.MediumTabButton
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.Room
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.ui.theme.typography

private sealed interface DialogState {
    object Closed : DialogState
    data class Opened(
        val controllers: List<Controller>,
        val roomId: String,
        val displayName: String
    ) : DialogState
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoomControllerPicker(
    rooms: List<Room>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onSelectController: (Pair<String, String>) -> Unit
) {
//    TODO: Saveable?
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.Closed) }
    BoxWithConstraints {
        val state = dialogState
        LazyVerticalGrid(
            columns = GridCells.Fixed(maxOf((maxWidth / 150.dp).toInt(), 1)),
            contentPadding = contentPadding,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rooms.map { room ->
                item {
                    AnimatedVisibility(
                        visible = !(state is DialogState.Opened && state.roomId == room.id),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        LargeTabButton(
                            icon = { OpenControllerIcon(room.icon, room.displayName) },
                            clickAndSemanticsModifier = Modifier.clickable {
                                dialogState =
                                    DialogState.Opened(
                                        room.controllers,
                                        room.id,
                                        room.displayName
                                    )
                            },
                            expandable = true
                        ) {
                            Text(room.displayName)
                        }
                    }
                }
            }
        }
        if (state is DialogState.Opened) {
            AlertDialog(
                onDismissRequest = {
                    dialogState = DialogState.Closed
                },
                title = { Text(state.displayName) },
                text = {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.controllers.map { controller ->
                            item {
                                ControllerButton(
                                    controller,
                                    controller.id,
                                    state.roomId
                                ) {
                                    dialogState =
                                        DialogState.Closed;
                                    onSelectController(it)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { dialogState = DialogState.Closed }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun ControllerButton(
    controller: Controller,
    controllerId: String,
    roomId: String,
    onSelectController: (Pair<String, String>) -> Unit
) =
    (controller.brandColor?.let {
        Color(
            android.graphics.Color.parseColor(
                controller.brandColor
            )
        )
    } ?: MaterialTheme.colorScheme.secondary).let { color ->
        MediumTabButton(
            clickAndSemanticsModifier = Modifier.clickable {
                onSelectController(
                    Pair(
                        roomId,
                        controllerId
                    )
                )
            },
            color = color,
            icon = {}
        ) {
            Text(controller.displayName)
        }
    }

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoomsLoading(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) =
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(5) {
            ControlledExpandableListItem(
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f),
                        shape = CircleShape,
                        highlight = PlaceholderHighlight.shimmer(MaterialTheme.colorScheme.surfaceVariant),
                    ),
                { Text("Loading") },
                { Text("Loading") }) {}
        }
    }

@Composable
fun RoomsErrorLoading(
    error: Throwable,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) =
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Outlined.ErrorOutline,
            "Error Loading",
            Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally),
            MaterialTheme.colorScheme.primary,
        )
        Button(onClick = { onReload() }) {
            Text("Reload")
        }
        Text(
            text = "Error Loading: " + (error.localizedMessage
                ?: "Unknown error"),
            style = typography.headlineSmall
        )
        Surface(
            tonalElevation = 6.dp,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            LazyColumn(Modifier.fillMaxHeight(), contentPadding = PaddingValues(10.dp)) {
                item {
                    Text(text = error.stackTraceToString())
                }
            }
        }
    }