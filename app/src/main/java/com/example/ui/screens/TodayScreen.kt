package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ActiveJourneyState
import com.example.data.Habit
import com.example.data.JourneyProgram
import com.example.data.JourneysData
import com.example.ui.HabitItemUiState
import com.example.ui.components.ActiveJourneyBanner
import com.example.ui.components.DailyLessonModal
import com.example.ui.components.FocusTimerModal
import com.example.ui.components.HabitCard
import com.example.ui.components.JourneyDetailModal
import com.example.ui.theme.FrostedPurplePrimary
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSGreen
import com.example.ui.theme.IOSOrange
import com.example.utils.DateEditStatus
import com.example.utils.DateEngine
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(
    selectedDate: LocalDate,
    systemToday: LocalDate,
    habits: List<HabitItemUiState>,
    activeJourneyStates: List<ActiveJourneyState> = emptyList(),
    onSelectDate: (LocalDate) -> Unit,
    onJumpToToday: () -> Unit,
    onToggleHabit: (Long, Boolean) -> Unit,
    onSkipHabit: (habitId: Long, isSkipped: Boolean) -> Unit,
    onIncrementCounterHabit: (habitId: Long, currentCount: Int, targetCount: Int, delta: Int) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onAddHabitClick: () -> Unit,
    onCompleteDailyLesson: (journeyId: String, dayNumber: Int) -> Unit = { _, _ -> },
    onPauseJourney: (journeyId: String) -> Unit = {},
    onStartJourney: (JourneyProgram) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeTimerHabit by remember { mutableStateOf<Habit?>(null) }
    var activeDailyLessonState by remember { mutableStateOf<Pair<ActiveJourneyState, JourneyProgram>?>(null) }
    var activeRoadmapProgram by remember { mutableStateOf<JourneyProgram?>(null) }

    val dateDays = (-4..4).map { systemToday.plusDays(it.toLong()) }
    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size
    val progressRatio = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val dateEditStatus = DateEngine.getDateEditStatus(selectedDate, systemToday)
    val isNotToday = selectedDate != systemToday

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabitClick,
                containerColor = FrostedPurplePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 90.dp)
                    .testTag("fab_add_habit")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Habit",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Title & Jump to Today Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                            style = MaterialTheme.typography.labelMedium,
                            color = FrostedPurplePrimary,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (selectedDate == systemToday) "Today" else if (selectedDate == systemToday.minusDays(1)) "Yesterday" else if (selectedDate.isAfter(systemToday)) "Scheduled" else "History",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isNotToday) {
                        Button(
                            onClick = onJumpToToday,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FrostedPurplePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_jump_to_today")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Jump to Today",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Today",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Horizontal Date Ribbon Selector
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dateDays) { day ->
                        val isSelected = day == selectedDate
                        val isToday = day == systemToday

                        Box(
                            modifier = Modifier
                                .width(58.dp)
                                .height(78.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    if (isSelected) FrostedPurplePrimary
                                    else if (isToday) FrostedPurplePrimary.copy(alpha = 0.18f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable { onSelectDate(day) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isToday) "TODAY" else day.format(DateTimeFormatter.ofPattern("EEE")).uppercase(),
                                    fontSize = 10.sp,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else if (isToday) FrostedPurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    fontSize = 18.sp,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Active Journey Banners (pinned right on Today's Dashboard)
            items(activeJourneyStates.filter { it.status == "active" }) { activeState ->
                val program = JourneysData.programs.find { it.id == activeState.journeyId }
                if (program != null) {
                    ActiveJourneyBanner(
                        activeState = activeState,
                        program = program,
                        onOpenDailyLesson = {
                            activeDailyLessonState = Pair(activeState, program)
                        },
                        onPauseJourney = { onPauseJourney(activeState.journeyId) },
                        onViewRoadmap = { activeRoadmapProgram = program },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            // Today's Summary Card (Frosted Glass)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Goal Progress",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$completedCount of $totalCount habits completed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { progressRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = FrostedPurplePrimary,
                                trackColor = FrostedPurplePrimary.copy(alpha = 0.15f)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Percentage Circle Text
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(FrostedPurplePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${(progressRatio * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = FrostedPurplePrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Habits Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR HABITS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${habits.size} Items",
                        style = MaterialTheme.typography.labelMedium,
                        color = IOSBlue,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Empty State
            if (habits.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Empty",
                            tint = IOSBlue.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Habits Set for Today",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap '+' or browse Explore to add new habits",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(habits, key = { it.habit.id }) { itemState ->
                    HabitCard(
                        habitState = itemState,
                        onToggle = { onToggleHabit(itemState.habit.id, itemState.isCompleted) },
                        onSkip = { onSkipHabit(itemState.habit.id, itemState.isSkipped) },
                        onIncrementCounter = { delta ->
                            onIncrementCounterHabit(
                                itemState.habit.id,
                                itemState.currentCount,
                                itemState.habit.targetCount,
                                delta
                            )
                        },
                        onDelete = { onDeleteHabit(itemState.habit) },
                        onOpenTimer = { activeTimerHabit = itemState.habit },
                        dateEditStatus = dateEditStatus
                    )
                }
            }
        }

        // Active Focus Timer Modal Sheet
        activeTimerHabit?.let { habitToTimer ->
            FocusTimerModal(
                habit = habitToTimer,
                onDismiss = { activeTimerHabit = null },
                onCompleteHabit = {
                    val itemState = habits.find { it.habit.id == habitToTimer.id }
                    if (itemState != null) {
                        if (habitToTimer.scheduleType == "COUNTER" || habitToTimer.targetCount > 1) {
                            val remainingNeeded = habitToTimer.targetCount - itemState.currentCount
                            if (remainingNeeded > 0) {
                                onIncrementCounterHabit(
                                    habitToTimer.id,
                                    itemState.currentCount,
                                    habitToTimer.targetCount,
                                    remainingNeeded
                                )
                            }
                        } else {
                            if (!itemState.isCompleted) {
                                onToggleHabit(habitToTimer.id, false)
                            }
                        }
                    }
                }
            )
        }
        // Active Daily Coaching Lesson Modal Sheet
        activeDailyLessonState?.let { (state, program) ->
            DailyLessonModal(
                activeState = state,
                program = program,
                onDismiss = { activeDailyLessonState = null },
                onCompleteLesson = { dayNumber ->
                    onCompleteDailyLesson(program.id, dayNumber)
                }
            )
        }

        // Active Roadmap Detail Modal Sheet
        activeRoadmapProgram?.let { prog ->
            val isActive = activeJourneyStates.any { it.journeyId == prog.id && it.status == "active" }
            JourneyDetailModal(
                program = prog,
                isActive = isActive,
                onDismiss = { activeRoadmapProgram = null },
                onStartJourney = { p ->
                    onStartJourney(p)
                    activeRoadmapProgram = null
                }
            )
        }
    }
}
