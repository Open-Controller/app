package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
        TextField(
            value = houseRefBuilder.icon,
            leadingIcon = { Icon(Icons.Outlined.Image, "Icon") },
            label = { Text("Icon") },
            onValueChange = {
                houseRefBuilder =
                    houseRefBuilder.toBuilder().setIcon(it).build()
            })
        TextField(
            value = houseRefBuilder.displayName,
            leadingIcon = { Icon(Icons.Outlined.TextFields, "Name") },
            label = { Text("Name") },
            onValueChange = {
                houseRefBuilder =
                    houseRefBuilder.toBuilder().setDisplayName(it).build()
            })
        TextField(
            value = houseRefBuilder.networkHouseRef.url,
            label = { Text("URL") },
            leadingIcon = { Icon(Icons.Outlined.Link, "URL") },
            onValueChange = {
                houseRefBuilder = houseRefBuilder.toBuilder().setNetworkHouseRef(
                    houseRefBuilder.networkHouseRef.toBuilder().setUrl(it)
                ).build()
            })
    }
}
