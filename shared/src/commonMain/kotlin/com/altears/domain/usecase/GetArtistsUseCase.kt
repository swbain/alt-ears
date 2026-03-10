package com.altears.domain.usecase

import com.altears.data.repository.FestivalRepository
import com.altears.domain.model.ArtistUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetArtistsUseCase(private val repository: FestivalRepository) {
    
    operator fun invoke(): Flow<List<ArtistUi>> {
        return combine(
            repository.getArtists(),
            repository.getAllShows(),
            repository.getScheduledShows()
        ) { artists, shows, scheduledShows ->
            val showCountByArtist = shows.groupingBy { it.artistId.toInt() }.eachCount()
            
            // Get artist IDs that have at least one scheduled show
            val scheduledShowIds = scheduledShows.map { it.id.toInt() }.toSet()
            val artistsWithScheduledShows = shows
                .filter { it.id.toInt() in scheduledShowIds }
                .map { it.artistId.toInt() }
                .toSet()
            
            artists.map { artist ->
                ArtistUi(
                    id = artist.id.toInt(),
                    name = artist.title,
                    imageUrl = artist.imageUrl,
                    iconUrl = artist.iconUrl,
                    description = artist.description,
                    showCount = showCountByArtist[artist.id.toInt()] ?: 0,
                    hasScheduledShow = artist.id.toInt() in artistsWithScheduledShows
                )
            }
        }
    }
}
