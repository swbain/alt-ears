package com.altears.domain.usecase

import com.altears.data.repository.FestivalRepository
import com.altears.domain.model.ArtistUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetArtistDetailUseCase(private val repository: FestivalRepository) {
    
    operator fun invoke(artistId: Int): Flow<ArtistUi?> {
        return combine(
            repository.getArtist(artistId),
            repository.getShowsForArtist(artistId)
        ) { artist, shows ->
            artist?.let {
                ArtistUi(
                    id = it.id.toInt(),
                    name = it.title,
                    imageUrl = it.imageUrl,
                    iconUrl = it.iconUrl,
                    description = it.description,
                    showCount = shows.size
                )
            }
        }
    }
}
