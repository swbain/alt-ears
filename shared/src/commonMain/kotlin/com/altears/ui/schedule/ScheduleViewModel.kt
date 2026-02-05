package com.altears.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altears.domain.model.ShowUi
import com.altears.domain.usecase.GetScheduleUseCase
import com.altears.domain.usecase.ToggleScheduleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScheduleState(
    val shows: List<ShowUi> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface ScheduleAction {
    data class RemoveFromSchedule(val showId: Int) : ScheduleAction
}

class ScheduleViewModel(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val toggleScheduleUseCase: ToggleScheduleUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ScheduleState())
    val state: StateFlow<ScheduleState> = _state.asStateFlow()
    
    init {
        observeSchedule()
    }
    
    fun onAction(action: ScheduleAction) {
        when (action) {
            is ScheduleAction.RemoveFromSchedule -> removeFromSchedule(action.showId)
        }
    }
    
    private fun observeSchedule() {
        viewModelScope.launch {
            getScheduleUseCase().collect { shows ->
                _state.update { it.copy(shows = shows, isLoading = false) }
            }
        }
    }
    
    private fun removeFromSchedule(showId: Int) {
        viewModelScope.launch {
            toggleScheduleUseCase(showId)
        }
    }
}
