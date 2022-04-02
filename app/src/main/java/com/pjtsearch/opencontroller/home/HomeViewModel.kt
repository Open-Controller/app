package com.pjtsearch.opencontroller.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.pjtsearch.opencontroller.executor.Controller
import com.pjtsearch.opencontroller.executor.House
import com.pjtsearch.opencontroller.executor.Panic
import com.pjtsearch.opencontroller.executor.Widget
import com.pjtsearch.opencontroller.extensions.resolveHouseRef
import com.pjtsearch.opencontroller.settings.HouseRef
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ControllerMenuState {
    val items: List<Widget>
    data class Open(override val items: List<Widget>) : ControllerMenuState
    data class Closed(override val items: List<Widget>) : ControllerMenuState
}

sealed interface HouseLoadingState {
    data class Loading(val house: House?) : HouseLoadingState
    data class Loaded(val house: House) : HouseLoadingState
    data class Error(val error: Throwable, val house: House?) : HouseLoadingState

    companion object From {
        fun fromLoadingAndError(isLoading: Boolean, loadingError: Throwable?, house: House?) =
            if (isLoading) {
                if (loadingError != null) Error(loadingError, house)
                else Loading(house)
            } else {
                checkNotNull(house)
                Loaded(house)
            }
    }
}

/**
 * UI state for the Home route.
 *
 * This is derived from [HomeViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface HomeUiState {
    val houseLoadingState: HouseLoadingState

    /**
     * There are no posts to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoController(
        override val houseLoadingState: HouseLoadingState
    ) : HomeUiState

    /**
     * There are posts to render, as contained in [postsFeed].
     *
     * There is guaranteed to be a [selectedPost], which is one of the posts from [postsFeed].
     */
    data class HasController(
        val roomDisplayName: String,
        val selectedController: Controller,
        val isControllerOpen: Boolean,
        val controllerMenuState: ControllerMenuState,
        override val houseLoadingState: HouseLoadingState,
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
    val loadingError: Throwable? = null,
    val isControllerMenuOpen: Boolean = false,
    val controllerMenuItems: List<Widget> = listOf()
) {

    /**
     * Converts this [HomeViewModelState] into a more strongly typed [HomeUiState] for driving
     * the ui.
     */
    fun toUiState(): HomeUiState =
        if (house == null) {
            HomeUiState.NoController(
                houseLoadingState = HouseLoadingState.fromLoadingAndError(isLoading, loadingError, null)
            )
        } else if (selectedController == null) {
            HomeUiState.NoController(
                houseLoadingState = HouseLoadingState.fromLoadingAndError(isLoading, loadingError, house)
            )
        } else {
            HomeUiState.HasController(
                // Determine the selected post. This will be the post the user last selected.
                // If there is none (or that post isn't in the current feed), default to the
                // highlighted post
//                TODO: What if reloaded? Should it only have the ids, so that can refresh? It actually probably already will
                selectedController = house.rooms.find { it.id == selectedController.first }!!.controllers.find { it.id == selectedController.second }!!,
                isControllerOpen = isControllerOpen,
                roomDisplayName = house.rooms.find { it.id == selectedController.first }!!.displayName,
                houseLoadingState = HouseLoadingState.fromLoadingAndError(isLoading, loadingError, house),
                controllerMenuState = if (isControllerMenuOpen) {
                    ControllerMenuState.Open(controllerMenuItems)
                } else {
                    ControllerMenuState.Closed(controllerMenuItems)
                }
            )
        }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
class HomeViewModel(
    private val houseRef: HouseRef,
    private val onPanic: (Panic) -> Unit
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
        viewModelState.update { it.copy(isLoading = true, loadingError = null) }

        GlobalScope.launch {
            val fetchResult = resolveHouseRef(houseRef)
            viewModelState.update {
                when (fetchResult) {
                    is Ok -> when (val evalResult = fetchResult.value) {
                        is Ok -> it.copy(house = evalResult.value, isLoading = false, loadingError = null)
                        is Err -> {
                            onPanic(evalResult.error)
                            it
                        }
                    }
                    is Err -> {
                        it.copy(loadingError = fetchResult.error)
                    }
                }
            }
        }
    }

    fun selectController(controller: Pair<String, String>) {
        // Treat selecting a detail as simply interacting with it
        interactedWithController(controller)
    }

    fun interactedWithControllerMenu(open: Boolean, items: List<Widget>) {
        viewModelState.update {
            it.copy(
                isControllerMenuOpen = open,
                controllerMenuItems = items
            )
        }
    }

    /**
     * Notify that the user interacted with the article details
     */
    fun interactedWithController(controller: Pair<String, String>) {
        viewModelState.update {
            it.copy(
                selectedController = controller,
                isControllerOpen = true,
                isControllerMenuOpen = false
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
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(houseRef, onPanic) as T
            }
        }
    }
}