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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.LargeTopAppBarWithPadding
import com.pjtsearch.opencontroller.components.ListItem
import com.pjtsearch.opencontroller.components.SmallIconButton
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.components.ModifyHouseRef
import kotlinx.coroutines.launch

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
    var selected: List<Int> by remember { mutableStateOf(listOf()) }
    var adding: HouseRef? by remember { mutableStateOf(null) }
    var editing: Pair<Int, HouseRef>? by remember { mutableStateOf(null) }

    val view = LocalView.current
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
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
                        0 -> SmallIconButton(
                            onClick = { }
                        ) {
                            Icon(Icons.Outlined.MoreVert, "More")
                        }
                        1 -> Row {
                            SmallIconButton(
                                onClick = {
                                    editing = Pair(selected[0], settings.value.getHouseRefs(selected[0]))
                                }
                            ) {
                                Icon(Icons.Outlined.Edit, "Edit this house")
                            }
                        }
                    }
                    if (selected.isNotEmpty()) {
                        SmallIconButton(
                            onClick = {
                                scope.launch {
                                    ctx.settingsDataStore.updateData { settings ->
                                        settings.toBuilder().clearHouseRefs().addAllHouseRefs(
                                            settings.houseRefsList.filterIndexed { i, _ ->
                                                !selected.contains(i)
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
                text = { Text("Add house") },
                icon = { Icon(Icons.Outlined.Add, "Add a house") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            BoxWithConstraints {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(maxOf((maxWidth / 150.dp).toInt(), 1)),
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxHeight(),
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    settings.value.houseRefsList.forEachIndexed { i, it ->
                        item {
                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                selected = selected.contains(i),
                                clickAndSemanticsModifier =
                                Modifier.combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        if (selected.isEmpty()) {
                                            onHouseSelected(it)
                                        } else if (!selected.contains(i)) {
                                            selected = selected + i
                                        } else {
                                            selected = selected - i
                                        }
                                    },
                                    onLongClick = {
                                        view.performHapticFeedback(
                                            HapticFeedbackConstants.LONG_PRESS
                                        )
                                        if (!selected.contains(i)) {
                                            selected = selected + i
                                        } else {
                                            selected = selected - i
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
                                        text = "No Room Icon",
                                        size = 2
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
                                .setHouseRefs(editingState.first, editingState.second)
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
                                .addHouseRefs(addingState).build()
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