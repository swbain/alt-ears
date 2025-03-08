package com.pavlovsfrog.altears

import com.pavlovsfrog.altears.cache.Database
import com.pavlovsfrog.altears.cache.DatabaseDriverFactory

class AltEarsSdk(databaseDriverFactory: DatabaseDriverFactory) {

    private val database = Database(databaseDriverFactory)
    private val getScheduleData = GetScheduleData()

    suspend fun getEvents(): List<ScheduleEvent> {
        val cachedEvents = database.getEvents()
        return cachedEvents.ifEmpty {
            getScheduleData().also { database.clearAndCreateEvents(it) }
        }
    }
    
    fun updateMyScheduleStatus(event: ScheduleEvent, isInMySchedule: Boolean) {
        database.updateMyScheduleStatus(event, isInMySchedule)
    }
}