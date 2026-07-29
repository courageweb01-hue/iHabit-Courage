package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HabitItemUiState(
    val habit: Habit,
    val isCompleted: Boolean,
    val currentStreak: Int
)

data class DayHeatmap(
    val date: LocalDate,
    val dateString: String,
    val completionRatio: Float, // 0.0 to 1.0
    val totalHabits: Int,
    val completedHabits: Int
)

data class CategoryStat(
    val category: String,
    val count: Int,
    val colorHex: String
)

data class PresetHabit(
    val title: String,
    val category: String,
    val icon: String,
    val colorHex: String,
    val targetDays: Int = 7,
    val description: String
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HabitRepository
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    init {
        val database = HabitDatabase.getDatabase(application, viewModelScope)
        repository = HabitRepository(database.habitDao())
    }

    val habits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<CompletionLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combine habits + logs for selected date
    val habitsForSelectedDate: StateFlow<List<HabitItemUiState>> = combine(
        habits,
        logs,
        selectedDate
    ) { habitList, logList, date ->
        val dateStr = date.toString()
        val logsOnDate = logList.filter { it.date == dateStr && it.status == "COMPLETED" }.map { it.habitId }.toSet()

        habitList.map { habit ->
            val isCompleted = logsOnDate.contains(habit.id)
            val streak = calculateStreakForHabit(habit.id, logList)
            HabitItemUiState(
                habit = habit,
                isCompleted = isCompleted,
                currentStreak = streak
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Heatmap data for the past 28 days
    val past28DaysHeatmap: StateFlow<List<DayHeatmap>> = combine(
        habits,
        logs
    ) { habitList, logList ->
        val today = LocalDate.now()
        val days = (27 downTo 0).map { today.minusDays(it.toLong()) }
        val activeHabitsCount = habitList.size.coerceAtLeast(1)

        days.map { date ->
            val dateStr = date.toString()
            val completedOnDay = logList.filter { it.date == dateStr && it.status == "COMPLETED" }.map { it.habitId }.distinct().size
            val ratio = (completedOnDay.toFloat() / activeHabitsCount.toFloat()).coerceIn(0f, 1f)
            DayHeatmap(
                date = date,
                dateString = dateStr,
                completionRatio = ratio,
                totalHabits = activeHabitsCount,
                completedHabits = completedOnDay
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Category distribution stats
    val categoryStats: StateFlow<List<CategoryStat>> = habits.map { habitList ->
        habitList.groupBy { it.category }
            .map { (cat, list) ->
                CategoryStat(
                    category = cat,
                    count = list.size,
                    colorHex = list.firstOrNull()?.colorHex ?: "#007AFF"
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun toggleHabit(habitId: Long, isCurrentlyCompleted: Boolean) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.toString()
            repository.toggleHabitCompletion(habitId, dateStr, isCurrentlyCompleted)
        }
    }

    fun addHabit(
        title: String,
        category: String,
        icon: String,
        colorHex: String,
        frequency: String,
        targetDays: Int
    ) {
        viewModelScope.launch {
            val newHabit = Habit(
                title = title,
                category = category,
                icon = icon,
                colorHex = colorHex,
                frequency = frequency,
                targetDays = targetDays
            )
            repository.insertHabit(newHabit)
        }
    }

    fun addPreset(preset: PresetHabit) {
        addHabit(
            title = preset.title,
            category = preset.category,
            icon = preset.icon,
            colorHex = preset.colorHex,
            frequency = "EVERYDAY",
            targetDays = preset.targetDays
        )
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }

    private fun calculateStreakForHabit(habitId: Long, allLogs: List<CompletionLog>): Int {
        val dates = allLogs.filter { it.habitId == habitId && it.status == "COMPLETED" }
            .mapNotNull {
                try {
                    LocalDate.parse(it.date)
                } catch (e: Exception) {
                    null
                }
            }
            .toSet()

        if (dates.isEmpty()) return 0

        var streak = 0
        var checkDate = LocalDate.now()

        // If not completed today, check yesterday as starting point for streak
        if (!dates.contains(checkDate)) {
            checkDate = checkDate.minusDays(1)
        }

        while (dates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }

    val presetLibrary = listOf(
        PresetHabit("Hydrate (8 Glasses)", "Health", "Water", "#007AFF", 7, "Maintain optimal hydration levels every day"),
        PresetHabit("Morning Workout", "Fitness", "Workout", "#FF3B30", 5, "Boost energy with 30 minutes of daily exercise"),
        PresetHabit("Read 15 Pages", "Productivity", "Book", "#FF9500", 7, "Expand knowledge and build strong focus"),
        PresetHabit("Mindful Meditation", "Mind", "Meditation", "#AF52DE", 7, "10 minutes of deep breathing and calm clarity"),
        PresetHabit("Early Night Sleep", "Health", "Sleep", "#5856D6", 7, "Restore energy with early sleep before 11 PM"),
        PresetHabit("Daily Journaling", "Mind", "Journal", "#34C759", 7, "Reflect on accomplishments and daily thoughts"),
        PresetHabit("No Sugar Snacks", "Nutrition", "Apple", "#FF2D55", 6, "Eliminate processed sugar for vibrant health"),
        PresetHabit("10k Daily Steps", "Fitness", "Walk", "#30B0C7", 7, "Stay active throughout the day with movement"),
        PresetHabit("Learn New Language", "Productivity", "Translate", "#FFCC00", 5, "Practice 15 minutes of language exercises"),
        PresetHabit("Cold Morning Shower", "Health", "Shower", "#5AC8FA", 7, "Invigorate your body and boost resilience")
    )
}
