package com.pjtsearch.opencontroller.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pjtsearch.opencontroller.extensions.OpenControllerIcon
import com.pjtsearch.opencontroller.ui.theme.shapes

@Composable
fun OpenControllerButton(modifier: Modifier, text: String, icon: String?, size: Int?, onClick: () -> Unit) {
    val view = LocalView.current
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.secondary) {
        Box(
            modifier
                .size(when (size) {
                    0 -> 65.dp
                    1 -> 77.dp
                    2 -> 156.dp
                    else -> 65.dp
                })
                .padding(when (size) {
                    0 -> 5.dp
                    1 -> 8.dp
                    2 -> 8.dp
                    else -> 5.dp
                })
                .clip(shapes.medium)
                .background(
                    MaterialTheme.colors.secondary.copy(alpha = 0.07f),
                    shapes.medium
                )
                .clickable(role = Role.Button) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onClick()
                }, Alignment.Center
        ) {
            icon?.let {
                OpenControllerIcon(icon, text, size)
            } ?: Text(text)
        }
    }
}