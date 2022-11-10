/*
 * Copyright (c) 2022 PJTSearch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.pjtsearch.opencontroller

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
//                LocalOverscrollConfiguration provides OverscrollConfiguration(
//                    forceShowAlways = true
//                ),
                LocalDensity provides Density(
//                    Lower density for small devices
                    density = if (windowHeightClass == WindowSize.Compact) 3.0f else LocalDensity.current.density,
                    fontScale = LocalDensity.current.fontScale
                )
            ) {
                SystemUi(this.window) {
                    MainActivityView(windowSizeClass)
                }
            }
        }
    }
}

/**
 * Represents the state of the error dialog
 */
sealed interface ErrorDialogState {
    /**
     * State representing a closed dialog
     */
    object Closed : ErrorDialogState

    /**
     * State representing an open dialog
     *
     * @property error The error to be shown in the dialog
     */
    data class Opened(val error: Throwable) : ErrorDialogState
}

/**
 * A component for the main activity
 *
 * @param windowSize The size from the width of the window
 */
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun MainActivityView(windowSize: WindowSize) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val navController = rememberNavController()
//    The destination to start from, starts as null so can load from settings
    var initialStartDestination: String? by remember { mutableStateOf(null) }

    val isExpandedScreen = windowSize == WindowSize.Expanded

    var errorDialogState: ErrorDialogState by remember { mutableStateOf(ErrorDialogState.Closed) }

    val onError = { err: Throwable ->
        scope.launch {
            err.printStackTrace()
            errorDialogState = ErrorDialogState.Opened(err)
        }
    }

//    On launch, get the settings once and set the initialStartDestination
//    LaunchedEffect(ctx.settingsDataStore) {
//        ctx.settingsDataStore.data.take(1).collect { settings ->
//            initialStartDestination = if (settings.hasLastHouse()) {
//                Destinations.LAST_HOME_ROUTE
//            } else {
////                FIXME
//                TODO("FIX WHEN NO HOUSES")
//            }
//        }
//    }

//    Hide the navigation until initialStartDestination is loaded so that will go there immediately
    NavigationGraph(
        isExpandedScreen = isExpandedScreen,
        navController = navController,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        onError = { onError(it) },
        startDestination = Destinations.LAST_HOME_ROUTE
    )

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
