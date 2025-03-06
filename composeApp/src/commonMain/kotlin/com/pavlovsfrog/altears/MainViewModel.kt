package com.pavlovsfrog.altears

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()
    
    private val getScheduleData = GetScheduleData()
    
    init {
        viewModelScope.launch {
            val events = getScheduleData()
            _state.update { it.copy(
                isLoading = false,
                events = events
            ) }
        }
    }
}

data class MainState(
    val isLoading: Boolean = true,
    val events: List<ScheduleEvent> = emptyList()
)