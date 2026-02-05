package com.altears.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.altears.data.remote.FestivalApi
import com.altears.db.AltEarsDatabase
import com.altears.db.Artist
import com.altears.db.Day
import com.altears.db.Show
import com.altears.db.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class FestivalRepository(
    private val api: FestivalApi,
    private val database: AltEarsDatabase
) {
    private val queries = database.altEarsQueries
    
    // Artists
    fun getArtists(): Flow<List<Artist>> = 
        queries.getAllArtists().asFlow().mapToList(Dispatchers.IO)
    
    fun getArtist(id: Int): Flow<Artist?> = 
        queries.getArtistById(id.toLong()).asFlow().mapToOneOrNull(Dispatchers.IO)
    
    // Shows
    fun getAllShows(): Flow<List<Show>> = 
        queries.getAllShows().asFlow().mapToList(Dispatchers.IO)
    
    fun getShowsForArtist(artistId: Int): Flow<List<Show>> = 
        queries.getShowsByArtistId(artistId.toLong()).asFlow().mapToList(Dispatchers.IO)
    
    fun getShowsForDay(dayId: Int): Flow<List<Show>> = 
        queries.getShowsByDayId(dayId.toLong()).asFlow().mapToList(Dispatchers.IO)
    
    // Schedule
    fun getScheduledShows(): Flow<List<Show>> = 
        queries.getScheduledShows().asFlow().mapToList(Dispatchers.IO)
    
    fun isShowScheduled(showId: Int): Flow<Boolean> = 
        queries.isShowScheduled(showId.toLong()).asFlow().mapToOneOrNull(Dispatchers.IO)
            .map { it ?: false }
    
    suspend fun addToSchedule(showId: Int) = withContext(Dispatchers.IO) {
        val now = Clock.System.now().epochSeconds
        queries.addToSchedule(showId.toLong(), now)
    }
    
    suspend fun removeFromSchedule(showId: Int) = withContext(Dispatchers.IO) {
        queries.removeFromSchedule(showId.toLong())
    }
    
    suspend fun toggleSchedule(showId: Int): Boolean = withContext(Dispatchers.IO) {
        val isScheduled = queries.isShowScheduled(showId.toLong()).executeAsOne()
        if (isScheduled) {
            queries.removeFromSchedule(showId.toLong())
            false
        } else {
            val now = Clock.System.now().epochSeconds
            queries.addToSchedule(showId.toLong(), now)
            true
        }
    }
    
    // Days
    fun getDays(): Flow<List<Day>> = 
        queries.getAllDays().asFlow().mapToList(Dispatchers.IO)
    
    // Stages  
    fun getStages(): Flow<List<Stage>> = 
        queries.getAllStages().asFlow().mapToList(Dispatchers.IO)
    
    // Sync
    suspend fun refreshData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.getDashboard()
            val data = response.data
            
            database.transaction {
                // Clear old data (except schedule)
                queries.clearArtists()
                queries.clearShows()
                queries.clearStages()
                queries.clearDays()
                
                // Insert artists
                data.artists.forEach { artist ->
                    queries.insertArtist(
                        id = artist.id.toLong(),
                        title = artist.title,
                        imageUrl = artist.imageUrl,
                        iconUrl = artist.iconUrl,
                        description = artist.text,
                        sortOrder = artist.sortOrder.toLong()
                    )
                }
                
                // Insert shows
                data.gigs.forEach { gig ->
                    queries.insertShow(
                        id = gig.id.toLong(),
                        artistId = gig.artistId.toLong(),
                        title = gig.title,
                        displayDate = gig.displayDate,
                        stageTitle = gig.stageTitle,
                        startTimestamp = gig.startTimestamp,
                        endTimestamp = gig.endTimestamp,
                        stageId = gig.stageId.toLong(),
                        dayId = gig.dayId.toLong(),
                        sortOrder = gig.sortOrder.toLong()
                    )
                }
                
                // Insert stages
                data.stages.forEach { stage ->
                    queries.insertStage(
                        id = stage.id.toLong(),
                        title = stage.title,
                        subtitle = stage.subtitle,
                        lat = stage.lat,
                        lng = stage.lng,
                        sortOrder = stage.sortOrder.toLong()
                    )
                }
                
                // Insert days
                data.days.forEach { day ->
                    queries.insertDay(
                        id = day.id.toLong(),
                        startTimestamp = day.startTimestamp,
                        endTimestamp = day.endTimestamp,
                        title = day.title,
                        subtitle = day.subtitle,
                        titleShort = day.titleShort,
                        sortOrder = day.sortOrder.toLong()
                    )
                }
                
                // Update sync timestamp
                queries.setSyncValue("lastSync", Clock.System.now().epochSeconds.toString())
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLastSyncTime(): Long? = withContext(Dispatchers.IO) {
        queries.getSyncValue("lastSync").executeAsOneOrNull()?.toLongOrNull()
    }
    
    suspend fun needsRefresh(): Boolean = withContext(Dispatchers.IO) {
        val lastSync = getLastSyncTime() ?: return@withContext true
        val now = Clock.System.now().epochSeconds
        val hoursSinceSync = (now - lastSync) / 3600
        hoursSinceSync >= 1 // Refresh if more than 1 hour old
    }
}
