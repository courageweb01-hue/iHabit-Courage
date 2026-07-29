package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JourneyProgram
import com.example.ui.theme.FrostedPurplePrimary
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyDetailModal(
    program: JourneyProgram,
    isActive: Boolean,
    onDismiss: () -> Unit,
    onStartJourney: (JourneyProgram) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Science & Guidance, 1 = 30-Day Path, 2 = Schedule & Reminders
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderTime by remember { mutableStateOf(program.defaultReminderTime) }

    val themeColor = try {
        Color(android.graphics.Color.parseColor(program.colorTheme))
    } catch (e: Exception) {
        IOSBlue
    }

    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp) // Space for sticky footer button
                    .verticalScroll(scrollState)
            ) {
                // Top Header Card / Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(themeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = themeColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = program.category.uppercase(),
                                    color = themeColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("btn_close_journey_modal")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(themeColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getIconForHabit(program.iconName),
                                    contentDescription = program.title,
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = program.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = program.tagline,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Interactive Segmented Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = themeColor,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Science & Guidance", fontSize = 12.sp, maxLines = 1) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("30-Day Path", fontSize = 12.sp, maxLines = 1) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Schedule", fontSize = 12.sp, maxLines = 1) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    when (selectedTab) {
                        0 -> ScienceAndGuidanceTab(program = program, themeColor = themeColor)
                        1 -> TimelinePathTab(program = program, themeColor = themeColor)
                        2 -> ScheduleRemindersTab(
                            program = program,
                            themeColor = themeColor,
                            reminderEnabled = reminderEnabled,
                            onToggleReminder = { reminderEnabled = it },
                            reminderTime = reminderTime,
                            onTimeChange = { reminderTime = it }
                        )
                    }
                }
            }

            // Sticky Bottom iOS Action Footer Button
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isActive) {
                                onStartJourney(program)
                            }
                        },
                        enabled = !isActive,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("btn_start_journey"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) IOSGreen else themeColor,
                            disabledContainerColor = IOSGreen.copy(alpha = 0.85f),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.RocketLaunch,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isActive) "JOURNEY IN PROGRESS (30-DAY)" else "🚀 START 30-DAY JOURNEY",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScienceAndGuidanceTab(
    program: JourneyProgram,
    themeColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Educational Essay Breakdown
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = "Science",
                        tint = themeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "The Science Behind This Program",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = program.essay,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }

        // Pro Tips
        Column {
            Text(
                text = "ACTIONABLE PRO TIPS",
                style = MaterialTheme.typography.labelMedium,
                color = themeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            program.proTips.forEachIndexed { index, tip ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(themeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = themeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Key Results & Expectations
        Column {
            Text(
                text = "EXPECTED OUTCOMES",
                style = MaterialTheme.typography.labelMedium,
                color = themeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            program.keyResults.forEach { res ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = IOSGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = res,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Mindset Shifts
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = program.moreToExpect,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun TimelinePathTab(
    program: JourneyProgram,
    themeColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "YOUR 30-DAY PROGRESSION ARC",
            style = MaterialTheme.typography.labelMedium,
            color = themeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        val phases = listOf(
            program.phase1 to "Phase 1",
            program.phase2 to "Phase 2",
            program.phase3 to "Phase 3"
        )

        phases.forEachIndexed { idx, (phase, phaseLabel) ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, themeColor.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(themeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = themeColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = phaseLabel.uppercase(),
                                color = themeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = phase.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = phase.goal,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Associated Habits to be added
        Text(
            text = "HABITS INCLUDED IN THIS JOURNEY",
            style = MaterialTheme.typography.labelMedium,
            color = themeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        program.associatedHabits.forEach { habit ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForHabit(habit.icon),
                            contentDescription = habit.title,
                            tint = themeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Daily • ${habit.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleRemindersTab(
    program: JourneyProgram,
    themeColor: Color,
    reminderEnabled: Boolean,
    onToggleReminder: (Boolean) -> Unit,
    reminderTime: String,
    onTimeChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "SMART PROGRAM REMINDER",
            style = MaterialTheme.typography.labelMedium,
            color = themeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Daily Reminder",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Receive smart daily prompts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = onToggleReminder,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = themeColor)
                    )
                }

                if (reminderEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notification Time",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Time preset options
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("07:00 AM", "12:30 PM", "08:00 PM", "09:30 PM").forEach { t ->
                                val isSelected = reminderTime == t
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) themeColor else MaterialTheme.colorScheme.surface)
                                        .clickable { onTimeChange(t) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = t,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
