/*
 * Copyright (c) 2025 PJTSearch
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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OtherHouses
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoomsSidebar(
    houseLoadingState: HouseLoadingState,
    onOpenHouseSelector: () -> Unit,
    onSelectController: (Pair<String, String>) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Scaffold(contentWindowInsets = WindowInsets(0.dp), topBar = {
            CenterAlignedTopAppBar(title = {
                when (val state = houseLoadingState) {
                    is HouseLoadingState.Error -> Text("Error")
                    is HouseLoadingState.Loaded -> Text(state.house.displayName)
                    is HouseLoadingState.Loading -> Text(
                        state.house?.displayName ?: "Loading"
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = onOpenHouseSelector) {
                    Icon(
                        imageVector = Icons.Outlined.OtherHouses,
                        contentDescription = "Exit house"
                    )
                }
            }, windowInsets = WindowInsets(0.dp))
        }) { contentPadding ->
            Column(
                Modifier
                    .padding(contentPadding)
                    .padding(10.dp)
            ) {
                when (val state = houseLoadingState) {
                    is HouseLoadingState.Error ->
                        RoomsErrorLoading(
                            state.error, modifier = Modifier
                                .fillMaxHeight(),
                            contentPadding = PaddingValues(5.dp),
                            onReload = onReload
                        )

                    is HouseLoadingState.Loaded -> RoomControllerPicker(
                        state.house.rooms,
                        modifier = Modifier
                            .fillMaxHeight(),
                        onSelectController = onSelectController
                    )

                    is HouseLoadingState.Loading -> RoomsLoading(
                        Modifier
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}