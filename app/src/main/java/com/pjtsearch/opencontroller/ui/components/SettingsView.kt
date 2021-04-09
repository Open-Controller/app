package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.ControlledExpandableListItem
import com.pjtsearch.opencontroller.components.ExpandableListItem
import com.pjtsearch.opencontroller.const.BottomSheetPage
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.NetworkHouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.theme.shapes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun SettingsView(onBottomSheetPage: (BottomSheetPage) -> Unit) {
    val ctx = LocalContext.current
    val settings = ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scope = rememberCoroutineScope()
    Column {
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
    }
}