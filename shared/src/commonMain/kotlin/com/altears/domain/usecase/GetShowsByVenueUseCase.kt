package com.altears.domain.usecase

import com.altears.data.repository.FestivalRepository
import com.altears.domain.model.ShowUi
import com.altears.domain.model.VenueUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetShowsByVenueUseCase(private val repository: FestivalRepository) {
    
    fun getVenue(stageId: Int): Flow<VenueUi?> {
        return combine(
            repository.getStages(),
            repository.getAllShows()
        ) { stages, shows ->
            val stage = stages.find { it.id.toInt() == stageId } ?: return@combine null
            val showCount = shows.count { it.stageId.toInt() == stageId }
            
            VenueUi(
                id = stage.id.toInt(),
                name = stage.title,
                subtitle = stage.subtitle,
                showCount = showCount
            )
        }
    }
    
    fun getShows(stageId: Int): Flow<List<ShowUi>> {
        return combine(
            repository.getAllShows(),
            repository.getArtists(),
            repository.getDays(),
            repository.getScheduledShows()
        ) { shows, artists, days, scheduledShows ->
            val artistMap = artists.associateBy { it.id.toInt() }
            val dayMap = days.associateBy { it.id.toInt() }
            val scheduledIds = scheduledShows.map { it.id.toInt() }.toSet()
            
            shows
                .filter { it.stageId.toInt() == stageId }
                .map { show ->
                    ShowUi(
                        id = show.id.toInt(),
                        artistId = show.artistId.toInt(),
                        artistName = artistMap[show.artistId.toInt()]?.title ?: show.title,
                        title = show.title,
                        displayDate = show.displayDate,
                        stageTitle = show.stageTitle,
                        startTimestamp = show.startTimestamp,
                        endTimestamp = show.endTimestamp,
                        dayTitle = dayMap[show.dayId.toInt()]?.title ?: "",
                        isScheduled = show.id.toInt() in scheduledIds
                    )
                }
        }
    }
}
