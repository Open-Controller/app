package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.NetworkHouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

@ExperimentalMaterialApi
@Composable
fun SettingsView(onError: (Throwable) -> Unit) {
    val ctx = LocalContext.current
    val settings = ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scope = rememberCoroutineScope()
    Column {
        settings.value.houseRefsList.forEachIndexed { i, it ->
            ListItem(trailing = { IconButton(onClick = {
                scope.launch {
                    ctx.settingsDataStore.updateData { settings ->
                        settings.toBuilder().removeHouseRefs(i).build()
                    }
                }
            }) {
                Icon(Icons.Outlined.Delete, "Delete this house")
            }}) {
                Text(it.displayName)
            }
        }
        Button(onClick = {
            scope.launch {
                ctx.settingsDataStore.updateData {
                    Settings.newBuilder()
                            .addHouseRefs(
                                HouseRef.newBuilder().setDisplayName("Home")
                                    .setNetworkHouseRef(
                                        NetworkHouseRef.newBuilder()
                                            .setUrl("http://10.0.2.105:3612/")
                                    ).build()
                            )
                            .build()
                }
            }
        }) {
            Text("Set house")
        }
    }
}