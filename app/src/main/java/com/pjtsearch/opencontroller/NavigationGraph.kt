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

package com.pjtsearch.opencontroller

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pjtsearch.opencontroller.appsettings.AddEditHouse
import com.pjtsearch.opencontroller.appsettings.ManageHousesRoute
import com.pjtsearch.opencontroller.appsettings.SettingsRoute
import com.pjtsearch.opencontroller.home.HomeRoute
import com.pjtsearch.opencontroller.home.HomeViewModel
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.welcome.WelcomeRoute
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * A component that navigates between the destinations
 *
 * @param isExpandedScreen Whether the screen is the expanded size
 * @param modifier The modifier to be applied to the layout
 * @param navController The navigation host controller
 * @param navigationActions The navigation actions
 * @param onError Function called when a destination has an error
 * @param startDestination The initial destination
 */
@Composable
fun NavigationGraph(
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navigationActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
    onError: (Throwable) -> Unit,
    startDestination: String
) {
    NavHost(
        navController = navController,
        popExitTransition = {
            val intSpec =
                tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing)
            val floatSpec =
                tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            slideOutHorizontally(intSpec, targetOffsetX = { s -> s / 14 }) + fadeOut(
                floatSpec,
                0f
            )
        },
        enterTransition = {
            val intSpec =
                tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing)
            val floatSpec =
                tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            slideInHorizontally(intSpec, initialOffsetX = { s -> s / 14 }) + fadeIn(
                floatSpec,
                0f
            )
        },
        popEnterTransition = {
            val floatSpec =
                tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            fadeIn(floatSpec, 0f)
        },
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            Destinations.HOME_ROUTE + "/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) {
            val ctx = LocalContext.current
            val settings by ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())

            val houseRef = remember(settings) {
                settings.houseRefsList.find { h -> h.id == it.arguments?.getString("id")!! }
            }

            val homeViewModel: HomeViewModel? = if (houseRef != null) {
                viewModel(
                    factory = HomeViewModel.provideFactory(
                        houseRef
                    ) { panic -> onError(panic.asThrowable()) }
                )
            } else {
                null
            }

            when (homeViewModel) {
                null -> {
                    Text(
                        "Could not find house",
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                else -> {
                    val houseLoadingState by homeViewModel.houseLoadingState.collectAsState()
                    HomeRoute(
                        houseLoadingState = houseLoadingState,
                        isExpandedScreen = isExpandedScreen,
                        onError = onError,
                        onOpenSettings = { subRoute ->
                            navigationActions.navigateToSettings(
                                subRoute
                            )
                        },
                        onHouseSelected = { h ->
                            navigationActions.navigateToHome(
                                h.id,
                                false,
                                true
                            )
                        },
                        onReload = { homeViewModel.refreshHouse() }
                    )
                }
            }
        }

        composable(Destinations.WELCOME_ROUTE) {
            val ctx = LocalContext.current
            val scope = rememberCoroutineScope()

            WelcomeRoute(onHouseAdded = { house ->
                scope.launch {
                    ctx.settingsDataStore.updateData { oldSettings ->
                        oldSettings.toBuilder().clone().setLastHouse(house.id).build()
                    }
                    navigationActions.navigateToHome(
                        house.id,
                        false,
                        true
                    )
                }
            })
        }

        composable(Destinations.SETTINGS_ROUTE) {
            SettingsRoute(
                onOpenSubRoute = { subRoute ->
                    navigationActions.navigateToSettings(
                        subRoute
                    )
                },
                onExit = { navController.popBackStack() })
        }

        composable(
            Destinations.SETTINGS_ROUTE + "/" + SettingsDestinations.MANAGE_HOUSES_ROUTE
        ) {
            ManageHousesRoute(
                onOpenSettings = navigationActions.navigateToSettings,
                onExit = { navController.popBackStack() })
        }

        composable(
            Destinations.SETTINGS_ROUTE + "/editHouse/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) {
            val ctx = LocalContext.current
            val settings by ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())

            var houseRef by remember(settings) {
                mutableStateOf(
                    settings.houseRefsList.find { h -> h.id == it.arguments?.getString("id")!! }
                )
            }
            val scope = rememberCoroutineScope()

            when (val ref = houseRef) {
                null -> {
                    Text(
                        "Could not find house",
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                else -> AddEditHouse(
                    houseRef = ref,
                    onChange = { houseRef = it },
                    onSave = {
                        scope.launch {
                            ctx.settingsDataStore.updateData { settings ->
                                settings.toBuilder()
                                    .setHouseRefs(settings.houseRefsList.indexOfFirst { h ->
                                        h.id == ref.id
                                    }, ref)
                                    .build()
                            }
                        }
                    },
                    onDelete = {
                        scope.launch {
                            ctx.settingsDataStore.updateData { settings ->
                                settings.toBuilder()
                                    .removeHouseRefs(settings.houseRefsList.indexOfFirst { h ->
                                        h.id == ref.id
                                    })
                                    .build()
                            }
                        }
                    },
                    onExit = { navController.popBackStack() })
            }
        }

        composable(
            Destinations.SETTINGS_ROUTE + "/addHouse"
        ) {
            var houseRef by remember {
                mutableStateOf(
                    HouseRef.newBuilder().setId(UUID.randomUUID().toString()).build()
                )
            }
            val ctx = LocalContext.current
            val scope = rememberCoroutineScope()

            AddEditHouse(
                houseRef = houseRef,
                onChange = { houseRef = it },
                onSave = {
                    scope.launch {
                        ctx.settingsDataStore.updateData { settings ->
                            settings.toBuilder()
                                .addHouseRefs(houseRef)
                                .build()
                        }
                    }
                },
                onExit = { navController.popBackStack() })
        }

//        Navigates to the last used home
        composable(Destinations.LAST_HOME_ROUTE) {
            val ctx = LocalContext.current

//            Navigate to last home from settings on launch if it exists
            LaunchedEffect(ctx.settingsDataStore) {
                ctx.settingsDataStore.data.take(1).collect { settings ->
//                    Clear the current (LAST_HOME_ROUTE) destination from the back stack so don't go back to it
                    navController.popBackStack()
//                    Navigate to last house ref if it still exists, otherwise navigate to houses
                    settings.houseRefsList.find { it.id == settings.lastHouse }
                        ?.let { houseRef ->
                            navigationActions.navigateToHome(
                                houseRef.id,
                                false,
                                false
                            )
                        } ?: navigationActions.navigateToWelcome()
                }
            }
        }
    }
}