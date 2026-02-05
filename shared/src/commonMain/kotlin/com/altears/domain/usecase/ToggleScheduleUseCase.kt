package com.altears.domain.usecase

import com.altears.data.repository.FestivalRepository

class ToggleScheduleUseCase(private val repository: FestivalRepository) {
    
    suspend operator fun invoke(showId: Int): Boolean {
        return repository.toggleSchedule(showId)
    }
}
