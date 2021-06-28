package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.compose.router.BackStack
import com.pjtsearch.opencontroller.components.PagedBackdrop
import com.pjtsearch.opencontroller.const.BackgroundPage
import com.pjtsearch.opencontroller.const.BottomSheetPage
import com.pjtsearch.opencontroller.const.FrontPage
import com.pjtsearch.opencontroller.const.PageState
import com.pjtsearch.opencontroller.settings.HouseRef
import com.pjtsearch.opencontroller_lib_android.OpenControllerLibExecutor
import com.pjtsearch.opencontroller_lib_proto.Widget

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun Backdrop(
    menuState: BackdropScaffoldState,
    page: PageState,
    pageBackStack: BackStack<PageState>,
    houseRefs: List<HouseRef>,
    onOpenMenu: (List<Widget>) -> Unit,
    onError: (Throwable) -> Unit,
    onAddHome: () -> Unit,
    onExitHome: () -> Unit,
    onRevealMenu: () -> Unit,
    onBottomSheetPage: (BottomSheetPage) -> Unit
) = PagedBackdrop(
    menuState = menuState,
    page = page,
    backLayerContent = {
        Row(
            Modifier.padding(start = 8.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button({
                pageBackStack.push(page.copy(FrontPage.Settings, backdropValue = BackdropValue.Concealed))
            }) {
                Text("Settings")
            }
            Crossfade(page.backgroundPage) {
                if (it is BackgroundPage.Rooms) {
                    Button({
                        pageBackStack.push(page.copy(FrontPage.EmptyGreeter, BackgroundPage.Homes))
                    }) {
                        Text("Exit house")
                    }
                }
            }
        }
        Crossfade(page.backgroundPage) {
            when (it) {
                is BackgroundPage.Homes -> HousesMenu(houseRefs,
                    { e -> onError(e) }) { newHouse ->
                    pageBackStack.push(page.copy(
                        FrontPage.HomeGreeter(newHouse), BackgroundPage.Rooms(
                        newHouse,
                        OpenControllerLibExecutor(newHouse)
                    )))
                }
                is BackgroundPage.Rooms -> RoomsMenu(it.house) { controller ->
                    pageBackStack.push(page.copy(FrontPage.Controller(controller), backdropValue = BackdropValue.Concealed))
                }
            }
        }
    },
    frontLayerContent = {
        when (val it = page.frontPage) {
            is FrontPage.EmptyGreeter -> EmptyGreeterView(
                onRevealMenu = { onRevealMenu() },
                onAddHome = { onAddHome() }
            )
            is FrontPage.HomeGreeter -> HomeGreeterView(
                house = it.house,
                onRevealMenu = { onRevealMenu() },
                onExitHome = { onExitHome() }
            )
            is FrontPage.Settings -> SettingsView(
                onBottomSheetPage = { p -> onBottomSheetPage(p) }
            )
            is FrontPage.Controller -> ControllerView(
                it.controller,
                (page.backgroundPage as BackgroundPage.Rooms).executor,
                onOpenMenu = { w -> onOpenMenu(w) },
                onError = { e -> onError(e) }
            )
        }
    }
)
