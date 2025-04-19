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

//import com.pjtsearch.opencontroller.components.CenterAlignedTopAppBarWithPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.ui.components.ControllerView

@OptIn(
    ExperimentalComposeUiApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)
@Composable
fun HomeControllerScreen(
    roomDisplayName: String,
    isExpandedScreen: Boolean,
    controller: Controller,
    onBack: () -> Unit,
    onError: (Throwable) -> Unit,
    onInteractWithControllerMenu: (open: Boolean, items: List<Widget>) -> Unit,
    controllerMenuState: ControllerMenuState
) =
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(roomDisplayName + " " + controller.displayName) },
                navigationIcon = {
                    if (!isExpandedScreen) IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Exit house"
                        )
                    }
                },
                windowInsets = if (!isExpandedScreen) WindowInsets.statusBars.exclude(
                    WindowInsets.statusBars.only(
                        WindowInsetsSides.Bottom
                    )
                ) else TopAppBarDefaults.windowInsets
            )
        },
        content = { innerPadding ->
            Column(
                if (!isExpandedScreen) Modifier
                    .padding(innerPadding)
                    .padding(start = 15.dp, end = 15.dp)
                    .padding(
                        bottom = WindowInsets.navigationBars
                            .only(WindowInsetsSides.Bottom)
                            .asPaddingValues()
                            .calculateBottomPadding()
                    )
                else
                    Modifier
                        .padding(innerPadding)
            ) {
                ControllerView(
                    controller,
                    onError = onError,
                    menuState = controllerMenuState,
                    onInteractMenu = onInteractWithControllerMenu
                )
            }
        }
    )

