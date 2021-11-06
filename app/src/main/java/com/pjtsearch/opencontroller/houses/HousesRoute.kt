package com.pjtsearch.opencontroller.houses

import android.view.HapticFeedbackConstants
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.*
import com.pjtsearch.opencontroller.components.ExpandingBar
import com.pjtsearch.opencontroller.components.ListItem
import com.pjtsearch.opencontroller.components.SmallIconButton
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
    var editing: EditingState by remember { mutableStateOf(EditingState.NotEditing) }
    var adding: HouseRef? by remember { mutableStateOf(null) }
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
            ExpandingBar(
                title = { Text("Houses") },
                navigationIcon = {
                    SmallIconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    when (val editingState = editing) {
                        is EditingState.NotEditing -> SmallIconButton(onClick = { /* doSomething() */ }) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Localized description"
                            )
                        }
                        is EditingState.Editing -> Row {
                            SmallIconButton(
                                onClick = {
                                    scope.launch {
                                        ctx.settingsDataStore.updateData { settings ->
                                            settings.toBuilder().removeHouseRefs(editingState.index)
                                                .build()
                                        }
                                        editing = EditingState.NotEditing
                                    }
                                }
                            ) {
                                Icon(Icons.Outlined.Delete, "Delete this house")
                            }
                            SmallIconButton(
                                onClick = {
                                    editing = EditingState.Editing(
                                        editingState.index,
                                        editingState.current,
                                        true
                                    )
                                }
                            ) {
                                Icon(Icons.Outlined.Edit, "Edit this house")
                            }
                        }
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
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxSize()
            ) {
                settings.value.houseRefsList.forEachIndexed { i, it ->
                    item {
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            selected = editing.takeIf { it is EditingState.Editing }
                                ?.let { (it as EditingState.Editing).index } == i,
                            clickAndSemanticsModifier =
                            Modifier.combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    when (editing) {
                                        is EditingState.NotEditing -> onHouseSelected(it)
                                        is EditingState.Editing -> editing = EditingState.NotEditing
                                    }
                                },
                                onLongClick = {
                                    view.performHapticFeedback(
                                        HapticFeedbackConstants.LONG_PRESS
                                    )
                                    editing = EditingState.Editing(i, it, false)
                                },
                                indication = rememberRipple()
                            )
                        ) {
                            Text(it.displayName)
                        }
                    }
                }
            }
        }
    )
    when (val editingState = editing) {
        is EditingState.Editing -> if (editingState.dialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    editing = EditingState.NotEditing
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            ctx.settingsDataStore.updateData { settings ->
                                settings.toBuilder()
                                    .removeHouseRefs(editingState.index)
                                    .addHouseRefs(editingState.current).build()
                            }
                            editing = EditingState.NotEditing
                        }
                    }) { Text("Save") }
                },
                text = {
                    ModifyHouseRef(
                        houseRef = editingState.current,
                        onChange = { n ->
                            editing =
                                EditingState.Editing(
                                    editingState.index,
                                    n,
                                    true
                                )
                        },
                    )
                },
            )
        }
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