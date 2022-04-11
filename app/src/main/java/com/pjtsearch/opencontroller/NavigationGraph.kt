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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pjtsearch.opencontroller.home.HomeRoute
import com.pjtsearch.opencontroller.home.HomeViewModel
import com.pjtsearch.opencontroller.houses.HousesRoute
import com.pjtsearch.opencontroller.settings.HouseRef
import kotlinx.coroutines.flow.take

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
    startDestination: String = Destinations.HOUSES_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            Destinations.HOME_ROUTE + "/{house}",
            arguments = listOf(navArgument("house") {
                type = NavType.StringType
            })
        ) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
//                    Parse the HouseRef from the param string
                    HouseRef.parseFrom(
                        it.arguments?.getString("house")!!.encodeToByteArray()
                    ),
                ) { panic -> onError(panic.asThrowable()) }
            )
            HomeRoute(
                homeViewModel = homeViewModel,
                isExpandedScreen = isExpandedScreen,
//                Don't save old state so don't return to the previous home when navigating to another home
                onExit = { navigationActions.navigateToHouses(false) },
                onError = onError
            )
        }
        composable(Destinations.HOUSES_ROUTE) {
            HousesRoute(
                onHouseSelected = { navigationActions.navigateToHome(it, true) }
            )
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
                    settings.houseRefsMap[settings.lastHouse]?.let { houseRef ->
                        navigationActions.navigateToHome(
                            houseRef,
                            false
                        )
                    } ?: navigationActions.navigateToHouses(false)
                }
            }
        }
    }
}