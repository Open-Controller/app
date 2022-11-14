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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.ui.components.ModifyHouseRef

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHouse(
    houseRef: HouseRef,
    onChange: (HouseRef) -> Unit,
    onSave: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onExit: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(title = {
            Text(
                if (onDelete == null) {
                    "Add House"
                } else {
                    "Edit House"
                }
            )
        }, navigationIcon = {
            IconButton(
                onClick = onExit
            ) {
                Icon(Icons.Outlined.Close, "Close")
            }
        }, scrollBehavior = scrollBehavior)
    }, bottomBar = {
        BottomAppBar(windowInsets = WindowInsets.ime) {
            Spacer(Modifier.weight(1f))
            if (onDelete != null) {
                OutlinedButton(onClick = { onDelete(); onExit() }) {
                    Text("Delete")
                }
            }
            Spacer(Modifier.width(10.dp))
            Button(onClick = { onSave(); onExit() }) {
                Text("Save")
            }
        }
    }) { innerPadding ->
        val paddingValues = PaddingValues(
            start = 15.dp,
            end = 15.dp,
            top = innerPadding.calculateTopPadding() + 15.dp,
            bottom = innerPadding.calculateBottomPadding()
        )
        LazyVerticalGrid(columns = GridCells.Fixed(1), contentPadding = paddingValues) {
            item {
                ModifyHouseRef(
                    modifier = Modifier
                        .fillMaxSize(),
                    houseRef = houseRef,
                    onChange = onChange
                )
            }
        }
    }
}