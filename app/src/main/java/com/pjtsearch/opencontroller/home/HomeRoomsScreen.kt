package com.pjtsearch.opencontroller.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.systemBarsPadding
import com.pjtsearch.opencontroller.Controller
import com.pjtsearch.opencontroller.House
import com.pjtsearch.opencontroller.components.CenterBar
import com.pjtsearch.opencontroller.components.ControlledExpandableListItem
import com.pjtsearch.opencontroller.components.SmallIconButton
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeRoomsScreen(house: House?, isLoading: Boolean, onSelectController: (Pair<String, String>) -> Unit, onExit: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior { true }
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterBar(
                title = { house?.displayName?.let { Text(it) } },
                navigationIcon = {
                    SmallIconButton(onClick = onExit) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Exit house"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.statusBars,
                    applyStart = true,
                    applyTop = true,
                    applyEnd = true,
                )
            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                house?.rooms?.map { (roomId, room) ->
                    item {
                        ControlledExpandableListItem(
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            { Text(room.displayName) },
                            { OpenControllerIcon(room.icon, room.displayName) }) {
                            Row(
                                Modifier
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                room.controllers.map { (controllerId, controller) ->
                                    ControllerButton(
                                        controller,
                                        controllerId,
                                        roomId,
                                        onSelectController
                                    )
                                }
                            }
                        }
                    }
                } ?: item {Text("Loading")}
            }
        }
    )
}

@Composable
fun ControllerButton(
    controller: Controller,
    controllerId: String,
    roomId: String,
    onSelectController: (Pair<String, String>) -> Unit
) =
    (controller.brandColor?.let {
        Color(
            android.graphics.Color.parseColor(
                controller.brandColor
            )
        )
    } ?: MaterialTheme.colorScheme.secondary).let { color ->
        Button(
            modifier = Modifier
                .width(120.dp)
                .height(100.dp),
            onClick = { onSelectController(Pair(roomId, controllerId)) },
//            FIXME: Figure out colors
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = if (color.luminance() < 0.3) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black
            )
        ) {
            Text(controller.displayName)
        }
    }