package com.pjtsearch.opencontroller.components

import android.os.Build
import android.view.Window
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color.Companion.Transparent
import com.pjtsearch.opencontroller.ui.theme.OpenControllerTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SystemUi(window: Window, content: @Composable () -> Unit) =
        OpenControllerTheme {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            }

            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight

            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Transparent,
                    darkIcons = useDarkIcons
                )
            }
            ProvideWindowInsets(content = content)
        }