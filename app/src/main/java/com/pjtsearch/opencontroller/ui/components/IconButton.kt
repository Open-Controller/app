package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon

@Composable
fun OpenControllerButton(
    modifier: Modifier,
    text: String,
    icon: String?,
    size: Int?,
    onClick: () -> Unit
) {
    val view = LocalView.current
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
        }
    ) {
        icon?.let {
            OpenControllerIcon(icon, text, size)
        } ?: Text(text)
    }
}