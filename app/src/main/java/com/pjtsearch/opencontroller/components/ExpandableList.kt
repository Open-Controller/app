package com.pjtsearch.opencontroller.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.pjtsearch.opencontroller.ui.theme.shapes

@ExperimentalMaterialApi
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
        ListItem(
                modifier = Modifier
                        .clip(shape = shapes.small)
                        .toggleable(opened, onValueChange = { onChange(it) }),
                text = text,
                icon = icon
        )
        AnimatedVisibility(visible = opened) {
            Column(content = content)
        }
    }


@ExperimentalAnimationApi
@ExperimentalMaterialApi
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