package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.ui.theme.typography

@Composable
fun GreeterLayout(topContent: @Composable ColumnScope.() -> Unit,
                  bottomContent: @Composable RowScope.() -> Unit) =
    Column {
        Column(
            Modifier.weight(1f).align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            CompositionLocalProvider(
                LocalContentAlpha provides 0.25f,
                LocalTextStyle provides typography.h4
            ) {
                topContent()
            }
        }
        Row(Modifier.weight(0.5f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            bottomContent()
        }
    }