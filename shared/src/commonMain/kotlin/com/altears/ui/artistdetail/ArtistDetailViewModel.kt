package com.altears.ui.artistdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altears.domain.model.ArtistUi
import com.altears.domain.model.ShowUi
import com.altears.domain.usecase.GetArtistDetailUseCase
import com.altears.domain.usecase.GetShowsUseCase
import com.altears.domain.usecase.ToggleScheduleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArtistDetailState(
    val artist: ArtistUi? = null,
    val shows: List<ShowUi> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface ArtistDetailAction {
    data class ToggleSchedule(val showId: Int) : ArtistDetailAction
}

class ArtistDetailViewModel(
    private val artistId: Int,
    private val getArtistDetailUseCase: GetArtistDetailUseCase,
    private val getShowsUseCase: GetShowsUseCase,
    private val toggleScheduleUseCase: ToggleScheduleUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ArtistDetailState())
    val state: StateFlow<ArtistDetailState> = _state.asStateFlow()
    
    init {
        observeArtist()
        observeShows()
    }
    
    fun onAction(action: ArtistDetailAction) {
        when (action) {
            is ArtistDetailAction.ToggleSchedule -> toggleSchedule(action.showId)
        }
    }
    
    private fun observeArtist() {
        viewModelScope.launch {
            getArtistDetailUseCase(artistId).collect { artist ->
                _state.update { it.copy(artist = artist, isLoading = false) }
            }
        }
    }
    
    private fun observeShows() {
        viewModelScope.launch {
            getShowsUseCase.forArtist(artistId).collect { shows ->
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
