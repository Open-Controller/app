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

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.components.SwipePad
import com.pjtsearch.opencontroller.extensions.DirectionVector
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon

/**
 * A component for a swipe pad for a controller
 *
 * @param modifier A modifier for the layout
 * @param expand Whether to expand the widget over available width and height
 * @param onBottomIncrease Function to be called when the bottom increase button is pressed
 * @param onBottomDecrease Function to be called when the bottom decrease button is pressed
 * @param onBottomHold Function to be called when the bottom decrease button is held
 * @param onSwipeDown Function to be called when the pad is swiped down
 * @param onSwipeLeft Function to be called when the pad is swiped left
 * @param onSwipeRight Function to be called when the pad is swiped right
 * @param onSwipeUp Function to be called when the pad is swiped up
 * @param onClick Function to be called when the pad is clicked
 * @param bottomDecreaseIcon Icon to be displayed on the bottom decrease button
 * @param bottomIncreaseIcon Icon to be displayed on the bottom increase button
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ControllerSwipePad(
    modifier: Modifier,
    expand: Boolean?,
    onBottomIncrease: (() -> Unit)?,
    onBottomDecrease: (() -> Unit)?,
    onBottomHold: () -> Unit,
    onSwipeDown: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeUp: () -> Unit,
    onClick: () -> Unit,
    bottomDecreaseIcon: String,
    bottomIncreaseIcon: String,
) {
    val view = LocalView.current
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(40.dp)
    ) {
        Box {
            SwipePad(
                if (expand == true) Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                else Modifier.defaultMinSize(200.dp, 200.dp)
            ) {
                when (it) {
                    is DirectionVector.Down -> onSwipeDown()
                    is DirectionVector.Left -> onSwipeLeft()
                    is DirectionVector.Right -> onSwipeRight()
                    is DirectionVector.Up -> onSwipeUp()
                    DirectionVector.Zero -> onClick()
                }
            }
//            Bottom controls to be displayed if have bottom increase and decrease
            if (onBottomIncrease != null && onBottomDecrease != null) {
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp)
                        .then(
                            if (expand == true) Modifier.fillMaxWidth() else Modifier.defaultMinSize(
                                200.dp, 10.dp
                            )
                        ), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
//                        Decrease button with hold functionality
                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    onLongClick = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            view.performHapticFeedback(
                                                HapticFeedbackConstants.REJECT
                                            )
                                        } else {
                                            view.performHapticFeedback(
                                                HapticFeedbackConstants.LONG_PRESS
                                            )
                                        }
                                        onBottomHold()
                                    },
                                    onClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                        onBottomDecrease()
                                    },
                                    indication = rememberRipple(true, 32.dp)
                                )
                                .clip(CircleShape)
                                .size(64.dp), contentAlignment = Alignment.Center
                        ) {
                            OpenControllerIcon(
                                bottomDecreaseIcon, "Decrease", 1
                            )
                        }
//                        Increase button
                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                        onBottomIncrease()
                                    },
                                    indication = rememberRipple(true, 32.dp)
                                )
                                .clip(CircleShape)
                                .size(64.dp), contentAlignment = Alignment.Center
                        ) {
                            OpenControllerIcon(
                                bottomIncreaseIcon, "Increase", 1
                            )
                        }
                    }
                }
            }
        }
    }
}
