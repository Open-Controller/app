package com.pjtsearch.opencontroller.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoomControllerPicker(
    rooms: Map<String, Room>?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onSelectController: (Pair<String, String>) -> Unit
) =
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rooms?.map { (roomId, room) ->
            item {
                ControlledExpandableListItem(
                    Modifier
                        .fillMaxWidth(),
                    { Text(room.displayName) },
                    { OpenControllerIcon(room.icon, room.displayName) }) {
                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        room.controllers.map { (controllerId, controller) ->
                            ControllerButton(
                                controller,
                                controllerId,
                                roomId,
                                onSelectController
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
                    ),
                { Text("Loading") },
                { Text("Loading") }) {}
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
                .width(120.dp)
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