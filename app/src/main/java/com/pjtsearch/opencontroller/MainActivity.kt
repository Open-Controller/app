package com.pjtsearch.opencontroller

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.gestures.OverScrollConfiguration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.navigation.compose.rememberNavController
import com.pjtsearch.opencontroller.components.SystemUi
import com.pjtsearch.opencontroller.extensions.*
import com.pjtsearch.opencontroller.settings.Settings
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "Settings.proto",
    serializer = SettingsSerializer
)

class MainActivity : AppCompatActivity() {
    @OptIn(
        ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
        ExperimentalComposeUiApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = rememberWindowWidthClass()
            val windowHeightClass = rememberWindowHeightClass()
            CompositionLocalProvider(
                LocalOverScrollConfiguration provides OverScrollConfiguration(
                    forceShowAlways = true
                ),
                LocalDensity provides Density(
                    density = if (windowHeightClass == WindowSize.Compact) 3.0f else LocalDensity.current.density,
                    fontScale = LocalDensity.current.fontScale
                )
            ) {
                println(windowHeightClass)
                SystemUi(this.window) {
                    MainActivityView(windowSizeClass)
                }
            }
        }
    }
}

sealed interface ErrorDialogState {
    object Closed : ErrorDialogState
    data class Opened(val error: Throwable) : ErrorDialogState
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun MainActivityView(windowSize: WindowSize) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val navController = rememberNavController()
    var initialStartDestination: String? by remember { mutableStateOf(null) }

    val isExpandedScreen = windowSize == WindowSize.Expanded

    var errorDialogState: ErrorDialogState by remember { mutableStateOf(ErrorDialogState.Closed) }

    val onError = { err: Throwable ->
        scope.launch {
            err.printStackTrace()
            errorDialogState = ErrorDialogState.Opened(err)
        }
    }

    LaunchedEffect(ctx.settingsDataStore) {
        ctx.settingsDataStore.data.take(1).collect { setttings ->
            initialStartDestination = if (setttings.hasLastHouse()) {
                Destinations.LAST_HOME_ROUTE
            } else {
                Destinations.HOUSES_ROUTE
            }
        }
    }

    initialStartDestination?.let { startDestination ->
        NavigationGraph(
            isExpandedScreen = isExpandedScreen,
            navController = navController,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            onError = { onError(it) },
            startDestination = startDestination
        )
    }

    val state = errorDialogState
    if (state is ErrorDialogState.Opened) {
        AlertDialog(
            onDismissRequest = { errorDialogState = ErrorDialogState.Closed },
            confirmButton = {
                Button(
                    onClick = {
                        copy(
                            state.error.localizedMessage ?: "Unknown error",
                            state.error.stackTraceToString(),
                            ctx
                        )
                        errorDialogState = ErrorDialogState.Closed
                    }
                ) {
                    Text("Copy Stack Trace")
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = {
                        errorDialogState = ErrorDialogState.Closed
                    }
                ) {
                    Text("Dismiss")
                }
            },
            icon = { Icon(Icons.Outlined.ErrorOutline, "Error") },
            title = {
                Text(text = "Error: " + (state.error.localizedMessage ?: "Unknown error"))
            },
            text = {
                LazyColumn(Modifier.fillMaxHeight(0.8f)) {
                    item {
                        Text(text = state.error.stackTraceToString())
                    }
                }
            },
        )
    }
}
