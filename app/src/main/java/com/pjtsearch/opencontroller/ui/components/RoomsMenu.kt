package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.ExpandableListItem
import com.pjtsearch.opencontroller.extensions.icons
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
                    ExpandableListItem(Modifier.fillMaxWidth().padding(5.dp),
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
                        }}) {
                        room.controllersList.map { controller ->
                            ListItem(
                                    text = { Text(controller.displayName) },
                                    modifier = Modifier
                                            .height(30.dp)
                                            .clickable {
                                                onControllerClick(controller)
                                            })
                        }
                    }
                }
    }