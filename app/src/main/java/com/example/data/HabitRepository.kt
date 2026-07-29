package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HabitWithStatus(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val currentStreak: Int
)

data class StreakStats(
    val totalHabitsCount: Int,
    val totalCompletionsCount: Int,
    val activeStreakCount: Int,
    val todayCompletionRate: Float,
    val bestStreak: Int
)

class HabitRepository(private val habitDao: HabitDao) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allLogs: Flow<List<CompletionLog>> = habitDao.getAllLogs()

    fun getLogsForDate(date: String): Flow<List<CompletionLog>> {
        return habitDao.getLogsForDate(date)
    }

    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
        habitDao.deleteLogsForHabit(habit.id)
    }

    suspend fun toggleHabitCompletion(habitId: Long, dateStr: String, currentStatus: Boolean) {
        if (currentStatus) {
            // Remove log
            habitDao.deleteLogForDate(habitId, dateStr)
        } else {
            // Insert log
            habitDao.insertLog(
                CompletionLog(
                    habitId = habitId,
                    date = dateStr,
                    status = "COMPLETED"
                )
            )
        }
        updateStreakForHabit(habitId)
    }

    suspend fun updateStreakForHabit(habitId: Long) {
        val habit = habitDao.getHabitById(habitId) ?: return
        val logs = habitDao.getLogsForHabit(habitId)
        // Calculating streak
        // Note: For real time calculation, we can inspect logs
    }

    suspend fun resetAllData() {
        habitDao.deleteAllHabits()
        habitDao.deleteAllLogs()
    }
}
