package com.pjtsearch.opencontroller.houses

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.*
import com.pjtsearch.opencontroller.components.ExpandingBar
import com.pjtsearch.opencontroller.components.SmallIconButton
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller.settings.Settings
import com.pjtsearch.opencontroller.settingsDataStore
import com.pjtsearch.opencontroller.ui.components.ModifyHouseRef
import kotlinx.coroutines.launch

sealed interface EditMode {
    operator fun not(): EditMode =
        when (this) {
            is Editing -> NotEditing
            is NotEditing -> Editing(DialogMode.NoDialog)
        }

    object NotEditing : EditMode
    data class Editing(val dialogMode: DialogMode) : EditMode
}

sealed interface DialogMode {
    data class EditDialog(val index: Int, val current: HouseRef) : DialogMode
    data class AddDialog(val current: HouseRef) : DialogMode
    object NoDialog : DialogMode
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HousesRoute(onHouseSelected: (HouseRef) -> Unit) {
    val ctx = LocalContext.current
    val settings =
        ctx.settingsDataStore.data.collectAsState(initial = Settings.getDefaultInstance())
    val scope = rememberCoroutineScope()
    var editMode: EditMode by remember { mutableStateOf(EditMode.NotEditing) }
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
                    SmallIconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Localized description"
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
        floatingActionButton = {
            if (editMode is EditMode.Editing) {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 30.dp),
                    onClick = {
                        editMode =
                            EditMode.Editing(DialogMode.AddDialog(HouseRef.getDefaultInstance()))
                    },
                    text = { Text("Add house") },
                    icon = { Icon(Icons.Outlined.Add, "Add a house") }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Column(
                        Modifier
                            .systemBarsPadding()
                            .padding(5.dp)
                            .fillMaxSize()
                    ) {
                        Button(onClick = { editMode = !editMode }) {
                            Text("Edit")
                        }
                        settings.value.houseRefsList.forEachIndexed { i, it ->
                            FilledTonalButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                onClick = {
                                    when (editMode) {
                                        is EditMode.Editing -> editMode =
                                            EditMode.Editing(DialogMode.EditDialog(i, it))
                                        is EditMode.NotEditing -> onHouseSelected(it)
                                    }
                                }
                            ) {
                                Box(Modifier.fillMaxWidth()) {
                                    Text(it.displayName, Modifier.align(Alignment.CenterStart))
                                    if (editMode is EditMode.Editing) {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    ctx.settingsDataStore.updateData { settings ->
                                                        settings.toBuilder().removeHouseRefs(i)
                                                            .build()
                                                    }
                                                }
                                            },
                                            Modifier.align(Alignment.CenterEnd)
                                        ) {
                                            Icon(Icons.Outlined.Delete, "Delete this house")
                                        }
                                    }
                                }
                            }
                        }
                        if (editMode is EditMode.Editing) {
                            when (val mode = (editMode as EditMode.Editing).dialogMode) {
                                is DialogMode.EditDialog -> AlertDialog(
                                    onDismissRequest = {
                                        editMode = EditMode.Editing(DialogMode.NoDialog)
                                    },
                                    confirmButton = {
                                        Button(onClick = {
                                            scope.launch {
                                                ctx.settingsDataStore.updateData { settings ->
                                                    settings.toBuilder()
                                                        .removeHouseRefs(mode.index)
                                                        .addHouseRefs(mode.current).build()
                                                }
                                                editMode = EditMode.Editing(DialogMode.NoDialog)
                                            }
                                        }) { Text("Save") }
                                    },
                                    text = {
                                        ModifyHouseRef(
                                            houseRef = mode.current,
                                            onChange = { n ->
                                                editMode =
                                                    EditMode.Editing(
                                                        DialogMode.EditDialog(
                                                            mode.index,
                                                            n
                                                        )
                                                    )
                                            },
                                        )
                                    },
                                )
                                is DialogMode.AddDialog -> AlertDialog(
                                    onDismissRequest = {
                                        editMode = EditMode.Editing(DialogMode.NoDialog)
                                    },
                                    confirmButton = {
                                        Button(onClick = {
                                            scope.launch {
                                                ctx.settingsDataStore.updateData { settings ->
                                                    settings.toBuilder()
                                                        .addHouseRefs(mode.current).build()
                                                }
                                                editMode = EditMode.Editing(DialogMode.NoDialog)
                                            }
                                        }) { Text("Save") }
                                    },
                                    text = {
                                        ModifyHouseRef(
                                            houseRef = mode.current,
                                            onChange = { n ->
                                                editMode =
                                                    EditMode.Editing(DialogMode.AddDialog(n))
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}