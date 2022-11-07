/*
 * Copyright (c) 2022 PJTSearch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.pjtsearch.opencontroller.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils

@Composable
private fun TabButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(15.dp),
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    height: Dp,
    selected: Boolean = false,
    clickAndSemanticsModifier: Modifier = Modifier,
    expandable: Boolean = false,
    icon: @Composable () -> Unit,
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
                MaterialTheme.colorScheme.contentColorFor(color).toArgb(),
                if (selected) 0.15f else 0f
            )
        )
    ) {
        Row(
            Modifier
                .height(height)
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                icon()
                content()
            }
            if (expandable) {
                Icon(Icons.Outlined.ChevronRight, "Open up")
            }
        }
    }
}

@Composable
fun SmallTabButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(15.dp),
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    selected: Boolean = false,
    clickAndSemanticsModifier: Modifier = Modifier,
    expandable: Boolean = false,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    TabButton(
        modifier,
        shape,
        color,
        height = 50.dp,
        selected,
        clickAndSemanticsModifier,
        expandable,
        icon,
        content
    )
}

@Composable
fun MediumTabButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(15.dp),
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    selected: Boolean = false,
    clickAndSemanticsModifier: Modifier = Modifier,
    expandable: Boolean = false,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    TabButton(
        modifier,
        shape,
        color,
        height = 65.dp,
        selected,
        clickAndSemanticsModifier,
        expandable,
        icon,
        content
    )
}

@Composable
fun LargeTabButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(15.dp),
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    selected: Boolean = false,
    clickAndSemanticsModifier: Modifier = Modifier,
    expandable: Boolean = false,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    TabButton(
        modifier,
        shape,
        color,
        height = 80.dp,
        selected,
        clickAndSemanticsModifier,
        expandable,
        icon,
        content
    )
}