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

object SettingsDestinations {
    /**
     * The destination for the manage houses route
     */
    const val MANAGE_HOUSES_ROUTE = "manageHouses"

    /**
     * The destination for the add house route
     */
    const val ADD_HOUSE_ROUTE = "addHouse"

    /**
     * The destination for the edit house route
     */
    const val EDIT_HOUSE_ROUTE = "editHouse"
}

/**
 * The destinations for the navigation
 */
object Destinations {
    /**
     * The destination for the home route
     */
    const val HOME_ROUTE = "home"

    /**
     * The destination for the settings route
     */
    const val SETTINGS_ROUTE = "settings"

    /**
     * The destination for the home route
     */
    const val WELCOME_ROUTE = "welcome"

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
    val navigateToHome: (houseRefId: String, saveOldState: Boolean, allowDuplicate: Boolean) -> Unit =
        { houseRefId, saveOldState, allowDuplicate ->
            navController.navigate(
                Destinations.HOME_ROUTE + "/" + houseRefId
            ) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = saveOldState
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = !allowDuplicate
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }

    val navigateToWelcome: () -> Unit =
        {
            navController.navigate(
                Destinations.WELCOME_ROUTE
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
                restoreState = true
            }
        }

    val navigateToSettings: (destination: String?) -> Unit =
        { destination ->
            navController.navigate(
                Destinations.SETTINGS_ROUTE + if (destination != null) {
                    "/$destination"
                } else {
                    ""
                }
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
                restoreState = true
            }
        }
}