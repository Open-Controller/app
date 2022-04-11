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

package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import kotlinx.coroutines.launch

/**
 * The parameters for a button item
 *
 * @property text The text for the button
 * @property icon The icon for the button
 * @property onClick Function to be called when the button is clicked
 */
data class ButtonItemParams(
    val text: String,
    val icon: String?,
    val onClick: () -> Unit,
)

/**
 * A component for a group of adjacent controller buttons
 *
 * @param modifier A modifier for the layout
 * @param size The size of the button group
 * @param buttons The buttons to place inside the group
 */
@Composable
fun ControllerButtonGroup(
    modifier: Modifier,
    size: Int?,
    buttons: List<ButtonItemParams>
) =
    Surface(
        modifier = modifier
            .padding(
                when (size) {
                    0 -> 5.dp
                    1 -> 8.dp
                    2 -> 8.dp
                    else -> 5.dp
                }
            )
            .clip(CircleShape),
        color = MaterialTheme.colorScheme.tertiary,
        shape = CircleShape,
    ) {
        Row {
            buttons.map { btn ->
                ButtonInside(
                    btn.text,
                    btn.icon,
                    size,
                    btn.onClick
                )
            }
        }
    }

@Composable
fun ButtonInside(
    text: String,
    icon: String?,
    size: Int?,
    onClick: () -> Unit,
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        scope.launch {
//            Haptics on mouse down
            interactionSource.interactions.collect { value ->
                if (value is PressInteraction.Press) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }
            }
        }
    }

    Box(
        Modifier
            .height(
                when (size) {
                    0 -> 55.dp
                    1 -> 70.dp
                    2 -> 176.dp
                    else -> 65.dp
                }
            )
            .width(
                when (size) {
                    0 -> 35.dp
                    1 -> 50.dp
                    2 -> 156.dp
                    else -> 35.dp
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            OpenControllerIcon(icon, text, size)
        } else {
            Text(text)
        }
    }
}