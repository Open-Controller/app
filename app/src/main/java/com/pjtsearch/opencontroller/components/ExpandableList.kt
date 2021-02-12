package com.pjtsearch.opencontroller.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalAnimationApi
@Composable
fun ExpandableListItem(
        text: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        onOpen: () -> Unit = {},
        content: @Composable ColumnScope.() -> Unit
) {
    var opened by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        ListItem(
                modifier = Modifier.toggleable(opened, onValueChange = { onOpen(); opened = it }),
                text = text
        )
        AnimatedVisibility(visible = opened, modifier = Modifier.padding(start = 30.dp)) {
            Column(content = content)
        }
    }
}