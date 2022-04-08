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

package com.pjtsearch.opencontroller.houses

import android.view.HapticFeedbackConstants
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.LargeTopAppBarWithPadding
import com.pjtsearch.opencontroller.components.ListItem
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.extensions.houseIcons
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.components.ModifyHouseRef
import kotlinx.coroutines.launch
import java.util.*

//sealed interface EditMode {
//    operator fun not(): EditMode =
//        when (this) {
//            is Editing -> NotEditing
//            is NotEditing -> Editing(DialogMode.NoDialog)
//        }
//
//    object NotEditing : EditMode
//    data class Editing(val dialogMode: DialogMode) : EditMode
//}

//sealed interface DialogMode {
//    data class EditDialog(val index: Int, val current: HouseRef) : DialogMode
//    data class AddDialog(val current: HouseRef) : DialogMode
//    object NoDialog : DialogMode
//}

sealed interface EditingState {
    object NotEditing : EditingState
    data class Editing(val index: Int, val current: HouseRef, val dialogOpen: Boolean) :
        EditingState
}

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun HousesRoute(onHouseSelected: (HouseRef) -> Unit) {
    val ctx = LocalContext.current
    val settings =
        ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scope = rememberCoroutineScope()
    var selected: List<String> by rememberSaveable { mutableStateOf(listOf()) }
    var adding: HouseRef? by rememberSaveable { mutableStateOf(null) }
    var editing: Pair<String, HouseRef>? by rememberSaveable { mutableStateOf(null) }

    val view = LocalView.current
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    val beforeHouseSelected = { id: String, houseRef: HouseRef ->
        scope.launch {
            ctx.settingsDataStore.updateData { oldSettings ->
                oldSettings.toBuilder().clone().setLastHouse(id).build()
            }
        }
        onHouseSelected(houseRef)
    }

    LaunchedEffect(ctx.settingsDataStore) {
        scope.launch {
            ctx.settingsDataStore.updateData { oldSettings ->
                oldSettings.toBuilder().clone().clearLastHouse().build()
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            LargeTopAppBarWithPadding(
                title = { Text("Houses") },
                actions = {
                    when (selected.size) {
                        0 -> IconButton(
                            onClick = { }
                        ) {
                            Icon(Icons.Outlined.MoreVert, "More")
                        }
                        1 -> Row {
                            IconButton(
                                onClick = {
                                    editing = Pair(
                                        selected[0],
                                        settings.value.houseRefsMap[selected[0]]!!
                                    )
                                }
                            ) {
                                Icon(Icons.Outlined.Edit, "Edit this house")
                            }
                        }
                    }
                    if (selected.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    ctx.settingsDataStore.updateData { settings ->
                                        settings.toBuilder().clearHouseRefs()
                                            .putAllHouseRefs(
                                                settings.houseRefsMap.filter { (id, _) ->
                                                    !selected.contains(id)
                                                }
                                            ).build()
                                    }
                                    selected = listOf()
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.Delete, "Delete this house")
                        }
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 30.dp),
                onClick = {
                    adding = HouseRef.getDefaultInstance()
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                text = { Text("Add house") },
                icon = { Icon(Icons.Outlined.Add, "Add a house") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            BoxWithConstraints(Modifier.padding(innerPadding)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(maxOf((maxWidth / 150.dp).toInt(), 1)),
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(horizontal = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    settings.value.houseRefsMap.forEach { (id, it) ->
                        item {
                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                selected = selected.contains(id),
                                clickAndSemanticsModifier =
                                Modifier.combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        if (selected.isEmpty()) {
                                            beforeHouseSelected(id, it)
                                        } else if (!selected.contains(id)) {
                                            selected = selected + id
                                        } else {
                                            selected = selected - id
                                        }
                                    },
                                    onLongClick = {
                                        view.performHapticFeedback(
                                            HapticFeedbackConstants.LONG_PRESS
                                        )
                                        if (!selected.contains(id)) {
                                            selected = selected + id
                                        } else {
                                            selected = selected - id
                                        }
                                    },
                                    indication = rememberRipple()
                                )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OpenControllerIcon(
                                        icon = it.icon,
                                        text = it.icon ?: "No Room Icon",
                                        size = 1,
                                        iconSet = houseIcons
                                    )
                                    Text(it.displayName)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
    val editingState = editing
    if (editingState != null) {
        AlertDialog(
            onDismissRequest = {
                editing = null
                selected = listOf()
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        ctx.settingsDataStore.updateData { settings ->
                            settings.toBuilder()
                                .putHouseRefs(editingState.first, editingState.second)
                                .build()
                        }
                        editing = null
                        selected = listOf()
                    }
                }) { Text("Save") }
            },
            text = {
                ModifyHouseRef(
                    houseRef = editingState.second,
                    onChange = { n ->
                        editing =
                            Pair(
                                editingState.first,
                                n
                            )
                    },
                )
            },
        )
    }
    when (val addingState = adding) {
        is HouseRef -> AlertDialog(
            onDismissRequest = {
                adding = null
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        ctx.settingsDataStore.updateData { settings ->
                            settings.toBuilder()
                                .putHouseRefs(UUID.randomUUID().toString(), addingState)
                                .build()
                        }
                        adding = null
                    }
                }) { Text("Save") }
            },
            text = {
                ModifyHouseRef(
                    houseRef = addingState,
                    onChange = { n ->
                        adding = n
                    }
                )
            }
        )
    }
}