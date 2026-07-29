package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HabitViewModel
import com.example.ui.components.AddHabitSheet
import com.example.ui.components.CupertinoTabBar
import com.example.ui.components.NavTab
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.HabitTrackerTheme

import com.example.ui.components.FrostedMeshBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var systemDark = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemDark) }
            var currentTab by remember { mutableStateOf(NavTab.TODAY) }
            var showAddSheet by remember { mutableStateOf(false) }

            val viewModel: HabitViewModel = viewModel()

            val habitsForSelectedDate by viewModel.habitsForSelectedDate.collectAsStateWithLifecycle()
            val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
            val systemToday by viewModel.systemToday.collectAsStateWithLifecycle()
            val cutoffHour by viewModel.cutoffHour.collectAsStateWithLifecycle()
            val logs by viewModel.logs.collectAsStateWithLifecycle()
            val heatmapData by viewModel.past28DaysHeatmap.collectAsStateWithLifecycle()
            val categoryStats by viewModel.categoryStats.collectAsStateWithLifecycle()
            val activeJourneyIds by viewModel.activeJourneyIds.collectAsStateWithLifecycle()
            val activeJourneyStates by viewModel.activeJourneyStates.collectAsStateWithLifecycle()

            HabitTrackerTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FrostedMeshBackground {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (currentTab) {
                                NavTab.TODAY -> TodayScreen(
                                    selectedDate = selectedDate,
                                    systemToday = systemToday,
                                    habits = habitsForSelectedDate,
                                    activeJourneyStates = activeJourneyStates,
                                    onSelectDate = { viewModel.setSelectedDate(it) },
                                    onJumpToToday = { viewModel.jumpToToday() },
                                    onToggleHabit = { id, isDone -> viewModel.toggleHabit(id, isDone) },
                                    onSkipHabit = { id, isSkipped -> viewModel.toggleSkipHabit(id, isSkipped) },
                                    onIncrementCounterHabit = { id, currentCount, targetCount, delta ->
                                        viewModel.incrementCounter(id, currentCount, targetCount, delta)
                                    },
                                    onDeleteHabit = { habit -> viewModel.deleteHabit(habit) },
                                    onAddHabitClick = { showAddSheet = true },
                                    onCompleteDailyLesson = { journeyId, dayNumber ->
                                        viewModel.completeDailyLesson(journeyId, dayNumber)
                                    },
                                    onPauseJourney = { journeyId -> viewModel.pauseJourney(journeyId) },
                                    onStartJourney = { program -> viewModel.startJourney(program) }
                                )

                                NavTab.EXPLORE -> ExploreScreen(
                                    presets = viewModel.presetLibrary,
                                    activeJourneyIds = activeJourneyIds,
                                    onAddPreset = { preset -> viewModel.addPreset(preset) },
                                    onStartJourney = { program -> viewModel.startJourney(program) }
                                )

                                NavTab.STATS -> StatsScreen(
                                    habits = habitsForSelectedDate,
                                    logs = logs,
                                    heatmapData = heatmapData,
                                    categoryStats = categoryStats
                                )

                                NavTab.SETTINGS -> SettingsScreen(
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = { isDarkMode = it },
                                    cutoffHour = cutoffHour,
                                    onCutoffHourChange = { viewModel.setCutoffHour(it) },
                                    onResetData = { viewModel.resetAllData() }
                                )
                            }

                            // Cupertino Bottom Floating Tab Bar
                            CupertinoTabBar(
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it },
                                modifier = Modifier
                                    .align(androidx.compose.ui.Alignment.BottomCenter)
                                    .fillMaxWidth()
                            )

                            // Add Habit Modal Sheet
                            if (showAddSheet) {
                                AddHabitSheet(
                                    onDismiss = { showAddSheet = false },
                                    onSave = { title, category, icon, colorHex, frequency, targetDays, scheduleType, targetDaysList, targetCount ->
                                        viewModel.addHabit(
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
                                        showAddSheet = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
