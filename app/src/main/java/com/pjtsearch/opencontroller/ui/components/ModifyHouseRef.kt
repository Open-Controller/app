/*
 * Copyright (c) 2022 PJTSearch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.extensions.houseIcons
import com.pjtsearch.opencontroller.settings.HouseRef

/**
 * An interface representing state of the icon choosing dialog
 */
sealed interface ChoosingIconState {
    /**
     * The closed state of the icon choosing dialog
     */
    object Closed : ChoosingIconState

    /**
     * The open state of the icon choosing dialog
     *
     * @property currentIcon The currently selected icon
     */
    data class Opened(val currentIcon: String?) : ChoosingIconState
}


/**
 * A component for modifying a [HouseRef]
 *
 * @param houseRef The [HouseRef] to modify
 * @param onChange Function to be called when the house ref is modified
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyHouseRef(
    modifier: Modifier = Modifier,
    houseRef: HouseRef,
    onChange: (HouseRef) -> Unit
) {
    var houseRefBuilder by remember(houseRef) {
        mutableStateOf(
            HouseRef.newBuilder(houseRef).build()
        )
    }
    var choosingIconState by remember {
        mutableStateOf<ChoosingIconState>(
            ChoosingIconState.Closed
        )
    }

    LaunchedEffect(houseRefBuilder) {
        onChange(houseRefBuilder)
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
//        Icon chooser
        val currentIconState = choosingIconState
        if (currentIconState is ChoosingIconState.Opened) {
            AlertDialog(
                title = { Text("Choose icon") },
                text = {
                    IconPicker(currentIconState.currentIcon, onPick = {
                        choosingIconState = ChoosingIconState.Opened(it)
                    }, iconSet = houseIcons)
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
        FilledTonalButton(
            onClick = {
                choosingIconState = ChoosingIconState.Opened(houseRefBuilder.icon)
            },
            modifier = Modifier
                .align(
                    Alignment.CenterHorizontally
                )
                .size(80.dp)
        ) {
//            Show current icon or "Chose Icon"
            if (houseRefBuilder.icon != "") {
                OpenControllerIcon(
                    icon = houseRefBuilder.icon,
                    text = houseRefBuilder.icon,
                    size = 2,
                    iconSet = houseIcons
                )
            } else {
                Icon(
                    Icons.Outlined.AddCircleOutline,
                    "Choose Icon",
                    Modifier.size(40.dp)
                )
            }
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = houseRefBuilder.displayName,
            leadingIcon = { Icon(Icons.Outlined.TextFields, "Name") },
            label = { Text("Name") },
            shape = MaterialTheme.shapes.medium,
            onValueChange = {
                houseRefBuilder =
                    houseRefBuilder.toBuilder().setDisplayName(it).build()
            })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = houseRefBuilder.networkHouseRef.url,
            label = { Text("URL") },
            leadingIcon = { Icon(Icons.Outlined.Link, "URL") },
            shape = MaterialTheme.shapes.medium,
            onValueChange = {
                houseRefBuilder = houseRefBuilder.toBuilder().setNetworkHouseRef(
                    houseRefBuilder.networkHouseRef.toBuilder().setUrl(it)
                ).build()
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri)
        )
    }
}
