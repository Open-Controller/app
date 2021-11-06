package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun ListItem(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(15.dp),
    color: Color = MaterialTheme.colorScheme.primary,
    selected: Boolean = false,
    clickAndSemanticsModifier: Modifier,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColorFor(color.copy(alpha = if (selected) 0.3f else 0.15f))
    ) {
        Box(
            modifier
                .background(
                    color = color.copy(alpha = if (selected) 0.3f else 0.15f),
                    shape = shape
                )
                .clip(shape)
                .then(clickAndSemanticsModifier),
            propagateMinConstraints = true
        ) {
            Box(Modifier.padding(15.dp)) {
                content()
            }
        }
    }
}