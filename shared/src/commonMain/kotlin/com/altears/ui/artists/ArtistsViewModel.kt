package com.altears.ui.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altears.data.repository.FestivalRepository
import com.altears.domain.model.ArtistUi
import com.altears.domain.usecase.GetArtistsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArtistsState(
    val artists: List<ArtistUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface ArtistsEffect {
    data class NavigateToArtist(val artistId: Int) : ArtistsEffect
    data class ShowError(val message: String) : ArtistsEffect
}

sealed interface ArtistsAction {
    data object Refresh : ArtistsAction
    data class OnArtistClick(val artistId: Int) : ArtistsAction
}

class ArtistsViewModel(
    private val getArtistsUseCase: GetArtistsUseCase,
    private val repository: FestivalRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ArtistsState())
    val state: StateFlow<ArtistsState> = _state.asStateFlow()
    
    private val _effects = Channel<ArtistsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    
    init {
        observeArtists()
        checkAndRefresh()
    }
    
    fun onAction(action: ArtistsAction) {
        when (action) {
            is ArtistsAction.Refresh -> refresh()
            is ArtistsAction.OnArtistClick -> {
                viewModelScope.launch {
                    _effects.send(ArtistsEffect.NavigateToArtist(action.artistId))
                }
            }
        }
    }
    
    private fun observeArtists() {
        viewModelScope.launch {
            getArtistsUseCase().collect { artists ->
                _state.update { it.copy(artists = artists, isLoading = false) }
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
                    _effects.send(ArtistsEffect.ShowError(e.message ?: "Failed to refresh"))
                }
                .onSuccess {
                    _state.update { it.copy(isLoading = false, error = null) }
                }
        }
    }
}
