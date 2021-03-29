package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.ExpandableListItem
import com.pjtsearch.opencontroller.extensions.icons
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller_lib_proto.Controller
import com.pjtsearch.opencontroller_lib_proto.ControllerOrBuilder
import com.pjtsearch.opencontroller_lib_proto.HouseOrBuilder

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun RoomsMenu(house: HouseOrBuilder, onControllerClick: (Controller) -> Unit) =
    Column(modifier = Modifier.fillMaxHeight()) {
        house.roomsList
                ?.map { room ->
                    ExpandableListItem(
                        Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        {Text(room.displayName)},
                        {when (val icon = icons[room.icon] ?: throw Error("Could not find icon " + room.icon.toString())) {
                            is Int -> Icon(
                                painterResource(icon),
                                room.displayName
                            )
                            is ImageVector -> Icon(
                                icon,
                                room.displayName
                            )
                        }}) { Row(Modifier.clip(shapes.small).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            room.controllersList.map { controller ->
                                Button(onClick = { onControllerClick(controller) },
                                       modifier = Modifier.width(120.dp).height(100.dp)) {
                                    Text(controller.displayName)
                                }
                            }
                        }
                    }
                }
    }