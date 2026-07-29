package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // e.g., "Health", "Fitness", "Mind", "Productivity", "Nutrition"
    val icon: String, // e.g., "Water", "Workout", "Book", "Meditation", "Sleep", "Target"
    val colorHex: String, // e.g., "#007AFF"
    val frequency: String = "EVERYDAY", // "EVERYDAY", "WEEKDAYS", "WEEKENDS"
    val targetDays: Int = 7,
    val streak: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderTime: String? = null,
    val isArchived: Boolean = false
)

@Entity(tableName = "completion_logs")
data class CompletionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // Format: "YYYY-MM-DD"
    val status: String = "COMPLETED", // "COMPLETED", "SKIPPED"
    val value: Int = 1,
    val loggedAt: Long = System.currentTimeMillis()
)
