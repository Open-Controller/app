/*
 * Copyright (c) 2025 PJTSearch
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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pjtsearch.opencontroller.SettingsDestinations
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import kotlinx.coroutines.launch

/**
 * The destinations for the navigation
 */
object Destinations {
    /**
     * The destination for the base route
     */
    const val BASE_ROUTE = "base"

    /**
     * The destination for the controller route
     */
    const val CONTROLLER_ROUTE = "controller"
}

/**
 * Actions to navigate to destinations
 *
 * @constructor
 * Creates a new instance of the NavigationActions class for a controller
 *
 * @param navController The navigation host controller
 */
class NavigationActions(navController: NavHostController) {
    /**
     * Navigates to the Base route
     */
    val navigateToBase: () -> Unit =
        {
            navController.navigate(
                Destinations.BASE_ROUTE
            ) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = false
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = false
            }
        }

    val navigateToController: (room: String, controller: String) -> Unit =
        { room, controller ->
            val oldRoute = navController.currentBackStackEntry?.destination?.route
            val oldRoom =
                navController.currentBackStackEntry?.arguments?.getString("room")
            val oldController =
                navController.currentBackStackEntry?.arguments?.getString("controller")
//            Prevent from navigating to the same route. Need to do this because singletop doesn't check arguments
//            (see https://stackoverflow.com/a/68903458/22641520 and https://stackoverflow.com/q/75795737/22641520)
            if (oldRoute != Destinations.CONTROLLER_ROUTE + "/{room}/{controller}" || oldRoom != room || oldController != controller) {
                navController.navigate(
                    Destinations.CONTROLLER_ROUTE + "/$room/$controller"
                ) {
                    // Restore state when reselecting a previously selected item
                    restoreState = false
                }
            }
        }
}

@Composable
fun HomeRoute(
    houseLoadingState: HouseLoadingState,
    isExpandedScreen: Boolean,
    onError: (Throwable) -> Unit,
    onReload: () -> Unit,
    onOpenSettings: (String?) -> Unit,
    onHouseSelected: (HouseRef) -> Unit,
) {
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)
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

    if (isExpandedScreen) {
        Row(
            Modifier.padding(
                WindowInsets.systemBars.asPaddingValues()
            )
        ) {
            HomeRoomsSidebar(
                houseLoadingState = houseLoadingState,
                onOpenHouseSelector = { houseSelectorOpened = true },
                onSelectController = {
                    navigationActions.navigateToController(
                        it.first,
                        it.second
                    )
                },
                onReload = onReload,
                modifier = Modifier.weight(3f)
            )
            Column(
                Modifier
                    .weight(5f)
                    .padding(horizontal = 15.dp)
            ) {
                HomeRouteNavigator(
                    navController = navController,
                    navigationActions = navigationActions,
                    houseLoadingState = houseLoadingState,
                    isExpandedScreen = isExpandedScreen,
                    onError = onError,
                    onOpenSettings = onOpenSettings,
                    onOpenHouseSelector = { houseSelectorOpened = true },
                    onReload = onReload
                )
            }
        }
    } else {
        HomeRouteNavigator(
            navController = navController,
            navigationActions = navigationActions,
            houseLoadingState = houseLoadingState,
            isExpandedScreen = isExpandedScreen,
            onError = onError,
            onOpenSettings = onOpenSettings,
            onOpenHouseSelector = { houseSelectorOpened = true },
            onReload = onReload
        )
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
                    currentHouse = houseLoadingState.houseRef.id
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

@Composable
fun HomeRouteNavigator(
    navController: NavHostController = rememberNavController(),
    navigationActions: NavigationActions = NavigationActions(navController),
    houseLoadingState: HouseLoadingState,
    isExpandedScreen: Boolean,
    onError: (Throwable) -> Unit,
    onOpenSettings: (String?) -> Unit,
    onOpenHouseSelector: () -> Unit,
    onReload: () -> Unit,
) {
    NavHost(
        navController = navController,
        popExitTransition = {
            val floatSpec =
                tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            scaleOut(
                floatSpec,
                targetScale = 0.95f
            ) + fadeOut(floatSpec, 0f)
        },
        enterTransition = {
            val intSpec =
                tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing)
            val floatSpec =
                tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            slideInVertically(
                intSpec,
                { height -> height / 10 },
            ) + fadeIn(floatSpec, 0f)
        },
        popEnterTransition = {
            val floatSpec =
                tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            fadeIn(floatSpec, 0f)
        },
        startDestination = "base",
    ) {
        composable(Destinations.BASE_ROUTE) {
            if (!isExpandedScreen) {
                HomeRoomsScreen(
                    houseLoadingState = houseLoadingState,
                    onOpenHouseSelector = onOpenHouseSelector,
                    onSelectController = {
                        navigationActions.navigateToController(
                            it.first,
                            it.second
                        )
                    },
                    onReload = onReload,
                    onOpenSettings = onOpenSettings
                )
            } else {
                HomeEmptyBase()
            }
        }
        composable(
            Destinations.CONTROLLER_ROUTE + "/{room}/{controller}",
            arguments = listOf(navArgument("room") {
                type = NavType.StringType
            }, navArgument("controller") {
                type = NavType.StringType
            })
        ) {
            val room = it.arguments?.getString("room")!!
            val controller = it.arguments?.getString("controller")!!
            check(houseLoadingState is HouseLoadingState.Loaded)
            var menuState: ControllerMenuState by remember {
                mutableStateOf(
                    ControllerMenuState.Closed(listOf())
                )
            }
            HomeControllerScreen(
                roomDisplayName = houseLoadingState.house.rooms.find { it.id == room }!!.displayName,
                controller = houseLoadingState.house.rooms.find { it.id == room }!!.controllers.find { it.id == controller }!!,
                isExpandedScreen = isExpandedScreen,
                onBack = { navigationActions.navigateToBase() },
                onError = onError,
                onInteractWithControllerMenu = { open, widgets ->
                    menuState = if (open) ControllerMenuState.Open(
                        widgets
                    ) else ControllerMenuState.Closed(widgets)
                },
                controllerMenuState = menuState
            )
        }
    }

}