package com.altears.domain.usecase

import com.altears.data.repository.FestivalRepository
import com.altears.domain.model.ShowUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetShowsUseCase(private val repository: FestivalRepository) {
    
    operator fun invoke(): Flow<List<ShowUi>> {
        return combine(
            repository.getAllShows(),
            repository.getArtists(),
            repository.getDays(),
            repository.getScheduledShows()
        ) { shows, artists, days, scheduledShows ->
            val artistMap = artists.associateBy { it.id.toInt() }
            val dayMap = days.associateBy { it.id.toInt() }
            val scheduledIds = scheduledShows.map { it.id.toInt() }.toSet()
            
            shows.map { show ->
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
    
    fun forArtist(artistId: Int): Flow<List<ShowUi>> {
        return combine(
            repository.getShowsForArtist(artistId),
            repository.getArtists(),
            repository.getDays(),
            repository.getScheduledShows()
        ) { shows, artists, days, scheduledShows ->
            val artistMap = artists.associateBy { it.id.toInt() }
            val dayMap = days.associateBy { it.id.toInt() }
            val scheduledIds = scheduledShows.map { it.id.toInt() }.toSet()
            
            shows.map { show ->
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
