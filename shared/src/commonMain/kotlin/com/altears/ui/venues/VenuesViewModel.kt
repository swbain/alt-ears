package com.altears.ui.venues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altears.data.repository.FestivalRepository
import com.altears.domain.model.VenueUi
import com.altears.domain.usecase.GetVenuesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VenuesState(
    val venues: List<VenueUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface VenuesEffect {
    data class NavigateToVenue(val venueId: Int) : VenuesEffect
}

sealed interface VenuesAction {
    data object Refresh : VenuesAction
    data class OnVenueClick(val venueId: Int) : VenuesAction
}

class VenuesViewModel(
    private val getVenuesUseCase: GetVenuesUseCase,
    private val repository: FestivalRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(VenuesState())
    val state: StateFlow<VenuesState> = _state.asStateFlow()
    
    private val _effects = MutableSharedFlow<VenuesEffect>()
    val effects: SharedFlow<VenuesEffect> = _effects.asSharedFlow()
    
    init {
        observeVenues()
        checkAndRefresh()
    }
    
    fun onAction(action: VenuesAction) {
        when (action) {
            is VenuesAction.Refresh -> refresh()
            is VenuesAction.OnVenueClick -> {
                viewModelScope.launch {
                    _effects.emit(VenuesEffect.NavigateToVenue(action.venueId))
                }
            }
        }
    }
    
    private fun observeVenues() {
        viewModelScope.launch {
            getVenuesUseCase().collect { venues ->
                _state.update { it.copy(venues = venues, isLoading = false) }
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
                }
                .onSuccess {
                    _state.update { it.copy(isLoading = false, error = null) }
                }
        }
    }
}
