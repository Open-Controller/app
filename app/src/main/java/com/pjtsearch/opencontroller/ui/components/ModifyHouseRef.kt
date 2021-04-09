package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.settings.HouseRef
import kotlinx.coroutines.launch


@Composable
fun ModifyHouseRef(houseRef: HouseRef, onChange: (HouseRef) -> Unit, onSave: (HouseRef) -> Unit) {
    var houseRefBuilder by remember(houseRef) { mutableStateOf(HouseRef.newBuilder(houseRef).build()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(houseRefBuilder) {
        onChange(houseRefBuilder)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TextField(
            label = { Text("Name") },
            value = houseRefBuilder.displayName,
            onValueChange = {
                houseRefBuilder =
                    houseRefBuilder.toBuilder().setDisplayName(it).build()
            })
        TextField(
            label = { Text("URL") },
            value = houseRefBuilder.networkHouseRef.url,
            onValueChange = {
                houseRefBuilder = houseRefBuilder.toBuilder().setNetworkHouseRef(
                    houseRefBuilder.networkHouseRef.toBuilder().setUrl(it)
                ).build()
            })
        Button(onClick = { scope.launch {
            onSave(houseRefBuilder);
        }}) {
            Text("Save")
        }
    }
}
