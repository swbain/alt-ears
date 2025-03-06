package com.pavlovsfrog.altears

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleEvent(
    val artist: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
    val venue: String,
    val date: String,
    @SerialName("start_epoch") val startEpoch: Long,
    @SerialName("end_epoch") val endEpoch: Long,
    @SerialName("crosses_midnight") val crossesMidnight: Boolean = false
)