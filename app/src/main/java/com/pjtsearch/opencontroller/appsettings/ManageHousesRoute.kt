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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.SettingsDestinations
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.extensions.houseIcons
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageHousesRoute(onOpenSettings: (String?) -> Unit, onExit: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(modifier = Modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = { Text("Manage houses") }, navigationIcon = {
            IconButton(
                onClick = onExit
            ) {
                Icon(Icons.Outlined.ArrowBack, "Back")
            }
        }, scrollBehavior = scrollBehavior)
    }, floatingActionButton = {
        ExtendedFloatingActionButton(
            modifier = Modifier
                .padding(bottom = 30.dp),
            onClick = {
                onOpenSettings(SettingsDestinations.ADD_HOUSE_ROUTE)
            },
            containerColor = MaterialTheme.colorScheme.tertiary,
            text = { Text("Add house") },
            icon = { Icon(Icons.Outlined.Add, "Add a house") }
        )
    }, floatingActionButtonPosition = FabPosition.Center
    ) { contentPadding ->
        LazyVerticalGrid(
            modifier = Modifier.fillMaxHeight(),
            columns = GridCells.Fixed(1),
            contentPadding = contentPadding
        ) {
            items(settings.houseRefsList, { it.id }) {
                ListItem(
                    headlineText = { Text(it.displayName) },
                    modifier = Modifier
                        .clickable {
                            onOpenSettings(
                                SettingsDestinations.EDIT_HOUSE_ROUTE + "/" + it.id
                            )
                        },
                    leadingContent = {
                        OpenControllerIcon(
                            icon = it.icon,
                            text = it.icon,
                            iconSet = houseIcons
                        )
                    }
                )
            }
        }
    }
}