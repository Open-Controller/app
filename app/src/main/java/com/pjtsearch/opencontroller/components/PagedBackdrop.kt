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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.pjtsearch.opencontroller.const.PageState
import com.pjtsearch.opencontroller.ui.components.*
import com.pjtsearch.opencontroller.ui.theme.shapes
import com.pjtsearch.opencontroller.ui.theme.typography

@ExperimentalMaterialApi
@Composable
fun PagedBackdrop(
    menuState: BackdropScaffoldState,
    page: PageState,
    backLayerContent: @Composable () -> Unit,
    frontLayerContent: @Composable () -> Unit,
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
                concealedTitle = { Text(page.frontPage.title, style = typography.h5) },
                revealedTitle = { Text("Menu", style = typography.h5) }
            )
        },
        backLayerContent = {
            Column(
                Modifier
                    .padding(10.dp)
                    .padding(bottom = 20.dp)
            ) {
                backLayerContent()
            }
        },
        frontLayerContent = {
            Box(Modifier.padding(25.dp)) {
                Crossfade(menuState.targetValue, animationSpec = tween(100)) {
                    when (it) {
                        BackdropValue.Concealed -> frontLayerContent()
                        BackdropValue.Revealed -> Column {
                            Box(Modifier.fillMaxWidth()) {
                                Text(page.frontPage.title, style = typography.h5)
                                Icon(
                                    Icons.Outlined.KeyboardArrowUp,
                                    "Close menu",
                                    modifier = Modifier.align(alignment = Alignment.CenterEnd)
                                )
                            }
                            Column(Modifier.padding(top = 80.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                page.frontPage.bottomIcon?.let { ic ->
                                    Icon(
                                        ic, page.frontPage.bottomText ?: "Bottom Icon",
                                        Modifier.align(Alignment.CenterHorizontally).size(200.dp),
                                        MaterialTheme.colors.onSurface.copy(0.3f)
                                    )
                                }
                                page.frontPage.bottomText?.let { text ->
                                    Text(
                                        text,
                                        style = typography.h5,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )