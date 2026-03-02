package com.altears.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object TimeFormatter {
    
    /**
     * Festival timezone - Big Ears takes place in Knoxville, TN (Eastern Time)
     * All times should be displayed in Eastern regardless of user's location
     */
    private val FESTIVAL_TIMEZONE = TimeZone.of("America/New_York")
    
    /**
     * Formats a Unix timestamp (seconds) to 12-hour time format
     * Example: "7:30 PM"
     * 
     * Times are always displayed in Eastern Time (festival location)
     */
    fun formatTime(timestamp: Long, timeZone: TimeZone = FESTIVAL_TIMEZONE): String {
        val instant = Instant.fromEpochSeconds(timestamp)
        val localDateTime = instant.toLocalDateTime(timeZone)
        
        val hour = localDateTime.hour
        val minute = localDateTime.minute
        
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val amPm = if (hour < 12) "AM" else "PM"
        
        return if (minute == 0) {
            "$hour12 $amPm"
        } else {
            "$hour12:${minute.toString().padStart(2, '0')} $amPm"
        }
    }
    
    /**
     * Formats a time range from two timestamps
     * Example: "7:30 PM - 9:00 PM"
     * 
     * Times are always displayed in Eastern Time (festival location)
     */
    fun formatTimeRange(startTimestamp: Long, endTimestamp: Long, timeZone: TimeZone = FESTIVAL_TIMEZONE): String {
        return "${formatTime(startTimestamp, timeZone)} - ${formatTime(endTimestamp, timeZone)}"
    }
}
