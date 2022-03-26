package com.pjtsearch.opencontroller.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.fuel.core.FuelError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.House
import com.pjtsearch.opencontroller.executor.Panic
import com.pjtsearch.opencontroller.extensions.resolveHouseRef
import com.pjtsearch.opencontroller.settings.HouseRef
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Home route.
 *
 * This is derived from [HomeViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface HomeUiState {
    val isLoading: Boolean
    val house: House?

    /**
     * There are no posts to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoController(
        override val house: House?,
        override val isLoading: Boolean
    ) : HomeUiState

    /**
     * There are posts to render, as contained in [postsFeed].
     *
     * There is guaranteed to be a [selectedPost], which is one of the posts from [postsFeed].
     */
    data class HasController(
        val selectedController: Controller,
        val isControllerOpen: Boolean,
        override val isLoading: Boolean,
        override val house: House,
    ) : HomeUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class HomeViewModelState(
    val house: House? = null,
    val selectedController: Pair<String, String>? = null, // TODO back selectedController in a SavedStateHandle
    val isControllerOpen: Boolean = false,
    val isLoading: Boolean = false,
) {

    /**
     * Converts this [HomeViewModelState] into a more strongly typed [HomeUiState] for driving
     * the ui.
     */
    fun toUiState(): HomeUiState =
        if (house == null) {
            HomeUiState.NoController(
                house = null,
                isLoading = isLoading
            )
        } else if (selectedController == null) {
            HomeUiState.NoController(
                house = house,
                isLoading = isLoading
            )
        } else {
            HomeUiState.HasController(
                house = house,
                // Determine the selected post. This will be the post the user last selected.
                // If there is none (or that post isn't in the current feed), default to the
                // highlighted post
                selectedController = house.rooms[selectedController.first]!!.controllers[selectedController.second]!!,
                isControllerOpen = isControllerOpen,
                isLoading = isLoading,
            )
        }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
class HomeViewModel(
    private val houseRef: HouseRef,
    private val onPanic: (Panic) -> Unit,
    private val onGetHouseError: (Throwable) -> Unit
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshHouse()
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refreshHouse() {
        // Ui state is refreshing
        viewModelState.update { it.copy(isLoading = true) }

        GlobalScope.launch {
            val fetchResult = resolveHouseRef(houseRef)
            viewModelState.update {
                when (fetchResult) {
                    is Ok -> when (val evalResult = fetchResult.value) {
                        is Ok -> it.copy(house = evalResult.value, isLoading = false)
                        is Err -> {
                            onPanic(evalResult.error)
                            it
                        }
                    }
                    is Err -> {
                        onGetHouseError(fetchResult.error)
                        it
                    }
                }
            }
        }
    }

    fun selectController(controller: Pair<String, String>) {
        // Treat selecting a detail as simply interacting with it
        interactedWithController(controller)
    }

    /**
     * Notify that the user interacted with the article details
     */
    fun interactedWithController(controller: Pair<String, String>) {
        viewModelState.update {
            it.copy(
                selectedController = controller,
                isControllerOpen = true
            )
        }
    }

    /**
     * Notify that the user interacted with the article details
     */
    fun interactedWithRooms() {
        viewModelState.update {
            it.copy(
                isControllerOpen = false
            )
        }
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
            houseRef: HouseRef,
            onPanic: (Panic) -> Unit,
            onGetHouseError: (Throwable) -> Unit
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(houseRef, onPanic, onGetHouseError) as T
            }
        }
    }
}