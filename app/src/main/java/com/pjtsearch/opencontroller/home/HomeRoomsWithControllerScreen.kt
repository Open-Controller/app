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

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OtherHouses
import androidx.compose.material.icons.twotone.SettingsRemote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.SettingsDestinations
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.components.ControllerView
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun HomeRoomsWithControllerScreen(
    uiState: HomeUiState,
    onSelectController: (Pair<String, String>) -> Unit,
    onInteractWithControllerMenu: (open: Boolean, items: List<Widget>) -> Unit,
    onHouseSelected: (HouseRef) -> Unit,
    onReload: () -> Unit,
    onError: (Throwable) -> Unit,
    onOpenSettings: (String?) -> Unit,
) {
    var houseSelectorOpened by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val beforeHouseSelected = { houseRef: HouseRef ->
        houseSelectorOpened = false
        scope.launch {
            ctx.settingsDataStore.updateData { oldSettings ->
                oldSettings.toBuilder().clone().setLastHouse(houseRef.id).build()
            }
        }
        onHouseSelected(houseRef)
    }

    Row(
        Modifier.padding(
            WindowInsets.systemBars.asPaddingValues()
        )
    ) {
        Surface(
            modifier = Modifier
                .weight(3f)
                .padding(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Scaffold(contentWindowInsets = WindowInsets(0.dp), topBar = {
                CenterAlignedTopAppBar(title = {
                    when (val state = uiState.houseLoadingState) {
                        is HouseLoadingState.Error -> Text("Error")
                        is HouseLoadingState.Loaded -> Text(state.house.displayName)
                        is HouseLoadingState.Loading -> Text(
                            state.house?.displayName ?: "Loading"
                        )
                    }
                }, navigationIcon = {
                    IconButton(onClick = { houseSelectorOpened = true }) {
                        Icon(
                            imageVector = Icons.Outlined.OtherHouses,
                            contentDescription = "Exit house"
                        )
                    }
                }, windowInsets = WindowInsets(0.dp))
            }) { contentPadding ->
                Column(
                    Modifier
                        .padding(contentPadding)
                        .padding(10.dp)
                ) {
                    when (val state = uiState.houseLoadingState) {
                        is HouseLoadingState.Error ->
                            RoomsErrorLoading(
                                state.error, modifier = Modifier
                                    .fillMaxHeight(),
                                contentPadding = PaddingValues(5.dp),
                                onReload = onReload
                            )
                        is HouseLoadingState.Loaded -> RoomControllerPicker(
                            state.house.rooms,
                            modifier = Modifier
                                .fillMaxHeight(),
                            onSelectController = onSelectController
                        )
                        is HouseLoadingState.Loading -> RoomsLoading(
                            Modifier
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
        Column(
            Modifier
                .weight(5f)
                .padding(horizontal = 15.dp)
        ) {
            Crossfade(targetState = uiState is HomeUiState.HasController) { hasController ->
                when (hasController) {
                    true -> {
                        check(uiState is HomeUiState.HasController)
                        AnimatedContent(
                            targetState = uiState.selectedController,
                            transitionSpec = {
                                slideInVertically(
                                    tween<IntOffset>(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    ),
                                    { height -> 1 * height / 10 },
                                ) + fadeIn(
                                    tween<Float>(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    ), 0f
                                ) with
                                        slideOutVertically(
                                            tween<IntOffset>(
                                                durationMillis = 300,
                                                easing = FastOutSlowInEasing
                                            ),
                                            { height -> -1 * height / 10 },
                                        ) + fadeOut(
                                    tween<Float>(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    ), 0f
                                )
                            }) { controller ->
                            Scaffold(
                                topBar = {
                                    CenterAlignedTopAppBar(
                                        title = { Text(uiState.roomDisplayName + " " + controller.displayName) },
                                    )
                                },
                                content = { innerPadding ->
                                    Column(
                                        Modifier
                                            .padding(innerPadding)
                                    ) {
                                        ControllerView(
                                            controller,
                                            onError = onError,
                                            menuState = uiState.controllerMenuState,
                                            onInteractMenu = onInteractWithControllerMenu
                                        )
                                    }
                                }
                            )
                        }
                    }
                    false -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(40.dp)
                        ) {
                            Icon(
                                Icons.TwoTone.SettingsRemote,
                                "Controller Icon",
                                modifier = Modifier.size(250.dp),
                                tint = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                            )
                            Text(
                                text = "Choose a Controller",
                                color = MaterialTheme.colorScheme.onBackground.copy(0.4f),
                                style = MaterialTheme.typography.displayLarge
                            )
                        }
                    }
                }
            }
        }
    }

    if (houseSelectorOpened) {
        AlertDialog(
            onDismissRequest = { houseSelectorOpened = false },
            title = { Text("Choose house") },
            modifier = Modifier.height(500.dp),
            text = {
                HouseSelector(
                    modifier = Modifier.fillMaxHeight(),
                    houseRefsList = remember(settings) { settings.houseRefsList },
                    onHouseSelected = beforeHouseSelected,
                    currentHouse = uiState.houseLoadingState.houseRef.id
                )
            },
            confirmButton = {
                Button(onClick = {
                    houseSelectorOpened = false; onOpenSettings(
                    SettingsDestinations.MANAGE_HOUSES_ROUTE
                )
                }) {
                    Text("Manage")
                }
            })
    }
}