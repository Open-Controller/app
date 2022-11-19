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

package com.pjtsearch.opencontroller.appsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddHome
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.pjtsearch.opencontroller.SettingsDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(onOpenSubRoute: (String) -> Unit, onExit: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(modifier = Modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = onExit
                    ) {
                        Icon(Icons.Outlined.ArrowBack, "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }) { contentPadding ->
        LazyVerticalGrid(
            modifier = Modifier.fillMaxHeight(),
            columns = GridCells.Fixed(1),
            contentPadding = contentPadding
        ) {
            item {
                ListItem(
                    modifier = Modifier.clickable { onOpenSubRoute(SettingsDestinations.MANAGE_HOUSES_ROUTE) },
                    headlineText = { Text("Manage houses") },
                    supportingText = { Text("Edit & add houses") },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.AddHome, "Manage houses"
                        )
                    })
            }
        }
    }
}