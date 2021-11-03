package com.pjtsearch.opencontroller.settingsui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.insets.systemBarsPadding
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.components.ModifyHouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settings.HouseRef
import kotlinx.coroutines.launch

@Composable
fun SettingsRoute() {
    val ctx = LocalContext.current
    val settings = ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scope = rememberCoroutineScope()
    var editing: Pair<Int, HouseRef>? by remember { mutableStateOf(null) }
    var adding: HouseRef? by remember { mutableStateOf(null) }

    Column(Modifier.systemBarsPadding()) {
        settings.value.houseRefsList.forEachIndexed { i, it ->
            FilledTonalButton(
                onClick = {
                    editing = Pair(i, it)
                }
            ) {
                Box(Modifier.fillMaxWidth()) {
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
                }
            }
        }
        FilledTonalButton(
            onClick = {
                adding = HouseRef.getDefaultInstance()
            }
        ) {
            Text("Add house")
            Icon(Icons.Outlined.Add, "Add a house")
        }
        if (editing != null) {
            AlertDialog(
                onDismissRequest = { editing = null },
                confirmButton = { Button(onClick = {
                    scope.launch {
                        val ed = editing!!
                        ctx.settingsDataStore.updateData { settings ->
                            settings.toBuilder()
                                .removeHouseRefs(ed.first)
                                .addHouseRefs(ed.second).build()
                        }
                        editing = null
                    }
                }) { Text("Save") } },
                text = {
                    if (editing != null) {
                        ModifyHouseRef(
                            houseRef = editing!!.second,
                            onChange = { n -> editing = Pair(editing!!.first, n) },
                        )
                    }
                },
            )
        }
        if (adding != null) {
            AlertDialog(
                onDismissRequest = { adding = null },
                confirmButton = { Button(onClick = {
                    scope.launch {
                        val ad = adding!!
                        ctx.settingsDataStore.updateData { settings ->
                            settings.toBuilder()
                                .addHouseRefs(ad).build()
                        }
                        adding = null
                    }
                }) { Text("Save") } },
                text = {
                    if (adding != null) {
                        ModifyHouseRef(
                            houseRef = adding!!,
                            onChange = { n -> adding = n }
                        )
                    }
                }
            )
        }
    }
}