package com.pjtsearch.opencontroller.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
) {
    // UiState of the HomeScreen
    val uiState by homeViewModel.uiState.collectAsState()

    HomeRoute(
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        onSelectController = { homeViewModel.selectController(it) },
        onInteractWithRooms = { homeViewModel.interactedWithRooms() },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeRoute(
    uiState: HomeUiState,
    isExpandedScreen: Boolean,
    onSelectController: (Pair<String, String>) -> Unit,
    onInteractWithRooms: () -> Unit,
) {
    val homeScreenType = getHomeScreenType(isExpandedScreen, uiState)
    Crossfade(homeScreenType) {
        when (it) {
            HomeScreenType.RoomsWithController -> TODO()
            HomeScreenType.Rooms -> {
                HomeRoomsScreen(
                    house = uiState.house,
                    isLoading = uiState.isLoading,
                    onSelectController = onSelectController
                )
            }
            HomeScreenType.Controller -> {
                // Guaranteed by above condition for home screen type
                check(uiState is HomeUiState.HasController)

                HomeControllerScreen(
                    controller = uiState.selectedController,
                    houseScope = uiState.house.scope,
                    isExpandedScreen = isExpandedScreen,
                    onBack = onInteractWithRooms
                )

                // If we are just showing the detail, have a back press switch to the list.
                // This doesn't take anything more than notifying that we "interacted with the list"
                // since that is what drives the display of the feed
                BackHandler {
                    onInteractWithRooms()
                }
            }
        }
    }
}

/**
 * A precise enumeration of which type of screen to display at the home route.
 *
 * There are 3 options:
 * - [FeedWithArticleDetails], which displays both a list of all articles and a specific article.
 * - [Feed], which displays just the list of all articles
 * - [ArticleDetails], which displays just a specific article.
 */
private enum class HomeScreenType {
    RoomsWithController,
    Rooms,
    Controller
}

/**
 * Returns the current [HomeScreenType] to display, based on whether or not the screen is expanded
 * and the [HomeUiState].
 */
@Composable
private fun getHomeScreenType(
    isExpandedScreen: Boolean,
    uiState: HomeUiState
): HomeScreenType = when (isExpandedScreen) {
    false -> {
        when (uiState) {
            is HomeUiState.HasController -> {
                if (uiState.isControllerOpen) {
                    HomeScreenType.Controller
                } else {
                    HomeScreenType.Rooms
                }
            }
            is HomeUiState.NoController -> HomeScreenType.Rooms
        }
    }
    true -> HomeScreenType.RoomsWithController
}
