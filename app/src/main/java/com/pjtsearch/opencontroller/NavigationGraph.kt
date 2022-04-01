package com.pjtsearch.opencontroller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
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
                    HouseRef.parseFrom(
                        it.arguments?.getString("house")!!.encodeToByteArray()
                    ),
                ) { panic -> onError(panic.asThrowable()) }
            )
            HomeRoute(
                homeViewModel = homeViewModel,
                isExpandedScreen = isExpandedScreen,
                onExit = { navigationActions.navigateToHouses(false) },
                onError = onError
            )
        }
        composable(Destinations.HOUSES_ROUTE) {
            HousesRoute(
                onHouseSelected = { navigationActions.navigateToHome(it, true) }
            )
        }
        composable(Destinations.LAST_HOME_ROUTE) {
            val ctx = LocalContext.current

            LaunchedEffect(ctx.settingsDataStore) {
                ctx.settingsDataStore.data.take(1).collect { settings ->
                    navController.popBackStack()
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