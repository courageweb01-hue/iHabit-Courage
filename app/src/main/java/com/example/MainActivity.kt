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
            val logs by viewModel.logs.collectAsStateWithLifecycle()
            val heatmapData by viewModel.past28DaysHeatmap.collectAsStateWithLifecycle()
            val categoryStats by viewModel.categoryStats.collectAsStateWithLifecycle()

            HabitTrackerTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FrostedMeshBackground {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (currentTab) {
                                NavTab.TODAY -> TodayScreen(
                                    selectedDate = selectedDate,
                                    habits = habitsForSelectedDate,
                                    onSelectDate = { viewModel.setSelectedDate(it) },
                                    onToggleHabit = { id, isDone -> viewModel.toggleHabit(id, isDone) },
                                    onDeleteHabit = { habit -> viewModel.deleteHabit(habit) },
                                    onAddHabitClick = { showAddSheet = true }
                                )

                                NavTab.EXPLORE -> ExploreScreen(
                                    presets = viewModel.presetLibrary,
                                    onAddPreset = { preset -> viewModel.addPreset(preset) }
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
                                    onSave = { title, category, icon, colorHex, targetDays ->
                                        viewModel.addHabit(title, category, icon, colorHex, "EVERYDAY", targetDays)
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
