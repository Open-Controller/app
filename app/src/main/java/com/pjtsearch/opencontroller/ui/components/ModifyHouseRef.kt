package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.settings.HouseRef


@Composable
fun ModifyHouseRef(houseRef: HouseRef, onChange: (HouseRef) -> Unit) {
    var houseRefBuilder by remember(houseRef) { mutableStateOf(HouseRef.newBuilder(houseRef).build()) }

    LaunchedEffect(houseRefBuilder) {
        onChange(houseRefBuilder)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
//        FIXME: Figure out inputs
        BasicTextField(
            value = houseRefBuilder.icon,
            onValueChange = {
                houseRefBuilder =
                    houseRefBuilder.toBuilder().setIcon(it).build()
            })
        BasicTextField(
            value = houseRefBuilder.displayName,
            onValueChange = {
                houseRefBuilder =
                    houseRefBuilder.toBuilder().setDisplayName(it).build()
            })
        BasicTextField(
            value = houseRefBuilder.networkHouseRef.url,
            onValueChange = {
                houseRefBuilder = houseRefBuilder.toBuilder().setNetworkHouseRef(
                    houseRefBuilder.networkHouseRef.toBuilder().setUrl(it)
                ).build()
            })
    }
}
