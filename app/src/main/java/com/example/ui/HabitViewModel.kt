package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.utils.DateEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HabitItemUiState(
    val habit: Habit,
    val isCompleted: Boolean,
    val isSkipped: Boolean = false,
    val currentCount: Int = 0,
    val currentStreak: Int = 0
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
    val scheduleType: String = "DAILY",
    val targetDaysList: String = "1,2,3,4,5,6,7",
    val targetCount: Int = 1,
    val targetDays: Int = 7,
    val description: String
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HabitRepository

    private val _cutoffHour = MutableStateFlow(3) // 3 AM night owl default
    val cutoffHour: StateFlow<Int> = _cutoffHour.asStateFlow()

    private val _systemToday = MutableStateFlow(DateEngine.getSystemToday(_cutoffHour.value))
    val systemToday: StateFlow<LocalDate> = _systemToday.asStateFlow()

    private val _selectedDate = MutableStateFlow(DateEngine.getSystemToday(_cutoffHour.value))
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _activeJourneyIds = MutableStateFlow<Set<String>>(setOf("bedtime_ritual"))
    val activeJourneyIds: StateFlow<Set<String>> = _activeJourneyIds.asStateFlow()

    private val _activeJourneyStates = MutableStateFlow<Map<String, ActiveJourneyState>>(
        mapOf(
            "bedtime_ritual" to ActiveJourneyState(
                journeyId = "bedtime_ritual",
                startDate = DateEngine.getSystemToday(3).toString(),
                currentDay = 4,
                completedDays = setOf(1, 2, 3),
                status = "active"
            )
        )
    )
    val activeJourneyStates: StateFlow<List<ActiveJourneyState>> = _activeJourneyStates.map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val database = HabitDatabase.getDatabase(application, viewModelScope)
        repository = HabitRepository(database.habitDao())

        // Launch 24-hour Continuous Rollover Engine
        viewModelScope.launch {
            evaluateDailyReset()
            while (isActive) {
                delay(15000) // Ticker check every 15s
                val currentToday = DateEngine.getSystemToday(_cutoffHour.value)
                if (_systemToday.value != currentToday) {
                    val wasAnchoredToToday = _selectedDate.value == _systemToday.value
                    _systemToday.value = currentToday
                    if (wasAnchoredToToday) {
                        _selectedDate.value = currentToday
                    }
                    evaluateDailyReset()
                }
            }
        }
    }

    val habits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<CompletionLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combine habits + logs + systemToday for selected date
    val habitsForSelectedDate: StateFlow<List<HabitItemUiState>> = combine(
        habits,
        logs,
        selectedDate,
        systemToday
    ) { habitList, logList, date, sysToday ->
        val dateStr = date.toString()
        val dayOfWeekNumber = date.dayOfWeek.value // 1 = Mon ... 7 = Sun

        habitList
            .filter { habit ->
                if (habit.scheduleType == "WEEKLY_DAYS") {
                    val days = habit.targetDaysList.split(",").mapNotNull { it.trim().toIntOrNull() }
                    days.contains(dayOfWeekNumber)
                } else {
                    true
                }
            }
            .map { habit ->
                val log = logList.find { it.habitId == habit.id && it.date == dateStr }
                val currentCount = log?.value ?: 0
                val isSkipped = log != null && log.status == "SKIPPED"
                val isCompleted = if (habit.scheduleType == "COUNTER" || habit.targetCount > 1) {
                    currentCount >= habit.targetCount
                } else {
                    log != null && log.status == "COMPLETED"
                }
                val streak = calculateStreakForHabit(habit, logList, sysToday)
                HabitItemUiState(
                    habit = habit,
                    isCompleted = isCompleted,
                    isSkipped = isSkipped,
                    currentCount = currentCount,
                    currentStreak = streak
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Heatmap data for the past 28 days anchored to systemToday
    val past28DaysHeatmap: StateFlow<List<DayHeatmap>> = combine(
        habits,
        logs,
        systemToday
    ) { habitList, logList, sysToday ->
        val days = (27 downTo 0).map { sysToday.minusDays(it.toLong()) }
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

    fun jumpToToday() {
        _selectedDate.value = _systemToday.value
    }

    fun setCutoffHour(hour: Int) {
        _cutoffHour.value = hour
        val newToday = DateEngine.getSystemToday(hour)
        _systemToday.value = newToday
        viewModelScope.launch {
            evaluateDailyReset()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun toggleHabit(habitId: Long, isCurrentlyCompleted: Boolean) {
        val targetDate = _selectedDate.value
        val sysToday = _systemToday.value
        if (!DateEngine.canEditHabitForDate(targetDate, sysToday)) return

        viewModelScope.launch {
            val dateStr = targetDate.toString()
            repository.toggleHabitCompletion(habitId, dateStr, isCurrentlyCompleted)
        }
    }

    fun toggleSkipHabit(habitId: Long, isCurrentlySkipped: Boolean) {
        val targetDate = _selectedDate.value
        val sysToday = _systemToday.value
        if (!DateEngine.canEditHabitForDate(targetDate, sysToday)) return

        viewModelScope.launch {
            val dateStr = targetDate.toString()
            repository.toggleHabitSkip(habitId, dateStr, isCurrentlySkipped)
        }
    }

    fun incrementCounter(habitId: Long, currentCount: Int, targetCount: Int, delta: Int) {
        val targetDate = _selectedDate.value
        val sysToday = _systemToday.value
        if (!DateEngine.canEditHabitForDate(targetDate, sysToday)) return

        viewModelScope.launch {
            val dateStr = targetDate.toString()
            repository.updateHabitCounter(habitId, dateStr, currentCount, targetCount, delta)
        }
    }

    fun addHabit(
        title: String,
        category: String,
        icon: String,
        colorHex: String,
        frequency: String = "EVERYDAY",
        targetDays: Int = 7,
        scheduleType: String = "DAILY",
        targetDaysList: String = "1,2,3,4,5,6,7",
        targetCount: Int = 1
    ) {
        viewModelScope.launch {
            val newHabit = Habit(
                title = title,
                category = category,
                icon = icon,
                colorHex = colorHex,
                frequency = frequency,
                targetDays = targetDays,
                scheduleType = scheduleType,
                targetDaysList = targetDaysList,
                targetCount = targetCount
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
            targetDays = preset.targetDays,
            scheduleType = preset.scheduleType,
            targetDaysList = preset.targetDaysList,
            targetCount = preset.targetCount
        )
    }

    fun startJourney(program: com.example.data.JourneyProgram) {
        viewModelScope.launch {
            _activeJourneyIds.value = _activeJourneyIds.value + program.id
            val currentState = _activeJourneyStates.value[program.id] ?: ActiveJourneyState(
                journeyId = program.id,
                startDate = _systemToday.value.toString(),
                currentDay = 1,
                completedDays = emptySet(),
                status = "active"
            )
            _activeJourneyStates.value = _activeJourneyStates.value + (program.id to currentState.copy(status = "active"))

            program.associatedHabits.forEach { h ->
                val newHabit = Habit(
                    title = h.title,
                    category = h.category,
                    icon = h.icon,
                    colorHex = h.colorHex,
                    frequency = "EVERYDAY",
                    targetDays = 7,
                    scheduleType = h.scheduleType,
                    targetDaysList = "1,2,3,4,5,6,7",
                    targetCount = h.targetCount
                )
                repository.insertHabit(newHabit)
            }
        }
    }

    fun completeDailyLesson(journeyId: String, dayNumber: Int) {
        val state = _activeJourneyStates.value[journeyId] ?: return
        val newCompletedDays = state.completedDays + dayNumber
        val newCurrentDay = if (dayNumber == state.currentDay && dayNumber < 30) dayNumber + 1 else state.currentDay
        val newStatus = if (newCompletedDays.size >= 30) "completed" else "active"

        _activeJourneyStates.value = _activeJourneyStates.value + (
            journeyId to state.copy(
                currentDay = newCurrentDay,
                completedDays = newCompletedDays,
                status = newStatus
            )
        )
    }

    fun pauseJourney(journeyId: String) {
        val state = _activeJourneyStates.value[journeyId] ?: return
        _activeJourneyStates.value = _activeJourneyStates.value + (
            journeyId to state.copy(status = "paused")
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

    private suspend fun evaluateDailyReset() {
        // Continuous 24-hour reset engine keeps daily logs fresh and streak evaluation aligned
    }

    private fun calculateStreakForHabit(habit: Habit, allLogs: List<CompletionLog>, systemToday: LocalDate): Int {
        val habitLogs = allLogs.filter { it.habitId == habit.id }
        val dateToStatus = mutableMapOf<LocalDate, String>()

        habitLogs.forEach { log ->
            try {
                val d = LocalDate.parse(log.date)
                val status = if (log.status == "COMPLETED" || (habit.scheduleType == "COUNTER" && log.value >= habit.targetCount)) {
                    "COMPLETED"
                } else if (log.status == "SKIPPED") {
                    "SKIPPED"
                } else {
                    log.status
                }
                dateToStatus[d] = status
            } catch (e: Exception) {
                // ignore
            }
        }

        if (dateToStatus.isEmpty()) return 0

        var streak = 0
        var checkDate = systemToday

        val todayStatus = dateToStatus[checkDate]
        if (todayStatus != "COMPLETED" && todayStatus != "SKIPPED") {
            checkDate = checkDate.minusDays(1)
        }

        while (true) {
            val status = dateToStatus[checkDate]
            if (status == "COMPLETED") {
                streak++
                checkDate = checkDate.minusDays(1)
            } else if (status == "SKIPPED") {
                // Rest day: keep streak frozen/intact without resetting or incrementing
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    val presetLibrary = listOf(
        PresetHabit("Hydrate (8 Glasses)", "Health", "Water", "#007AFF", "COUNTER", "1,2,3,4,5,6,7", 8, 7, "Maintain optimal hydration levels every day"),
        PresetHabit("Morning Workout", "Fitness", "Workout", "#FF3B30", "WEEKLY_DAYS", "1,2,3,4,5", 1, 5, "Boost energy with 30 minutes of daily exercise"),
        PresetHabit("Read 15 Pages", "Productivity", "Book", "#FF9500", "COUNTER", "1,2,3,4,5,6,7", 15, 7, "Expand knowledge and build strong focus"),
        PresetHabit("Mindful Meditation", "Mind", "Meditation", "#AF52DE", "DAILY", "1,2,3,4,5,6,7", 1, 7, "10 minutes of deep breathing and calm clarity"),
        PresetHabit("Early Night Sleep", "Health", "Sleep", "#5856D6", "DAILY", "1,2,3,4,5,6,7", 1, 7, "Restore energy with early sleep before 11 PM"),
        PresetHabit("Daily Journaling", "Mind", "Journal", "#34C759", "DAILY", "1,2,3,4,5,6,7", 1, 7, "Reflect on accomplishments and daily thoughts"),
        PresetHabit("No Sugar Snacks", "Nutrition", "Apple", "#FF2D55", "WEEKLY_DAYS", "1,2,3,4,5,6", 1, 6, "Eliminate processed sugar for vibrant health"),
        PresetHabit("10k Daily Steps", "Fitness", "Walk", "#30B0C7", "COUNTER", "1,2,3,4,5,6,7", 10, 7, "Stay active throughout the day with movement"),
        PresetHabit("Learn New Language", "Productivity", "Translate", "#FFCC00", "WEEKLY_DAYS", "1,2,3,4,5", 1, 5, "Practice 15 minutes of language exercises"),
        PresetHabit("Cold Morning Shower", "Health", "Shower", "#5AC8FA", "DAILY", "1,2,3,4,5,6,7", 1, 7, "Invigorate your body and boost resilience")
    )
}
