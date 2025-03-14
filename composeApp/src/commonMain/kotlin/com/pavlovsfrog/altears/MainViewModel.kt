package com.pavlovsfrog.altears

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScheduleTab {
    MY_SCHEDULE,
    FULL_SCHEDULE,
    VENUES
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
            val venues = sdk.getVenues()
            _state.update { it.copy(
                isLoading = false,
                allEvents = events,
                venues = venues
            ) }
        }
    }
    
    fun selectTab(tab: ScheduleTab) {
        _state.update { it.copy(selectedTab = tab) }
    }
    
    fun selectVenue(venue: String?) {
        _state.update { it.copy(selectedVenue = venue) }
    }
    
    fun updateCurrentVisibleDay(day: String?) {
        _state.update { it.copy(currentVisibleDay = day) }
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
    val selectedTab: ScheduleTab = ScheduleTab.MY_SCHEDULE,
    val allEvents: List<ScheduleEvent> = emptyList(),
    val venues: List<String> = emptyList(),
    val selectedVenue: String? = null,
    val currentVisibleDay: String? = null
) {
    val events: List<ScheduleEvent> get() = 
        when (selectedTab) {
            ScheduleTab.MY_SCHEDULE -> allEvents.filter { it.isInMySchedule }
            ScheduleTab.FULL_SCHEDULE -> allEvents
            ScheduleTab.VENUES -> {
                if (selectedVenue != null) {
                    allEvents.filter { it.venue == selectedVenue }
                } else {
                    emptyList()
                }
            }
        }
        
    // Get all available days from events, sorted chronologically
    val availableDays: List<String> get() = 
        when (selectedTab) {
            ScheduleTab.MY_SCHEDULE -> {
                val myScheduleEvents = allEvents.filter { it.isInMySchedule }
                if (myScheduleEvents.isEmpty()) emptyList() else myScheduleEvents
            }
            ScheduleTab.FULL_SCHEDULE -> allEvents
            ScheduleTab.VENUES -> emptyList()
        }
        .groupBy { it.date }
        .keys
        .sortedBy { date ->
            events.filter { it.date == date }.minOfOrNull { it.startEpoch } ?: 0L
        }
        .map { it.split(",").firstOrNull()?.trim() ?: it }
}