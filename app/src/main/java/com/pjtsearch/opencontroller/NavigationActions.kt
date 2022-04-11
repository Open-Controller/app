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

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.pjtsearch.opencontroller.settings.HouseRef
import java.net.URLEncoder

/**
 * The destinations for the navigation
 */
object Destinations {
    /**
     * The destination for the home route
     */
    const val HOME_ROUTE = "home"

    /**
     * The destination for the houses route
     */
    const val HOUSES_ROUTE = "houses"

    /**
     * The destination for the last home route
     */
    const val LAST_HOME_ROUTE = "lastHome"
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
     * Navigates to the Home route
     */
    val navigateToHome: (home: HouseRef, saveOldState: Boolean) -> Unit =
        { home, saveOldState ->
            navController.navigate(
                Destinations.HOME_ROUTE + "/" + URLEncoder.encode(
                    home.toByteArray().decodeToString(), "utf-8"
                )
            ) {
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

    /**
     * Navigates to the Houses route
     */
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