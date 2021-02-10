package com.pjtsearch.opencontroller

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.pjtsearch.opencontroller.ui.theme.OpenControllerTheme
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import dev.chrisbanes.accompanist.insets.statusBarsPadding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.window.setDecorFitsSystemWindows(true)
            this.window.statusBarColor = Color.TRANSPARENT
            this.window.navigationBarColor = Color.TRANSPARENT
            this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            //if (MaterialTheme.colors.surface.luminance() > 0.5f) {
                this.window.decorView.systemUiVisibility = this.window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            //}

            //if (MaterialTheme.colors.surface.luminance() > 0.5f) {
                this.window.decorView.systemUiVisibility = this.window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            //}
        };*/
        setContent {
            SystemUi(this.window)
        }
    }
}

@Composable
fun SystemUi(windows: Window) =
    OpenControllerTheme {
        windows.statusBarColor = Color.TRANSPARENT
        windows.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windows.setDecorFitsSystemWindows(false)
        }

        @Suppress("DEPRECATION")
        if (MaterialTheme.colors.surface.luminance() > 0.5f) {
            windows.decorView.systemUiVisibility = windows.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        @Suppress("DEPRECATION")
        if (MaterialTheme.colors.surface.luminance() > 0.5f) {
            windows.decorView.systemUiVisibility = windows.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        ProvideWindowInsets {
            Surface(modifier = Modifier.statusBarsPadding()) {
                Greeting("Android")
            }
        }
    }

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OpenControllerTheme {
        Greeting("Android")
    }
}