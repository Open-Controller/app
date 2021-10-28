package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.Controller
import com.pjtsearch.opencontroller.House
import com.pjtsearch.opencontroller.components.ControlledExpandableListItem
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.ui.theme.shapes
import android.graphics.Color as AndroidColor

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun RoomsMenu(house: House, onControllerClick: (Controller) -> Unit) =
    Column(modifier = Modifier.fillMaxHeight()) {
        house.rooms.map { (_, room) ->
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
                    room.controllers.map { (_, controller) ->
                        ControllerButton(
                            controller,
                            onControllerClick
                        )
                    }
                }
            }
        }
    }

@Composable
fun ControllerButton(controller: Controller, onControllerClick: (Controller) -> Unit) =
    (controller.brandColor?.let {
        Color(
            AndroidColor.parseColor(
                controller.brandColor
            )
        )
    } ?: MaterialTheme.colors.secondary).let { color ->
        Button(
            elevation = ButtonDefaults.elevation(0.dp, 0.dp),
            modifier = Modifier
                .width(120.dp)
                .height(100.dp),
            onClick = { onControllerClick(controller) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = color,
                contentColor = if (color.luminance() < 0.3) Color.White else Color.Black
            )
        ) {
            Text(controller.displayName)
        }
    }
