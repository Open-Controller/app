package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LargeTopAppBarWithPadding(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
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
