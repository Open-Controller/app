package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun LargeButton(modifier: Modifier = Modifier,
                onClick: () -> Unit,
                icon: @Composable () -> Unit,
                text: @Composable () -> Unit) =
    Button(modifier = modifier, onClick = onClick) {
        Column(verticalArrangement = Arrangement.spacedBy(25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            icon()
            text()
        }
    }