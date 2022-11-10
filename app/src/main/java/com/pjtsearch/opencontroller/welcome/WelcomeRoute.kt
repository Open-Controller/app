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

package com.pjtsearch.opencontroller.welcome

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.components.ModifyHouseRef
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun WelcomeRoute(onHouseAdded: (HouseRef) -> Unit) {
    val ctx = LocalContext.current
//    val settings =
//        ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scope = rememberCoroutineScope()
    var adding: HouseRef by rememberSaveable {
        mutableStateOf(
            HouseRef.newBuilder().setId(
                UUID.randomUUID().toString()
            ).build()
        )
    }

    Column {
        ModifyHouseRef(
            houseRef = adding,
            onChange = { n ->
                adding = n
            }
        )
        Button(onClick = {
            scope.launch {
                ctx.settingsDataStore.updateData { settings ->
                    onHouseAdded(adding)
                    settings.toBuilder()
                        .addHouseRefs(adding)
                        .build()
                }
            }
        }) { Text("Save") }
    }
}