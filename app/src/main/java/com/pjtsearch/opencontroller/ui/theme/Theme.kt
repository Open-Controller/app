package com.pjtsearch.opencontroller.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColorScheme(
    secondary = Color.White,
    primary = blueA100,
    surface = grey1000,
    background = black
)

private val LightColorPalette = lightColorScheme(
    primary = blueA100,
    onPrimary = Color.White,
    primaryContainer = blueA100,
    onPrimaryContainer = Color.White,
    inversePrimary = blueA100,
    secondary = blueA100,
    onSecondary = Color.White,
    secondaryContainer = blueA100,
    onSecondaryContainer = Color.White,
    tertiaryContainer = blueA100,
    onTertiaryContainer = Color.White,
    background = Color(0xFFc0cfff),
    onBackground = Color.White,
    surface = blueA100,
    onSurface = Color.White,
    surfaceVariant = blueA100,
    onSurfaceVariant = Color.White,
    inverseSurface = blueA100,
    inverseOnSurface = Color.White,
    outline = blueA100,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

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