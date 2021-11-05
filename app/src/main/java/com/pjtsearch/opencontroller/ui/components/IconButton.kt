package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun OpenControllerButton(
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
    FilledTonalButton(
        modifier = modifier
            .size(
                when (size) {
                    0 -> 65.dp
                    1 -> 77.dp
                    2 -> 156.dp
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
            ),
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            onClick()
        },
        interactionSource = interactionSource
    ) {
        icon?.let {
            OpenControllerIcon(icon, text, size)
        } ?: Text(text)
    }
}