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

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.ui.components.ModifyHouseRef


@Composable
fun EditingDialog(
    state: HouseRef,
    onDismissRequest: () -> Unit,
    onSave: (HouseRef) -> Unit,
    onChange: (HouseRef) -> Unit
) = AlertDialog(
    onDismissRequest = onDismissRequest,
    confirmButton = {
        Button(onClick = {
            onSave(state)
        }) { Text("Save") }
    },
    text = {
        ModifyHouseRef(
            houseRef = state,
            onChange = onChange,
        )
    },
)