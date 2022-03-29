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
    val navigateToHome: (home: HouseRef, saveOldState: Boolean) -> Unit = { home, saveOldState ->
        navController.navigate(Destinations.HOME_ROUTE + "/" + URLEncoder.encode(home.toByteArray().decodeToString(), "utf-8")) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = saveOldState
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    val navigateToHouses: (saveOldState: Boolean) -> Unit = { saveOldState ->
        navController.navigate(Destinations.HOUSES_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = saveOldState
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}