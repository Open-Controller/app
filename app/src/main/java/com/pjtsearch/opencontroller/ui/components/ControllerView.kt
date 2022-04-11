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

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.home.ControllerMenuState

/**
 * A component that displays a [Controller]
 *
 * @param controller The [Controller] to display
 * @param onError Function to be called when the controller has an error in evaluation
 * @param menuState The state of the expansion menu
 * @param onInteractMenu Function to be called when the expansion menu is interacted with
 */
@ExperimentalComposeUiApi
@Composable
fun ControllerView(
    controller: Controller,
    onError: (Throwable) -> Unit,
    menuState: ControllerMenuState,
    onInteractMenu: (open: Boolean, items: List<Widget>) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .weight(0.1f)
                .padding(bottom = 10.dp)
        ) {
            controller.displayInterface?.widgets?.map {
                Widget(
                    it,
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    { items ->
                        onInteractMenu(
                            menuState is ControllerMenuState.Closed,
                            items
                        )
                    },
                    onError
                )
            }
        }
//        Expansion menu
        AnimatedVisibility(
            visible = menuState is ControllerMenuState.Open,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(25.dp),
                tonalElevation = 1.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    menuState.items.map {
                        Widget(
                            it,
                            Modifier.fillMaxWidth(),
                            { items -> onInteractMenu(true, items) },
                            onError
                        )
                    }
                }
            }
        }
    }
}