package com.pjtsearch.opencontroller.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.extensions.icons

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconPicker(
    icon: String?,
    onPick: (icon: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        itemsIndexed(icons.keys.toList()) { _, it ->
            val opacity by animateFloatAsState(targetValue = if (icon == it) 1f else 0f)
            Button(
                onClick = {
                    if (icon == it) {
                        onPick(null)
                    } else {
                        onPick(it)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(opacity),
                    contentColor = if (icon == it) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                )
            ) {
                OpenControllerIcon(it, it)
            }
        }
    }
}