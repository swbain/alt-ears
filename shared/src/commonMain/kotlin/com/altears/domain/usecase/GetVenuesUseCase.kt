package com.altears.domain.usecase

import com.altears.data.repository.FestivalRepository
import com.altears.domain.model.VenueUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetVenuesUseCase(private val repository: FestivalRepository) {
    
    operator fun invoke(): Flow<List<VenueUi>> {
        return combine(
            repository.getStages(),
            repository.getAllShows()
        ) { stages, shows ->
            val showCountByStage = shows.groupingBy { it.stageId.toInt() }.eachCount()
            
            stages.map { stage ->
                VenueUi(
                    id = stage.id.toInt(),
                    name = stage.title,
                    subtitle = stage.subtitle,
                    showCount = showCountByStage[stage.id.toInt()] ?: 0
                )
            }
        }
    }
}
