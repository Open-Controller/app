package com.pjtsearch.opencontroller.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun ExpandableListItem(
        modifier: Modifier = Modifier,
        text: @Composable () -> Unit,
        icon: @Composable () -> Unit = {},
        opened: Boolean,
        onChange: (Boolean) -> Unit = {},
        content: @Composable ColumnScope.() -> Unit) =
    Column(modifier = modifier) {
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = { onChange(!opened) }
        ) {
            icon()
            text()
        }
        AnimatedVisibility(visible = opened) {
            Column(content = content)
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun ControlledExpandableListItem(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit = {},
    onChange: (Boolean) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    var opened by remember { mutableStateOf(false) }
    ExpandableListItem(modifier, text, icon, opened, { opened = it; onChange(it) }, content)
}