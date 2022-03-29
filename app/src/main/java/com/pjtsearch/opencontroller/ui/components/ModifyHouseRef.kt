package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.settings.HouseRef

sealed interface ChoosingIconState {
    object Closed : ChoosingIconState
    data class Opened(val currentIcon: String?) : ChoosingIconState
}


@Composable
fun ModifyHouseRef(houseRef: HouseRef, onChange: (HouseRef) -> Unit) {
    var houseRefBuilder by remember(houseRef) {
        mutableStateOf(
            HouseRef.newBuilder(houseRef).build()
        )
    }
    var choosingIconState by remember { mutableStateOf<ChoosingIconState>(ChoosingIconState.Closed) }

    LaunchedEffect(houseRefBuilder) {
        onChange(houseRefBuilder)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val currentIconState = choosingIconState
        if (currentIconState is ChoosingIconState.Opened) {
            AlertDialog(
                title = { Text("Choose Icon") },
                text = {
                    IconPicker(currentIconState.currentIcon, onPick = {
                        choosingIconState = ChoosingIconState.Opened(it)
                    })
                },
                onDismissRequest = {
                    choosingIconState = ChoosingIconState.Closed
                },
                confirmButton = {
                    Button(onClick = {
                        val chosen = currentIconState.currentIcon
                        houseRefBuilder = chosen?.let {
                            houseRefBuilder.toBuilder().setIcon(it).build()
                        } ?: houseRefBuilder.toBuilder().clearIcon().build()
                        choosingIconState = ChoosingIconState.Closed
                    }) {
                        Text(text = "Set")
                    }
                }
            )
        }
        TextButton(
            onClick = { choosingIconState = ChoosingIconState.Opened(houseRefBuilder.icon) },
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        ) {
            if (houseRefBuilder.icon != "") {
                OpenControllerIcon(
                    icon = houseRefBuilder.icon,
                    text = houseRefBuilder.icon,
                    size = 2
                )
            } else {
                Text("Chose Icon")
            }
        }
        OutlinedTextField(
            value = houseRefBuilder.displayName,
            leadingIcon = { Icon(Icons.Outlined.TextFields, "Name") },
            label = { Text("Name") },
            shape = RoundedCornerShape(15.dp),
            onValueChange = {
                houseRefBuilder =
                    houseRefBuilder.toBuilder().setDisplayName(it).build()
            })
        OutlinedTextField(
            value = houseRefBuilder.networkHouseRef.url,
            label = { Text("URL") },
            leadingIcon = { Icon(Icons.Outlined.Link, "URL") },
            shape = RoundedCornerShape(15.dp),
            onValueChange = {
                houseRefBuilder = houseRefBuilder.toBuilder().setNetworkHouseRef(
                    houseRefBuilder.networkHouseRef.toBuilder().setUrl(it)
                ).build()
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri)
        )
    }
}
