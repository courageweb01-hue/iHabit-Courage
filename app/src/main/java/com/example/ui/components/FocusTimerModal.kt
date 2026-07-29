package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Habit
import com.example.ui.theme.FrostedPurplePrimary
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSGreen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerModal(
    habit: Habit,
    onDismiss: () -> Unit,
    onCompleteHabit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var initialDurationMinutes by remember { mutableStateOf(25) } // Default Pomodoro 25 min
    var totalSeconds by remember { mutableStateOf(initialDurationMinutes * 60) }
    var secondsLeft by remember { mutableStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val themeColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (e: Exception) {
        IOSBlue
    }

    // Effect for timer ticking
    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        } else if (isRunning && secondsLeft == 0) {
            isRunning = false
            isFinished = true
            
            // Trigger vibration & haptics
            triggerVibration(context)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            // Auto-complete habit & update streak
            onCompleteHabit()
        }
    }

    // Progress float calculation
    val progressRatio = if (totalSeconds > 0) {
        secondsLeft.toFloat() / totalSeconds.toFloat()
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = tween(durationMillis = 300),
        label = "timerProgress"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row: Habit Name & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Focus Timer",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_timer")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Circular Progress Timer Display
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .testTag("timer_circular_display"),
                contentAlignment = Alignment.Center
            ) {
                // Background track arc
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = themeColor.copy(alpha = 0.15f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Animated active progress arc
                    drawArc(
                        color = if (isFinished) IOSGreen else themeColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Time Text inside Circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isFinished) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Complete",
                            tint = IOSGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "COMPLETE!",
                            style = MaterialTheme.typography.titleMedium,
                            color = IOSGreen,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Habit marked done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val minutes = secondsLeft / 60
                        val seconds = secondsLeft % 60
                        val formattedTime = String.format("%02d:%02d", minutes, seconds)

                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 44.sp
                        )
                        Text(
                            text = if (isRunning) "Focusing..." else "Paused",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Duration Preset Buttons (5m, 15m, 25m, 45m)
            if (!isRunning && !isFinished) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    listOf(5, 15, 25, 45).forEach { mins ->
                        val isSelected = initialDurationMinutes == mins
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) themeColor else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable {
                                    initialDurationMinutes = mins
                                    totalSeconds = mins * 60
                                    secondsLeft = totalSeconds
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${mins}m",
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // Controls: Play/Pause, Reset, Complete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                IconButton(
                    onClick = {
                        isRunning = false
                        isFinished = false
                        totalSeconds = initialDurationMinutes * 60
                        secondsLeft = totalSeconds
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("btn_timer_reset")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Play / Pause Button
                IconButton(
                    onClick = {
                        if (isFinished) {
                            isFinished = false
                            secondsLeft = totalSeconds
                            isRunning = true
                        } else {
                            isRunning = !isRunning
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isFinished) IOSGreen else themeColor)
                        .testTag("btn_timer_toggle")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Quick Complete Button
                IconButton(
                    onClick = {
                        isRunning = false
                        isFinished = true
                        triggerVibration(context)
                        onCompleteHabit()
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(IOSGreen.copy(alpha = 0.2f))
                        .testTag("btn_timer_complete")
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Complete Habit Now",
                        tint = IOSGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun triggerVibration(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            val vibrator = vibratorManager?.defaultVibrator
            vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(500)
            }
        }
    } catch (e: Exception) {
        // Fallback gracefully if vibration not available or permitted
    }
}
