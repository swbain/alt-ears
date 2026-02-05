package com.altears.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val data: DashboardData
)

@Serializable
data class DashboardData(
    val id: Int,
    val title: String,
    @SerialName("displayDate") val displayDate: String,
    @SerialName("timeZone") val timeZone: String,
    val artists: List<ArtistDto>,
    val gigs: List<GigDto>,
    val stages: List<StageDto>,
    val days: List<DayDto>
)

@Serializable
data class ArtistDto(
    val id: Int,
    val title: String,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("iconUrl") val iconUrl: String? = null,
    val text: String? = null,
    @SerialName("sortOrder") val sortOrder: Int = 0
)

@Serializable
data class GigDto(
    val id: Int,
    @SerialName("artistId") val artistId: Int,
    val title: String,
    @SerialName("displayDate") val displayDate: String,
    @SerialName("stageTitle") val stageTitle: String,
    @SerialName("startTimestamp") val startTimestamp: Long,
    @SerialName("endTimestamp") val endTimestamp: Long,
    @SerialName("stageId") val stageId: Int,
    @SerialName("dayId") val dayId: Int,
    @SerialName("sortOrder") val sortOrder: Int = 0
)

@Serializable
data class StageDto(
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    @SerialName("sortOrder") val sortOrder: Int = 0
)

@Serializable
data class DayDto(
    val id: Int,
    @SerialName("startTimestamp") val startTimestamp: Long,
    @SerialName("endTimestamp") val endTimestamp: Long,
    val title: String,
    val subtitle: String,
    @SerialName("titleShort") val titleShort: String,
    @SerialName("sortOrder") val sortOrder: Int = 0
)
