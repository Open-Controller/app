package com.pjtsearch.opencontroller.components

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.luminance
import com.pjtsearch.opencontroller.ui.theme.OpenControllerTheme
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@Composable
fun SystemUi(window: Window, content: @Composable () -> Unit) =
        OpenControllerTheme {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            }

            @Suppress("DEPRECATION")
            if (MaterialTheme.colors.background.luminance() > 0.5f) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            @Suppress("DEPRECATION")
            if (MaterialTheme.colors.background.luminance() > 0.5f) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            ProvideWindowInsets(content = content)
        }