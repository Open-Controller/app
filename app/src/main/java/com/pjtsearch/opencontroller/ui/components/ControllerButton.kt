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
import androidx.compose.material3.contentColorFor
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

@Composable
fun ControllerButton(
    modifier: Modifier,
    text: String,
    icon: String?,
    size: Int?,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        scope.launch {
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
        tonalElevation = 5.dp,
        shape = CircleShape,
    ) {
        Box(Modifier.padding(3.dp), contentAlignment = Alignment.Center) {
            if (icon != null) {
                OpenControllerIcon(icon, text, size)
            } else {
                Text(text)
            }
        }
    }
}