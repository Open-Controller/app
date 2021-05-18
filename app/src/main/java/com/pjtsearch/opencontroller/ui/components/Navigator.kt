package com.pjtsearch.opencontroller.ui.components

import androidx.compose.material.BackdropValue
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import com.github.zsoltk.compose.router.BackStack
import com.github.zsoltk.compose.router.Router
import com.pjtsearch.opencontroller.const.PageState
import kotlinx.coroutines.launch

@Composable
fun Navigator(id: String, default: PageState, content: @Composable () -> Unit) =
    Router(
        id,
        default
    ) { backStack ->
        CompositionLocalProvider(LocalRoute provides Route(backStack.last(), backStack)) {
            content()
        }
    }

data class Route(val page: PageState, val backStack: BackStack<PageState>)

val LocalRoute = compositionLocalOf<Route> {
    TODO()
}