package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Long)

    // Completion Logs
    @Query("SELECT * FROM completion_logs WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<CompletionLog>>

    @Query("SELECT * FROM completion_logs")
    fun getAllLogs(): Flow<List<CompletionLog>>

    @Query("SELECT * FROM completion_logs WHERE habitId = :habitId")
    fun getLogsForHabit(habitId: Long): Flow<List<CompletionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CompletionLog)

    @Query("DELETE FROM completion_logs WHERE habitId = :habitId AND date = :date")
    suspend fun deleteLogForDate(habitId: Long, date: String)

    @Query("DELETE FROM completion_logs WHERE habitId = :habitId")
    suspend fun deleteLogsForHabit(habitId: Long)

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()

    @Query("DELETE FROM completion_logs")
    suspend fun deleteAllLogs()
}
