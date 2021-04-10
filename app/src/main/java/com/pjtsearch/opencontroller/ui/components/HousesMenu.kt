package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.pjtsearch.opencontroller.extensions.resolveHouseRef
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.NetworkHouseRef
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller_lib_proto.House

@ExperimentalMaterialApi
@Composable
fun HousesMenu(houseRefs: List<HouseRef>, onError: (Throwable) -> Unit, onChoose: (House) -> Unit) =
    Column {
        houseRefs.map {
            ListItem(Modifier.padding(5.dp).padding(start = 20.dp).clip(shapes.small).clickable { Thread {
                resolveHouseRef(it).onFailure(onError).onSuccess(onChoose)
            }.start()}) {
                Text(it.displayName)
            }
        }
    }

@ExperimentalMaterialApi
@Preview
@Composable
fun HousesMenuPreview() =
    HousesMenu(listOf(
        HouseRef.newBuilder().setDisplayName("Test")
            .setNetworkHouseRef(NetworkHouseRef.newBuilder().setUrl("http://10.0.2.105:3612/"))
            .build(),
        HouseRef.newBuilder().setDisplayName("Test2")
            .setNetworkHouseRef(NetworkHouseRef.newBuilder().setUrl("http://10.0.2.105:3612/"))
            .build()
    ), {}, {})