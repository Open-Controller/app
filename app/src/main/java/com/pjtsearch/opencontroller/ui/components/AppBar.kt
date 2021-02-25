package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun AppBar(
        menuState: BackdropScaffoldState,
        concealedTitle: @Composable BoxScope.() -> Unit,
        revealedTitle: @Composable BoxScope.() -> Unit) =
        TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp,
                title = {
                    Crossfade(menuState.targetValue, content = {
                        when (it) {
                            BackdropValue.Concealed -> Box(content = concealedTitle)
                            BackdropValue.Revealed -> Box(content = revealedTitle)
                        }
                    })
                }
        )