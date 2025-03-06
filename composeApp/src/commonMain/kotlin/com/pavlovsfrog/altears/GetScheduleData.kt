package com.pavlovsfrog.altears

import alt_ears.composeapp.generated.resources.Res
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

class GetScheduleData {
    private val json = Json { ignoreUnknownKeys = true }
    
    @OptIn(ExperimentalResourceApi::class)
    suspend operator fun invoke(): List<ScheduleEvent> {
        val jsonContent = Res.readBytes("files/schedule_final.json").decodeToString()
        val events = json.decodeFromString<List<ScheduleEvent>>(jsonContent)

        // Sort by start time ascending (using epoch timestamp)
        return events.sortedBy { it.startEpoch }
    }
}