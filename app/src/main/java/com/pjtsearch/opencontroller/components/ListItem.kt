package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils

@Composable
fun ListItem(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(15.dp),
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    selected: Boolean = false,
    clickAndSemanticsModifier: Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(shape)
            .then(clickAndSemanticsModifier),
        shape = shape,
        color = Color(
            ColorUtils.blendARGB(
                color.toArgb(),
                MaterialTheme.colorScheme.onSecondaryContainer.toArgb(),
                if (selected) 0.15f else 0f
            )
        )
    ) {
        Box(Modifier.padding(15.dp)) {
            content()
        }
    }
}
