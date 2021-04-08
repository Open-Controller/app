package com.pjtsearch.opencontroller.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.pjtsearch.opencontroller.const.BackgroundPage
import com.pjtsearch.opencontroller.const.Page
import com.pjtsearch.opencontroller.ui.components.*
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller.ui.theme.typography

@ExperimentalMaterialApi
@Composable
fun PagedBackdrop(
    menuState: BackdropScaffoldState,
    page: Page,
    backgroundPage: BackgroundPage,
    backLayerContent: @Composable (BackgroundPage) -> Unit,
    frontLayerContent: @Composable (Page) -> Unit,
) =
    BackdropScaffold(
        scaffoldState = menuState,
        headerHeight = 100.dp,
        modifier = Modifier.statusBarsPadding(),
        backLayerBackgroundColor = MaterialTheme.colors.background,
        frontLayerElevation = if (MaterialTheme.colors.isLight) 18.dp else 1.dp,
        frontLayerShape = shapes.large,
        appBar = {
            AppBar(
                menuState = menuState,
                concealedTitle = { Text(page.title, style = typography.h5) },
                revealedTitle = { Text("Menu", style = typography.h5) }
            )
        },
        backLayerContent = {
            Column(
                Modifier
                    .padding(10.dp)
                    .padding(bottom = 20.dp)
            ) {
                backLayerContent(backgroundPage)
            }
        },
        frontLayerContent = {
            Box(Modifier.padding(25.dp)) {
                Crossfade(menuState.targetValue, animationSpec = tween(100)) {
                    when (it) {
                        BackdropValue.Concealed -> frontLayerContent(page)
                        BackdropValue.Revealed -> Box(Modifier.fillMaxWidth()) {
                            Text(page.title, style = typography.h5)
                            Icon(
                                Icons.Outlined.KeyboardArrowUp,
                                "Close menu",
                                modifier = Modifier.align(alignment = Alignment.CenterEnd)
                            )
                        }
                    }
                }
            }
        }
    )