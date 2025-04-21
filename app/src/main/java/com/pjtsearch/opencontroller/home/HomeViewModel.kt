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

package com.pjtsearch.opencontroller.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.pjtsearch.opencontroller.executor.House
import com.pjtsearch.opencontroller.executor.Panic
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.extensions.resolveHouseRef
import com.pjtsearch.opencontroller.settings.HouseRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface ControllerMenuState {
    val items: List<Widget>

    data class Open(override val items: List<Widget>) : ControllerMenuState
    data class Closed(override val items: List<Widget>) : ControllerMenuState
}

sealed interface HouseLoadingState {
    val houseRef: HouseRef
    val house: House?

    data class Loading(override val house: House?, override val houseRef: HouseRef) :
        HouseLoadingState

    data class Loaded(override val house: House, override val houseRef: HouseRef) :
        HouseLoadingState

    data class Error(
        val error: Throwable,
        override val house: House?,
        override val houseRef: HouseRef
    ) : HouseLoadingState
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
class HomeViewModel(
    private val houseRef: HouseRef,
    private val onPanic: (Panic) -> Unit
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow<HouseLoadingState>(HouseLoadingState.Loading(null, houseRef))

    // UI state exposed to the UI
    val houseLoadingState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshHouse()
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refreshHouse() {
        // Ui state is refreshing
        viewModelState.update { HouseLoadingState.Loading(it.house, it.houseRef) }

        viewModelScope.launch {
            val fetchResult = withContext(Dispatchers.IO) {
                resolveHouseRef(houseRef)
            }
            viewModelState.update {
                when (fetchResult) {
                    is Ok -> when (val evalResult = fetchResult.value) {
                        is Ok -> HouseLoadingState.Loaded(evalResult.value, it.houseRef)
                        is Err -> {
                            onPanic(evalResult.error)
                            it
                        }
                    }

                    is Err -> {
                        HouseLoadingState.Error(fetchResult.error, it.house, it.houseRef)
                    }
                }
            }
        }
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            houseRef: HouseRef,
            onPanic: (Panic) -> Unit,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(houseRef, onPanic) as T
            }
        }
    }
}