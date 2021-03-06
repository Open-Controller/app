package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.NetworkHouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

@Composable
fun SettingsView(onError: (Throwable) -> Unit) {
    val ctx = LocalContext.current
    Column {
        Button(onClick = {
            thread{ runBlocking {
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
            }}
        }) {
            Text("Set house")
        }
        Button(onClick = {
            thread{ runBlocking {
                ctx.settingsDataStore.updateData {
                    Settings.newBuilder().build()
                }
            }}
        }) {
            Text("Clear houses")
        }
    }
}