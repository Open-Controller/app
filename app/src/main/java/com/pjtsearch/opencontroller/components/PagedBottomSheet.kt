package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.const.BottomSheetPage

@ExperimentalMaterialApi
@Composable
fun PagedBottomSheet(modifier: Modifier = Modifier,
                     state: ModalBottomSheetState,
                     page: BottomSheetPage,
                     sheetContent: @Composable ColumnScope.(BottomSheetPage) -> Unit,
                     content: @Composable () -> Unit) =
    ModalBottomSheetLayout(
        modifier = modifier,
        scrimColor = Color.Black.copy(0.5f),
        sheetState = state,
        sheetContent = {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth()) {
                sheetContent(page)
            }
        }
    ) { content() }