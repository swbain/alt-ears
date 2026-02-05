package com.altears.ui.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altears.data.repository.FestivalRepository
import com.altears.domain.model.ShowUi
import com.altears.domain.usecase.GetShowsUseCase
import com.altears.domain.usecase.ToggleScheduleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShowsState(
    val shows: List<ShowUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface ShowsEffect {
    data class ShowError(val message: String) : ShowsEffect
}

sealed interface ShowsAction {
    data object Refresh : ShowsAction
    data class ToggleSchedule(val showId: Int) : ShowsAction
}

class ShowsViewModel(
    private val getShowsUseCase: GetShowsUseCase,
    private val toggleScheduleUseCase: ToggleScheduleUseCase,
    private val repository: FestivalRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ShowsState())
    val state: StateFlow<ShowsState> = _state.asStateFlow()
    
    private val _effects = Channel<ShowsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    
    init {
        observeShows()
        checkAndRefresh()
    }
    
    fun onAction(action: ShowsAction) {
        when (action) {
            is ShowsAction.Refresh -> refresh()
            is ShowsAction.ToggleSchedule -> toggleSchedule(action.showId)
        }
    }
    
    private fun observeShows() {
        viewModelScope.launch {
            getShowsUseCase().collect { shows ->
                _state.update { it.copy(shows = shows, isLoading = false) }
            }
        }
    }
    
    private fun checkAndRefresh() {
        viewModelScope.launch {
            if (repository.needsRefresh()) {
                refresh()
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    
    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.refreshData()
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                    _effects.send(ShowsEffect.ShowError(e.message ?: "Failed to refresh"))
                }
                .onSuccess {
                    _state.update { it.copy(isLoading = false, error = null) }
                }
        }
    }
    
    private fun toggleSchedule(showId: Int) {
        viewModelScope.launch {
            toggleScheduleUseCase(showId)
        }
    }
}
