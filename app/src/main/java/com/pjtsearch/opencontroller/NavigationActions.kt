package com.pjtsearch.opencontroller

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.pjtsearch.opencontroller.settings.HouseRef
import java.net.URLEncoder

object Destinations {
    const val HOME_ROUTE = "home"
    const val HOUSES_ROUTE = "houses"
}

class NavigationActions(navController: NavHostController) {
    val navigateToHome: (HouseRef) -> Unit = {
        navController.navigate(Destinations.HOME_ROUTE + "/" + URLEncoder.encode(it.toByteArray().decodeToString(), "utf-8")) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    val navigateToHouses: () -> Unit = {
        navController.navigate(Destinations.HOUSES_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}