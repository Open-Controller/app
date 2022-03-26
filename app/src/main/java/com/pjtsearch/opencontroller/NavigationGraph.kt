package com.pjtsearch.opencontroller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                    {onError(it.asThrowable())},
                    {onError(it)},
                )
            )
            HomeRoute(
                homeViewModel = homeViewModel,
                isExpandedScreen = isExpandedScreen,
                onExit = { navigationActions.navigateToHouses() },
                onError = onError
            )
        }
        composable(Destinations.HOUSES_ROUTE) {
            HousesRoute(
                onHouseSelected = { navigationActions.navigateToHome(it) }
            )
        }
    }
}