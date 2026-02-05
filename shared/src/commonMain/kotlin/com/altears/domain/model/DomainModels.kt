package com.altears.domain.model

data class ArtistUi(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val iconUrl: String?,
    val description: String?,
    val showCount: Int = 0
)

data class ShowUi(
    val id: Int,
    val artistId: Int,
    val artistName: String,
    val title: String,
    val displayDate: String,
    val stageTitle: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val dayTitle: String,
    val isScheduled: Boolean = false
)

data class DayUi(
    val id: Int,
    val title: String,
    val subtitle: String,
    val titleShort: String
)
