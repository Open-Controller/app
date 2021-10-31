package com.pjtsearch.opencontroller.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.pjtsearch.opencontroller.Controller
import com.pjtsearch.opencontroller.House
import com.pjtsearch.opencontroller.components.ControlledExpandableListItem
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.ui.theme.shapes

@OptIn(ExperimentalAnimationApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun HomeRoomsScreen(house: House?, isLoading: Boolean, onSelectController: (Pair<String, String>) -> Unit) =
    Column(modifier = Modifier.fillMaxHeight().systemBarsPadding()) {
        house?.rooms?.map { (roomId, room) ->
            ControlledExpandableListItem(
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                { Text(room.displayName) },
                { OpenControllerIcon(room.icon, room.displayName) }) {
                Row(
                    Modifier
                        .clip(shapes.small)
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
        } ?: Text("Loading")
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
    } ?: MaterialTheme.colors.secondary).let { color ->
        Button(
            elevation = ButtonDefaults.elevation(0.dp, 0.dp),
            modifier = Modifier
                .width(120.dp)
                .height(100.dp),
            onClick = { onSelectController(Pair(roomId, controllerId)) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = color,
                contentColor = if (color.luminance() < 0.3) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black
            )
        ) {
            Text(controller.displayName)
        }
    }