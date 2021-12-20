package com.pjtsearch.opencontroller.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColorScheme()

private val LightColorPalette = lightColorScheme()

@Composable
fun OpenControllerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) {
            dynamicDarkColorScheme(ctx)
        } else {
            dynamicLightColorScheme(ctx)
        }
    } else {
        if (darkTheme) {
            DarkColorPalette
        } else {
            LightColorPalette
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content,
    )
}