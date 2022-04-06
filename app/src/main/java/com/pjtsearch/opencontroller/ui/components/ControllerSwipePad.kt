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
