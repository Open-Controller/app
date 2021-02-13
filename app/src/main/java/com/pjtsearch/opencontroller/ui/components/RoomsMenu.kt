package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonValue
import com.pjtsearch.opencontroller.components.ExpandableListItem
import com.pjtsearch.opencontroller.extensions.toList
import org.json.JSONObject

@ExperimentalAnimationApi
@Composable
fun RoomsMenu(house: JsonObject, onControllerClick: (Pair<String, String>) -> Unit) =
    Column(modifier = Modifier.padding(10.dp).padding(bottom = 20.dp).fillMaxHeight()) {
        (house["rooms"] as JsonArray<JsonObject>)
                ?.map { room ->
                    ExpandableListItem(
                            modifier = Modifier.fillMaxWidth().padding(5.dp).padding(start = 10.dp),
                            text = { Text(room["name"] as String) }) {
                        (room["controllers"] as JsonArray<JsonObject>).map { controller ->
                            ListItem(
                                    text = { Text(controller["name"] as String) },
                                    modifier = Modifier
                                            .height(30.dp)
                                            .clickable {
                                                onControllerClick(Pair(room["name"] as String, controller["name"] as String))
                                            })
                        }
                    }
                }
    }