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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OtherHouses
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeRoomsScreen(
    houseLoadingState: HouseLoadingState,
    onOpenHouseSelector: () -> Unit,
    onSelectController: (Pair<String, String>) -> Unit,
    onReload: () -> Unit,
    onOpenSettings: (String?) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
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
                    IconButton(onClick = onOpenHouseSelector) {
                        Icon(
                            imageVector = Icons.Outlined.OtherHouses,
                            contentDescription = "Exit house"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenSettings(null) }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Open Settings"
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
}
