package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun DialogSheet(modifier: Modifier = Modifier,
                 state: ModalBottomSheetState,
                 content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    var dialogContent: @Composable ColumnScope.(state: ModalBottomSheetState) -> Unit by remember { mutableStateOf({}) }
    ModalBottomSheetLayout(
        modifier = modifier,
        scrimColor = Color.Black.copy(0.5f),
        sheetState = state,
        sheetContent = {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                dialogContent(state)
            }
        }
    ) {
        CompositionLocalProvider(LocalDialogOpener provides {
            scope.launch {
                dialogContent = it
                delay(100)
                state.show()
            }
        }) {
            content()
        }
    }
}

@ExperimentalMaterialApi
val LocalDialogOpener = compositionLocalOf<(@Composable ColumnScope.(state: ModalBottomSheetState) -> Unit) -> Unit> { {} }