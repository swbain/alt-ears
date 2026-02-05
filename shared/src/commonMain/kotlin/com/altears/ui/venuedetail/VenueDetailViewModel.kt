package com.altears.ui.venuedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altears.domain.model.ShowUi
import com.altears.domain.model.VenueUi
import com.altears.domain.usecase.GetShowsByVenueUseCase
import com.altears.domain.usecase.ToggleScheduleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VenueDetailState(
    val venue: VenueUi? = null,
    val shows: List<ShowUi> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface VenueDetailAction {
    data class ToggleSchedule(val showId: Int) : VenueDetailAction
}

class VenueDetailViewModel(
    private val venueId: Int,
    private val getShowsByVenueUseCase: GetShowsByVenueUseCase,
    private val toggleScheduleUseCase: ToggleScheduleUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(VenueDetailState())
    val state: StateFlow<VenueDetailState> = _state.asStateFlow()
    
    init {
        observeVenue()
        observeShows()
    }
    
    fun onAction(action: VenueDetailAction) {
        when (action) {
            is VenueDetailAction.ToggleSchedule -> toggleSchedule(action.showId)
        }
    }
    
    private fun observeVenue() {
        viewModelScope.launch {
            getShowsByVenueUseCase.getVenue(venueId).collect { venue ->
                _state.update { it.copy(venue = venue, isLoading = false) }
            }
        }
    }
    
    private fun observeShows() {
        viewModelScope.launch {
            getShowsByVenueUseCase.getShows(venueId).collect { shows ->
                _state.update { it.copy(shows = shows) }
            }
        }
    }
    
    private fun toggleSchedule(showId: Int) {
        viewModelScope.launch {
            toggleScheduleUseCase(showId)
        }
    }
}
