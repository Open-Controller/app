package com.pjtsearch.opencontroller.home

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.twotone.SettingsRemote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.ui.components.ControllerView

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun HomeRoomsWithControllerScreen(
    uiState: HomeUiState,
    onSelectController: (Pair<String, String>) -> Unit,
    onInteractWithControllerMenu: (open: Boolean, items: List<Widget>) -> Unit,
    onExit: () -> Unit,
    onError: (Throwable) -> Unit
) {
    Row(
        Modifier.padding(
            WindowInsets.systemBars.asPaddingValues()
        )
    ) {
        Surface(
            modifier = Modifier
                .weight(3f)
                .padding(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(25.dp)
        ) {
            Column(Modifier.padding(10.dp)) {
                IconButton(
                    onClick = { onExit() },
                    modifier = Modifier
                        .padding(5.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                ) {
                    Icon(Icons.Outlined.ArrowBack, "Exit Home")
                }
                when (val state = uiState.houseLoadingState) {
                    is HouseLoadingState.Error ->
                        RoomsErrorLoading(
                            state.error, modifier = Modifier
                                .fillMaxHeight()
                                .padding(5.dp)
                        )
                    is HouseLoadingState.Loaded -> RoomControllerPicker(
                        state.house.rooms,
                        modifier = Modifier
                            .fillMaxHeight(),
                        onSelectController = onSelectController
                    )
                    is HouseLoadingState.Loading -> RoomsLoading(Modifier
                        .fillMaxHeight())
                }
            }
        }
        Column(
            Modifier
                .weight(5f)
                .padding(horizontal = 15.dp)) {
            Crossfade(targetState = uiState is HomeUiState.HasController) { hasController ->
                when (hasController) {
                    true -> {
                        check(uiState is HomeUiState.HasController)
                        AnimatedContent(targetState = uiState.selectedController, transitionSpec = {
                            slideInVertically(
                                tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing),
                                { height -> 1 * height / 10 },
                            ) + fadeIn(tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing), 0f) with
                                    slideOutVertically(
                                        tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing),
                                        { height -> -1 * height / 10 },
                                    ) + fadeOut(tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing), 0f)
                        }) { controller ->
                            Scaffold(
                                topBar = {
                                    CenterAlignedTopAppBar(
                                        title = { Text(controller.displayName) },
                                    )
                                },
                                content = { innerPadding ->
                                    Column(
                                        Modifier
                                            .padding(innerPadding)
                                    ) {
                                        ControllerView(
                                            controller,
                                            onError = onError,
                                            menuState = uiState.controllerMenuState,
                                            onInteractMenu = onInteractWithControllerMenu
                                        )
                                    }
                                }
                            )
                        }
                    }
                    false -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(40.dp)
                        ) {
                            Icon(
                                Icons.TwoTone.SettingsRemote,
                                "Controller Icon",
                                modifier = Modifier.size(250.dp),
                                tint = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                            )
                            Text(
                                text = "Choose a Controller",
                                color = MaterialTheme.colorScheme.onBackground.copy(0.4f),
                                style = MaterialTheme.typography.displayLarge
                            )
                        }
                    }
                }
            }
        }
    }
}