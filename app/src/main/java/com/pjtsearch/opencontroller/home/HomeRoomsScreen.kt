package com.pjtsearch.opencontroller.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.OtherHouses
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.LargeTopAppBarWithPadding

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeRoomsScreen(
    houseLoadingState: HouseLoadingState,
    onSelectController: (Pair<String, String>) -> Unit,
    onExit: () -> Unit
) {
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBarWithPadding(
                title = { Text("Rooms") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(
                            imageVector = Icons.Outlined.OtherHouses,
                            contentDescription = "Exit house"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                contentPadding = WindowInsets.statusBars.exclude(
                    WindowInsets.statusBars.only(
                        WindowInsetsSides.Bottom
                    )
                ).asPaddingValues()
            )
        },
        content = { innerPadding ->
            when (houseLoadingState) {
                is HouseLoadingState.Error ->
                    RoomsErrorLoading(
                        houseLoadingState.error, modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 15.dp)
                    )
                is HouseLoadingState.Loaded -> RoomControllerPicker(
                    houseLoadingState.house.rooms,
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(horizontal = 15.dp),
                    onSelectController = onSelectController
                )
                is HouseLoadingState.Loading -> RoomsLoading(
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(horizontal = 15.dp),
                )
            }
        }
    )
}
