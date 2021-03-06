package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.NetworkHouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.theme.shapes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SettingsView(onError: (Throwable) -> Unit) {
    val ctx = LocalContext.current
    val settings = ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scope = rememberCoroutineScope()
    var editorShown by remember { mutableStateOf(false) }
    var editingHouseRef: HouseRef? by remember { mutableStateOf(null) }
    Column {
        settings.value.houseRefsList.forEachIndexed { i, it ->
            ListItem(
                trailing = {
                    IconButton(onClick = {
                        scope.launch {
                            ctx.settingsDataStore.updateData { settings ->
                                settings.toBuilder().removeHouseRefs(i).build()
                            }
                        }
                    }) {
                        Icon(Icons.Outlined.Delete, "Delete this house")
                    }
                },
                modifier = Modifier.clickable { editingHouseRef = it; editorShown = true }
            ) {
                Text(it.displayName)
            }
        }
        Button(onClick = {
            editorShown = true
            editingHouseRef = null
        }) {
            Text("Add house")
        }
        EditHouseRefDialog(editorShown, editingHouseRef, { editorShown = false }, { scope.launch {
            ctx.settingsDataStore.updateData { settings ->
                if (editingHouseRef != null) {
                    val i = settings.toBuilder().houseRefsList.indexOf(editingHouseRef)
                    settings.toBuilder().removeHouseRefs(i)
                }
                settings.toBuilder().addHouseRefs(it).build()
            }
        }})
    }
}

@Composable
fun EditHouseRefDialog(shown: Boolean, houseRef: HouseRef?, onClose: () -> Unit, onSave: (HouseRef) -> Unit) {
    var houseRefBuilder by mutableStateOf(HouseRef.getDefaultInstance())
    LaunchedEffect(houseRef) {
        if (houseRef != null) {
            houseRefBuilder = houseRefBuilder.toBuilder().mergeFrom(houseRef).build()
        }
    }
    var closing by mutableStateOf(false)
    val scope = rememberCoroutineScope()

    if (shown) {
        AlertDialog(
            shape = shapes.medium,
            onDismissRequest = { scope.launch {
                closing = true; delay(200); onClose()
            }},
            title = {
                Text(houseRef?.let { "Edit House" } ?: "Add House")
            },
            confirmButton = {
                // TODO: Get rid of this hack to to have text boxes disabled before dialog
                // see https://issuetracker.google.com/issues/180021390 and https://issuetracker.google.com/issues/180124293
                Button(onClick = { scope.launch {
                    onSave(houseRefBuilder); closing = true; delay(200); onClose()
                }}) {
                    Text("Save")
                }
            },
            dismissButton = {
                // TODO: Get rid of this hack to to have text boxes disabled before dialog
                // see https://issuetracker.google.com/issues/180021390 and https://issuetracker.google.com/issues/180124293
                Button(onClick = { scope.launch {
                   closing = true; delay(200); onClose()
                }}) {
                    Text("Close")
                }
            },
            text = {
                Column {
                    TextField(
                        enabled = !closing,
                        value = houseRefBuilder.displayName,
                        onValueChange = {
                            houseRefBuilder =
                                houseRefBuilder.toBuilder().setDisplayName(it).build()
                            println(houseRefBuilder.displayName)
                        })
                    TextField(
                        enabled = !closing,
                        value = houseRefBuilder.networkHouseRef.url,
                        onValueChange = {
                            houseRefBuilder = houseRefBuilder.toBuilder().setNetworkHouseRef(
                                houseRefBuilder.networkHouseRef.toBuilder().setUrl(it)
                            ).build()
                        })
                }
            }
        )
    }
}