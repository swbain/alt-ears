package com.pavlovsfrog.altears

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScheduleTab {
    FULL_SCHEDULE,
    MY_SCHEDULE
}

class MainViewModel(private val sdk: AltEarsSdk) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()
    
    init {
        loadEvents()
    }
    
    private fun loadEvents() {
        viewModelScope.launch {
            val events = sdk.getEvents()
            _state.update { it.copy(
                isLoading = false,
                allEvents = events
            ) }
        }
    }
    
    fun selectTab(tab: ScheduleTab) {
        _state.update { it.copy(selectedTab = tab) }
    }
    
    fun toggleMySchedule(event: ScheduleEvent) {
        // Calculate new state
        val newState = !event.isInMySchedule
        
        // Update database
        sdk.updateMyScheduleStatus(event, newState)
        
        // Update in-memory state
        val updatedEvents = _state.value.allEvents.map { 
            if (it.artist == event.artist && it.startEpoch == event.startEpoch) {
                it.copy(isInMySchedule = newState)
            } else {
                it
            }
        }
        
        _state.update { it.copy(allEvents = updatedEvents) }
    }
}

data class MainState(
    val isLoading: Boolean = true,
    val selectedTab: ScheduleTab = ScheduleTab.FULL_SCHEDULE,
    val allEvents: List<ScheduleEvent> = emptyList()
) {
    val events: List<ScheduleEvent> get() = 
        if (selectedTab == ScheduleTab.MY_SCHEDULE) {
            allEvents.filter { it.isInMySchedule }
        } else {
            allEvents
        }
}