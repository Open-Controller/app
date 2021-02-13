package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.ExpandableListItem
import com.pjtsearch.opencontroller_lib.House

@ExperimentalAnimationApi
@Composable
fun RoomsMenu(house: House, onControllerClick: (Pair<String, String>) -> Unit) =
    Column(modifier = Modifier.padding(10.dp).padding(bottom = 20.dp).fillMaxHeight()) {
        house.rooms
                ?.map { room ->
                    ExpandableListItem(
                            modifier = Modifier.fillMaxWidth().padding(5.dp).padding(start = 10.dp),
                            text = { Text(room.name) }) {
                        room.controllers.map { controller ->
                            ListItem(
                                    text = { Text(controller.name) },
                                    modifier = Modifier
                                            .height(30.dp)
                                            .clickable {
                                                onControllerClick(Pair(room.name, controller.name))
                                            })
                        }
                    }
                }
    }