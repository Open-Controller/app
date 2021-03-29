package com.pjtsearch.opencontroller.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.ui.theme.shapes

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ExpandableListItem(
        modifier: Modifier = Modifier,
        text: @Composable () -> Unit,
        icon: @Composable () -> Unit = {},
        onOpen: () -> Unit = {},
        content: @Composable ColumnScope.() -> Unit
) {
    var opened by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        ListItem(
                modifier = Modifier
                        .clip(shape = shapes.small)
                        .toggleable(opened, onValueChange = { onOpen(); opened = it }),
                text = text,
                icon = icon
        )
        AnimatedVisibility(visible = opened, modifier = Modifier.padding(start = 30.dp)) {
            Column(content = content)
        }
    }
}