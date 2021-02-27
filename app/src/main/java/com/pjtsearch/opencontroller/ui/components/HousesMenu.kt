package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.pjtsearch.opencontroller.extensions.HouseRef
import com.pjtsearch.opencontroller.extensions.NetworkHouseRef
import com.pjtsearch.opencontroller_lib_proto.HouseOrBuilder
import kotlin.concurrent.thread

@ExperimentalMaterialApi
@Composable
fun HousesMenu(houseRefs: List<HouseRef>, onError: (Throwable) -> Unit, onChoose: (HouseOrBuilder) -> Unit) =
    Column {
        houseRefs.map {
            ListItem(Modifier.clickable { thread {
                it.resolve().onFailure(onError).onSuccess(onChoose)
            }}) {
                Text(it.displayName)
            }
        }
    }

@ExperimentalMaterialApi
@Preview
@Composable
fun HousesMenuPreview() =
    HousesMenu(listOf(
        NetworkHouseRef("Test", "https://google.com"),
        NetworkHouseRef("Test2", "https://google.com")
    ), {}, {})