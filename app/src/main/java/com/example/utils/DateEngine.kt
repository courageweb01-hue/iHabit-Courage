package com.example.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class DateEditStatus {
    EDITABLE_TODAY,       // Current active day (system today based on cutoff hour)
    RETROACTIVE_PAST,     // Yesterday / within 48h, retroactive logging allowed
    READONLY_PAST,        // > 48h in past, streak integrity locked history
    SCHEDULED_FUTURE      // Future date, read-only schedule preview
}

object DateEngine {

    /**
     * Calculates system "today" considering user's night-owl cutoff hour.
     * For example, with cutoffHour = 3, times between 00:00 and 02:59 are counted
     * as part of the previous calendar day.
     */
    fun getSystemToday(cutoffHour: Int = 3, now: LocalDateTime = LocalDateTime.now()): LocalDate {
        val date = now.toLocalDate()
        return if (now.hour < cutoffHour) {
            date.minusDays(1)
        } else {
            date
        }
    }

    /**
     * Determines editability status of a target date relative to system today.
     */
    fun getDateEditStatus(targetDate: LocalDate, systemToday: LocalDate): DateEditStatus {
        return when {
            targetDate.isAfter(systemToday) -> DateEditStatus.SCHEDULED_FUTURE
            targetDate == systemToday -> DateEditStatus.EDITABLE_TODAY
            targetDate == systemToday.minusDays(1) -> DateEditStatus.RETROACTIVE_PAST
            else -> DateEditStatus.READONLY_PAST
        }
    }

    /**
     * Checks if a date allows habit completion/toggling.
     * Allowed for Today (EDITABLE_TODAY) and Yesterday (RETROACTIVE_PAST).
     */
    fun canEditHabitForDate(targetDate: LocalDate, systemToday: LocalDate): Boolean {
        val status = getDateEditStatus(targetDate, systemToday)
        return status == DateEditStatus.EDITABLE_TODAY || status == DateEditStatus.RETROACTIVE_PAST
    }

    /**
     * Formats rollover hour into human-readable label.
     */
    fun getCutoffHourLabel(hour: Int): String {
        return when (hour) {
            0 -> "Midnight (12:00 AM)"
            1 -> "1:00 AM"
            2 -> "2:00 AM (Night Owl)"
            3 -> "3:00 AM (Late Night)"
            4 -> "4:00 AM (Early Bird)"
            else -> "$hour:00 AM"
        }
    }
}
