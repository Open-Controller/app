package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore

@ExperimentalAnimationApi
@Composable
fun SettingsView() {
    val ctx = LocalContext.current
    val settings = ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scope = rememberCoroutineScope()
    Column {
        /*
        settings.value.houseRefsList.forEachIndexed { i, it ->
            ListItem(
                text = { Box(Modifier.fillMaxWidth()) {
                    Text(it.displayName, Modifier.align(Alignment.CenterStart))
                    IconButton(onClick = {
                        scope.launch {
                            ctx.settingsDataStore.updateData { settings ->
                                settings.toBuilder().removeHouseRefs(i).build()
                            }
                        }
                    },
                    Modifier.align(Alignment.CenterEnd)) {
                        Icon(Icons.Outlined.Delete, "Delete this house")
                    }
                }},
                modifier = Modifier.clickable {
                    onBottomSheetPage(BottomSheetPage.EditHouseRef(mutableStateOf(it), i))
                }
            )
        }
        ListItem(
            text = { Text("Add house") },
            icon = { Icon(Icons.Outlined.Add, "Add a house") },
            modifier = Modifier.clickable {
                onBottomSheetPage(BottomSheetPage.AddHouseRef(mutableStateOf(HouseRef.getDefaultInstance())))
            }
        )
        */
//        FIXME: Figure out
        Text("todo")
    }
}