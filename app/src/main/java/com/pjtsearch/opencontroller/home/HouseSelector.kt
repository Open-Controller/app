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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.extensions.houseIcons
import com.pjtsearch.opencontroller.settings.HouseRef


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseSelector(
    modifier: Modifier = Modifier,
    houseRefsList: List<HouseRef>,
    currentHouse: String,
    onHouseSelected: (HouseRef) -> Unit
) {
    LazyColumn(modifier) {
        items(houseRefsList, { it.id }) {
            ListItem(
                headlineText = { Text(it.displayName) },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onHouseSelected(it) },
                tonalElevation = if (currentHouse != it.id) {
                    ListItemDefaults.Elevation
                } else {
                    50.dp
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