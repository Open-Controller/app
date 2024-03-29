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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import kotlinx.coroutines.launch

sealed interface ControllerButtonContext {
    object Normal : ControllerButtonContext
    object SwipePad : ControllerButtonContext
}

val LocalControllerButtonContext =
    compositionLocalOf<ControllerButtonContext> { ControllerButtonContext.Normal }

/**
 * A button component for controllers
 *
 * @param modifier Modifier to be applied to the layout
 * @param text The text to display
 * @param icon The icon to display
 * @param size The size of the button
 * @param onClick Function to be called when user clicks on the element
 */
@Composable
fun ControllerButton(
    modifier: Modifier,
    text: String?,
    icon: String?,
    size: Int?,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val controllerButtonContext = LocalControllerButtonContext.current
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        scope.launch {
//            Haptic feedback on mouse down
            interactionSource.interactions.collect { value ->
                if (value is PressInteraction.Press) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }
            }
        }
    }
    Surface(
        modifier = modifier
            .size(
                when (size) {
                    0 -> 65.dp
                    1 -> 87.dp
                    2 -> 176.dp
                    else -> 65.dp
                }
            )
            .padding(
                when (size) {
                    0 -> 5.dp
                    1 -> 8.dp
                    2 -> 8.dp
                    else -> 5.dp
                }
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onClick()
            },
        color = when (controllerButtonContext) {
            ControllerButtonContext.SwipePad -> MaterialTheme.colorScheme.secondaryContainer
            ControllerButtonContext.Normal -> when (size) {
                2 -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        },
        tonalElevation = 5.dp,
        shape = CircleShape,
    ) {
        Box(Modifier.padding(3.dp), contentAlignment = Alignment.Center) {
            if (icon != null) {
                OpenControllerIcon(icon, text ?: icon, size)
            } else if (text != null) {
                Text(text)
            }
        }
    }
}