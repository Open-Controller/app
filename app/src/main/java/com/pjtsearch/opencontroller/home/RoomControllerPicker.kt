package com.pjtsearch.opencontroller.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.pjtsearch.opencontroller.components.ControlledExpandableListItem
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.Room
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.ui.theme.typography

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoomControllerPicker(
    rooms: List<Room>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onSelectController: (Pair<String, String>) -> Unit
) =
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rooms.map { room ->
            item {
                ControlledExpandableListItem(
                    Modifier
                        .fillMaxWidth(),
                    { Text(room.displayName) },
                    { OpenControllerIcon(room.icon, room.displayName) }) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        room.controllers.map { controller ->
                            ControllerButton(
                                controller,
                                controller.id,
                                room.id,
                                onSelectController
                            )
                        }
                    }
                }
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
        Button(
            modifier = Modifier
                .width(130.dp)
                .height(100.dp),
            shape = RoundedCornerShape(15.dp),
            onClick = { onSelectController(Pair(roomId, controllerId)) },
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = contentColorFor(backgroundColor = color)
            )
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
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) =
    Column(
        modifier = modifier,
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
            shape = RoundedCornerShape(25.dp)
        ) {
            LazyColumn(Modifier.fillMaxHeight(), contentPadding = PaddingValues(10.dp)) {
                item {
                    Text(text = error.stackTraceToString())
                }
            }
        }
    }