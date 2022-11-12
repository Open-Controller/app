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

package com.pjtsearch.opencontroller.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OtherHouses
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeRoomsScreen(
    houseLoadingState: HouseLoadingState,
    onHouseSelected: (HouseRef) -> Unit,
    onSelectController: (Pair<String, String>) -> Unit,
    onReload: () -> Unit,
) {
    var houseSelectorOpened by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val beforeHouseSelected = { houseRef: HouseRef ->
        houseSelectorOpened = false
        scope.launch {
            ctx.settingsDataStore.updateData { oldSettings ->
                oldSettings.toBuilder().clone().setLastHouse(houseRef.id).build()
            }
        }
        onHouseSelected(houseRef)
    }
    var editing: HouseRef? by remember { mutableStateOf(null) }
    var adding: HouseRef? by remember { mutableStateOf(null) }
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    when (houseLoadingState) {
                        is HouseLoadingState.Error -> Text("Error")
                        is HouseLoadingState.Loaded -> Text(houseLoadingState.house.displayName)
                        is HouseLoadingState.Loading -> Text(
                            houseLoadingState.house?.displayName ?: "Loading"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { houseSelectorOpened = true }) {
                        Icon(
                            imageVector = Icons.Outlined.OtherHouses,
                            contentDescription = "Exit house"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.statusBars.exclude(
                    WindowInsets.statusBars.only(
                        WindowInsetsSides.Bottom
                    )
                )
            )
        },
        content = { innerPadding ->
            val paddingValues = PaddingValues(
                start = 15.dp,
                end = 15.dp,
                top = innerPadding.calculateTopPadding() + 15.dp,
                bottom = innerPadding.calculateBottomPadding()
            )
            when (houseLoadingState) {
                is HouseLoadingState.Error ->
                    RoomsErrorLoading(
                        houseLoadingState.error, modifier = Modifier
                            .fillMaxHeight(),
                        contentPadding = paddingValues,
                        onReload = onReload
                    )
                is HouseLoadingState.Loaded -> RoomControllerPicker(
                    houseLoadingState.house.rooms,
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = paddingValues,
                    onSelectController = onSelectController
                )
                is HouseLoadingState.Loading -> RoomsLoading(
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = paddingValues,
                )
            }
        }
    )
    if (houseSelectorOpened) {
        AlertDialog(
            onDismissRequest = { houseSelectorOpened = false },
            title = { Text("Choose House") },
            modifier = Modifier.height(500.dp),
            text = {
                HouseSelector(
                    modifier = Modifier.fillMaxHeight(),
                    houseRefsList = remember(settings) { settings.houseRefsList },
                    onHouseSelected = beforeHouseSelected,
                    currentHouse = houseLoadingState.houseRef.id,
                    onEdit = { editing = it },
                    onDelete = {
                        scope.launch {
                            ctx.settingsDataStore.updateData { settings ->
                                settings.toBuilder()
                                    .removeHouseRefs(settings.houseRefsList.indexOfFirst { h ->
                                        h.id == it
                                    })
                                    .build()
                            }
                            editing = null
                        }
                    }
                )
            },
            confirmButton = {
                Button(onClick = {
                    adding =
                        HouseRef.newBuilder().setId(UUID.randomUUID().toString()).build()
                }) {
                    Text("Add")
                }
            })
    }
    //    Edit house dialog
    when (val editingState = editing) {
        is HouseRef -> EditingDialog(
            state = editingState,
            onDismissRequest = { editing = null },
            onSave = {
                scope.launch {
                    ctx.settingsDataStore.updateData { settings ->
                        settings.toBuilder()
                            .setHouseRefs(settings.houseRefsList.indexOfFirst { h ->
                                h.id == it.id
                            }, it)
                            .build()
                    }
                    editing = null
                }
            },
            onChange = { editing = it }
        )
    }
//    Add house dialog
    when (val addingState = adding) {
        is HouseRef -> EditingDialog(
            state = addingState,
            onDismissRequest = { adding = null },
            onSave = {
                scope.launch {
                    ctx.settingsDataStore.updateData { settings ->
                        settings.toBuilder()
                            .addHouseRefs(it)
                            .build()
                    }
                    adding = null
                    settings.toString()
                }
            },
            onChange = { adding = it }
        )
    }
}
