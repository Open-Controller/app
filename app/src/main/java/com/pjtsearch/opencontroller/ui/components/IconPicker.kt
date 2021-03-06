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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.extensions.icons

/**
 * A widget to pick icons
 *
 * @param icon The currently selected icon
 * @param onPick A function to be called when an icon is picked
 * @param modifier A modifier for the layout
 * @param iconSet The set of icons to choose from
 */
@Composable
fun IconPicker(
    icon: String?,
    onPick: (icon: String?) -> Unit,
    modifier: Modifier = Modifier,
    iconSet: HashMap<String, *> = icons
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        itemsIndexed(iconSet.keys.toList()) { _, it ->
            val opacity by animateFloatAsState(targetValue = if (icon == it) 1f else 0f)
            Button(
                onClick = {
//                    Toggle selected icon
                    if (icon == it) {
                        onPick(null)
                    } else {
                        onPick(it)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(opacity),
                    contentColor = if (icon == it) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                )
            ) {
                OpenControllerIcon(it, it, iconSet = iconSet)
            }
        }
    }
}