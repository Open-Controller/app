package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller.ui.components.Widget as WidgetDisplay
import com.pjtsearch.opencontroller_lib_proto.Widget
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun rememberWidgetBottomSheetState(
    state: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded}
    ),
    sheetContent: MutableState<List<Widget>?> = remember{ mutableStateOf(null) }
): WidgetBottomSheetState =
    remember{ WidgetBottomSheetState(state, sheetContent) }

@ExperimentalMaterialApi
data class WidgetBottomSheetState(
    val state: ModalBottomSheetState,
    val sheetContent: MutableState<List<Widget>?>
) {
    suspend fun open(widgets: List<Widget>) {
        sheetContent.value = widgets
        state.show()
    }
}

@ExperimentalMaterialApi
@Composable
fun WidgetBottomSheet(modifier: Modifier = Modifier,
                      executor: OpenControllerLibExecutor?,
                      state: WidgetBottomSheetState,
                      onError: (Throwable) -> Unit,
                      content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(modifier = modifier, sheetState = state.state, sheetContent = {
        Box(
            Modifier
                .padding(20.dp)
                .fillMaxHeight(0.7f)) {
            if (state.sheetContent.value != null && executor !== null)
                state.sheetContent.value!!.map {
                    this@ModalBottomSheetLayout.WidgetDisplay(
                        it,
                        executor,
                        onOpenMenu = { w -> scope.launch { state.open(w) }},
                        onError = { onError(it) }
                    )
                }
        }
    }) { content() }
}