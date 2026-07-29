package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Habit
import com.example.ui.HabitItemUiState
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSGreen
import com.example.ui.theme.IOSOrange
import com.example.utils.DateEditStatus

import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.Timer

@Composable
fun HabitCard(
    habitState: HabitItemUiState,
    onToggle: () -> Unit,
    onIncrementCounter: (delta: Int) -> Unit,
    onSkip: () -> Unit,
    onDelete: () -> Unit,
    onOpenTimer: () -> Unit,
    dateEditStatus: DateEditStatus = DateEditStatus.EDITABLE_TODAY,
    modifier: Modifier = Modifier
) {
    val habit = habitState.habit
    val isCompleted = habitState.isCompleted
    val streak = habitState.currentStreak
    val currentCount = habitState.currentCount
    val targetCount = habit.targetCount.coerceAtLeast(1)
    val isCounterHabit = habit.scheduleType == "COUNTER" || targetCount > 1
    val canEdit = dateEditStatus == DateEditStatus.EDITABLE_TODAY || dateEditStatus == DateEditStatus.RETROACTIVE_PAST

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (e: Exception) {
        IOSBlue
    }

    val displayColor = if (dateEditStatus == DateEditStatus.SCHEDULED_FUTURE) Color.Gray else parsedColor
    val cardBgColor = MaterialTheme.colorScheme.surface

    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "cardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .padding(vertical = 5.dp)
            .testTag("habit_card_${habit.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (dateEditStatus == DateEditStatus.SCHEDULED_FUTURE) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else cardBgColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (canEdit) 1f else 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Icon Badge + Details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Icon Badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(displayColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForHabit(habit.icon),
                            contentDescription = habit.title,
                            tint = displayColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title & Category & Badges
                    Column {
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (dateEditStatus == DateEditStatus.SCHEDULED_FUTURE) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Category Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = habit.category,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Scheduled Future Badge
                            if (dateEditStatus == DateEditStatus.SCHEDULED_FUTURE) {
                                Surface(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Schedule,
                                            contentDescription = "Scheduled",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "SCHEDULED",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }

                            // Readonly History Badge
                            if (dateEditStatus == DateEditStatus.READONLY_PAST) {
                                Surface(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Lock,
                                            contentDescription = "Locked History",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "LOCKED",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }

                            // Retroactive Past Badge
                            if (dateEditStatus == DateEditStatus.RETROACTIVE_PAST) {
                                Surface(
                                    color = IOSOrange.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "YESTERDAY",
                                        fontSize = 10.sp,
                                        color = IOSOrange,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Streak flame badge
                            if (streak > 0 && dateEditStatus != DateEditStatus.SCHEDULED_FUTURE) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFF9500).copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = Color(0xFFFF9500),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "$streak d",
                                        fontSize = 11.sp,
                                        color = Color(0xFFFF9500),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            // Rest Day / Skipped Badge
                            if (habitState.isSkipped) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF5856D6).copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PauseCircle,
                                        contentDescription = "Rest Day",
                                        tint = Color(0xFF5856D6),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "REST DAY",
                                        fontSize = 11.sp,
                                        color = Color(0xFF5856D6),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // Focus Timer Action Button
                IconButton(
                    onClick = onOpenTimer,
                    enabled = canEdit,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_open_timer_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Focus Timer",
                        tint = if (canEdit) displayColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Skip / Rest Day Action Button
                IconButton(
                    onClick = onSkip,
                    enabled = canEdit,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_skip_habit_${habit.id}")
                ) {
                    Icon(
                        imageVector = if (habitState.isSkipped) Icons.Default.PauseCircle else Icons.Outlined.PauseCircle,
                        contentDescription = "Skip / Rest Day",
                        tint = if (canEdit) (if (habitState.isSkipped) Color(0xFF5856D6) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Delete Action Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Habit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (!isCounterHabit) {
                    Spacer(modifier = Modifier.width(6.dp))

                    // Standard Checkmark Toggle Button
                    val buttonBg by animateColorAsState(
                        targetValue = if (isCompleted) displayColor else Color.Transparent,
                        animationSpec = tween(durationMillis = 200),
                        label = "checkBg"
                    )

                    val checkBorderColor by animateColorAsState(
                        targetValue = if (isCompleted) displayColor else if (canEdit) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        label = "checkBorder"
                    )

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(buttonBg)
                            .border(
                                width = if (isCompleted) 0.dp else 2.dp,
                                color = checkBorderColor,
                                shape = CircleShape
                            )
                            .clickable(enabled = canEdit) { onToggle() }
                            .testTag("check_habit_${habit.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        } else if (!canEdit) {
                            Icon(
                                imageVector = if (dateEditStatus == DateEditStatus.SCHEDULED_FUTURE) Icons.Outlined.Schedule else Icons.Outlined.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // If Counter Habit: Progress Bar and Counter Controls
            if (isCounterHabit) {
                Spacer(modifier = Modifier.height(12.dp))

                val progressRatio = (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isCompleted) IOSGreen else displayColor,
                    trackColor = displayColor.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$currentCount / $targetCount",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isCompleted) IOSGreen else MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                        if (isCompleted) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = IOSGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = IOSGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "DONE",
                                        color = IOSGreen,
                                        fontSize = 10.sp,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    // Stepper Buttons (+ / -)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { onIncrementCounter(-1) },
                            enabled = canEdit && currentCount > 0,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (canEdit && currentCount > 0) MaterialTheme.colorScheme.surfaceVariant
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .testTag("btn_decrement_${habit.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrement",
                                tint = if (canEdit && currentCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { onIncrementCounter(+1) },
                            enabled = canEdit,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (canEdit) displayColor else displayColor.copy(alpha = 0.3f))
                                .testTag("btn_increment_${habit.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increment",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getIconForHabit(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "water" -> Icons.Default.WaterDrop
        "workout" -> Icons.Default.FitnessCenter
        "book" -> Icons.Default.MenuBook
        "meditation" -> Icons.Default.SelfImprovement
        "sleep" -> Icons.Default.Bedtime
        "journal" -> Icons.Default.EditNote
        "apple" -> Icons.Default.Restaurant
        "walk" -> Icons.Default.DirectionsWalk
        "translate" -> Icons.Default.Translate
        "shower" -> Icons.Default.Shower
        else -> Icons.Default.CheckCircle
    }
}
