package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ExpandingBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
//    val statusBar = LocalWindowInsets.current
//    val statusBarHeight: Dp
//
//    LocalDensity.current.run {
//        statusBarHeight = statusBar.statusBars.layoutInsets.top.toDp()
//    }
    val scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    val backgroundColor by colors.containerColor(scrollFraction)

    Surface(
        color = backgroundColor,
        modifier = modifier
    ) {
        LargeTopAppBar(
            title = title,
            modifier = Modifier.padding(contentPadding),
            navigationIcon = navigationIcon,
            actions = actions,
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    }
}
