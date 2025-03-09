package com.pavlovsfrog.altears.cache

import com.pavlovsfrog.altears.ScheduleEvent

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun getEvents(): List<ScheduleEvent> {
        return dbQuery.selectAllEventsInfo { artist, startTime, endTime, venue, date, startEpoch, endEpoch, crossesMidnight, isInMySchedule ->
            mapEvent(artist, startTime, endTime, venue, date, startEpoch, endEpoch, crossesMidnight, isInMySchedule ?: false)
        }.executeAsList()
    }
    
    internal fun getVenues(): List<String> {
        return dbQuery.selectAllVenues().executeAsList()
    }

    internal fun clearAndCreateEvents(events: List<ScheduleEvent>) {
        dbQuery.removeAllEvents()
        events.forEach { event ->
            dbQuery.insertEvent(
                artist = event.artist,
                startTime = event.startTime,
                endTime = event.endTime,
                venue = event.venue,
                date = event.date,
                startEpoch = event.startEpoch,
                endEpoch = event.endEpoch,
                crossesMidnight = event.crossesMidnight,
                isInMySchedule = event.isInMySchedule
            )
        }
    }
    
    internal fun updateMyScheduleStatus(event: ScheduleEvent, isInMySchedule: Boolean) {
        dbQuery.updateMyScheduleStatus(
            isInMySchedule = isInMySchedule,
            artist = event.artist,
            startEpoch = event.startEpoch
        )
    }

    private fun mapEvent(
        artist: String,
        startTime: String,
        endTime: String,
        venue: String,
        date: String,
        startEpoch: Long,
        endEpoch: Long,
        crossesMidnight: Boolean?,
        isInMySchedule: Boolean
    ): ScheduleEvent = ScheduleEvent(
        artist = artist,
        startTime = startTime,
        endTime = endTime,
        venue = venue,
        date = date,
        startEpoch = startEpoch,
        endEpoch = endEpoch,
        crossesMidnight = crossesMidnight ?: false,
        isInMySchedule = isInMySchedule
    )
}