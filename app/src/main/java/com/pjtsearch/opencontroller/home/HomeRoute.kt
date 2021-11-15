package com.pjtsearch.opencontroller.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    onError: (Throwable) -> Unit,
) {
    // UiState of the HomeScreen
    val uiState by homeViewModel.uiState.collectAsState()

    HomeRoute(
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        onSelectController = { homeViewModel.selectController(it) },
        onInteractWithRooms = { homeViewModel.interactedWithRooms() },
        onError = onError
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeRoute(
    uiState: HomeUiState,
    isExpandedScreen: Boolean,
    onSelectController: (Pair<String, String>) -> Unit,
    onInteractWithRooms: () -> Unit,
    onError: (Throwable) -> Unit,
) {
    val homeScreenType = getHomeScreenType(isExpandedScreen, uiState)
    AnimatedContent(
        targetState = homeScreenType,
        modifier = Modifier.fillMaxHeight(),
        transitionSpec = {
            val intSpec = tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing)
            val floatSpec = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            val dir = if (targetState == HomeScreenType.Rooms) {
                -1
            } else {
                1
            }
            slideInVertically(
                { height -> dir * height / 10 },
                intSpec
            ) + fadeIn(0f, floatSpec) with
                    slideOutVertically(
                        { height -> -dir * height / 10 },
                        intSpec
                    ) + fadeOut(0f, floatSpec)
        },
    ) {
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
                    isExpandedScreen = isExpandedScreen,
                    onBack = onInteractWithRooms,
                    onError = onError
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
