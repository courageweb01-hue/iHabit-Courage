package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Habit::class, CompletionLog::class], version = 1, exportSchema = false)
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
                        title = "Drink 2L Water",
                        category = "Health",
                        icon = "Water",
                        colorHex = "#007AFF",
                        targetDays = 7,
                        streak = 3
                    ),
                    Habit(
                        title = "30-Min Workout",
                        category = "Fitness",
                        icon = "Workout",
                        colorHex = "#FF3B30",
                        targetDays = 5,
                        streak = 5
                    ),
                    Habit(
                        title = "Read 15 Pages",
                        category = "Productivity",
                        icon = "Book",
                        colorHex = "#FF9500",
                        targetDays = 7,
                        streak = 2
                    ),
                    Habit(
                        title = "10-Min Meditation",
                        category = "Mind",
                        icon = "Meditation",
                        colorHex = "#AF52DE",
                        targetDays = 7,
                        streak = 4
                    ),
                    Habit(
                        title = "Sleep Before 11 PM",
                        category = "Health",
                        icon = "Sleep",
                        colorHex = "#5856D6",
                        targetDays = 7,
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
