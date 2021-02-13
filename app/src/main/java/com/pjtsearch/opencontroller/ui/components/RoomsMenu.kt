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
import com.pjtsearch.opencontroller.extensions.toList
import org.json.JSONObject

@ExperimentalAnimationApi
@Composable
fun RoomsMenu(house: JSONObject, onControllerClick: (List<String>) -> Unit) =
    Column(modifier = Modifier.padding(10.dp).padding(bottom = 20.dp).fillMaxHeight()) {
        house.getJSONArray("rooms").toList()
                ?.map { room ->
                    ExpandableListItem(
                            modifier = Modifier.fillMaxWidth().padding(5.dp).padding(start = 10.dp),
                            text = { Text(room.getString("name")) }) {
                        room.getJSONArray("controllers").toList().map { controller ->
                            ListItem(
                                    text = { Text(controller.getString("name")) },
                                    modifier = Modifier
                                            .height(30.dp)
                                            .clickable {
                                                onControllerClick(listOf(room.getString("name"), controller.getString("name")))
                                            })
                        }
                    }
                }
    }