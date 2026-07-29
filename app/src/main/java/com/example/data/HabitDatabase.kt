package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Habit::class, CompletionLog::class], version = 2, exportSchema = false)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_tracker_db"
                )
                .addCallback(HabitDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class HabitDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.habitDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: HabitDao) {
                val initialHabits = listOf(
                    Habit(
                        title = "Drink 8 Glasses of Water",
                        category = "Health",
                        icon = "Water",
                        colorHex = "#007AFF",
                        scheduleType = "COUNTER",
                        targetCount = 8,
                        streak = 3
                    ),
                    Habit(
                        title = "30-Min Workout",
                        category = "Fitness",
                        icon = "Workout",
                        colorHex = "#FF3B30",
                        scheduleType = "WEEKLY_DAYS",
                        targetDaysList = "1,2,3,4,5", // Mon - Fri
                        targetDays = 5,
                        streak = 5
                    ),
                    Habit(
                        title = "Read 15 Pages",
                        category = "Productivity",
                        icon = "Book",
                        colorHex = "#FF9500",
                        scheduleType = "COUNTER",
                        targetCount = 15,
                        streak = 2
                    ),
                    Habit(
                        title = "10-Min Meditation",
                        category = "Mind",
                        icon = "Meditation",
                        colorHex = "#AF52DE",
                        scheduleType = "DAILY",
                        streak = 4
                    ),
                    Habit(
                        title = "Sleep Before 11 PM",
                        category = "Health",
                        icon = "Sleep",
                        colorHex = "#5856D6",
                        scheduleType = "DAILY",
                        streak = 1
                    )
                )

                for (habit in initialHabits) {
                    val habitId = dao.insertHabit(habit)
                    // Add some sample past completions for streaks
                    val today = java.time.LocalDate.now()
                    for (i in 1..habit.streak) {
                        val pastDate = today.minusDays(i.toLong()).toString()
                        dao.insertLog(
                            CompletionLog(
                                habitId = habitId,
                                date = pastDate,
                                status = "COMPLETED"
                            )
                        )
                    }
                }
            }
        }
    }
}
