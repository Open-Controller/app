package com.pjtsearch.opencontroller.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.pjtsearch.opencontroller.Device
import com.pjtsearch.opencontroller.Widget
import com.pjtsearch.opencontroller.components.PagedBottomSheet
import com.pjtsearch.opencontroller.const.BackgroundPage
import com.pjtsearch.opencontroller.const.BottomSheetPage
import com.pjtsearch.opencontroller.const.PageState
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller_lib_proto.WidgetExpr
import com.pjtsearch.opencontroller.ui.components.Widget as WidgetDisplay

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun BottomSheet(
    sheetState: ModalBottomSheetState,
    page: PageState,
    onOpenMenu: (List<Widget>) -> Unit,
    onError: (Throwable) -> Unit,
    onAddHouseRef: (HouseRef) -> Unit,
    onEditHouseRef: (HouseRef, Int) -> Unit,
    content: @Composable () -> Unit,
) =
    PagedBottomSheet(
        state = sheetState,
        page = page.bottomSheetPage,
        sheetContent = { pg ->
            val bgPage = page.backgroundPage
            when (pg) {
//                TODO: Coordinate with fg Controller page instead?
                is BottomSheetPage.Widgets -> if (bgPage is BackgroundPage.Rooms) {
                    pg.widgets.map { w ->
                        WidgetDisplay(
                            w,
                            bgPage.house.scope,
                            Modifier.fillMaxWidth(),
                            onOpenMenu = { onOpenMenu(it) },
                            onError = { onError(it) }
                        )
                    }
                }
                is BottomSheetPage.AddHouseRef -> ModifyHouseRef(
                    pg.houseRef.value,
                    { pg.houseRef.value = it }) {
                    onAddHouseRef(it)
                }
                is BottomSheetPage.EditHouseRef -> ModifyHouseRef(
                    pg.houseRef.value,
                    { pg.houseRef.value = it }) {
                    onEditHouseRef(it, pg.index)
                }
            }
        }
    ) {
        content()
    }