package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActiveJourneyState
import com.example.data.DailyLesson
import com.example.data.JourneyProgram
import com.example.data.JourneysData
import com.example.ui.theme.FrostedPurplePrimary
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSGreen
import com.example.ui.theme.IOSOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLessonModal(
    activeState: ActiveJourneyState,
    program: JourneyProgram,
    onDismiss: () -> Unit,
    onCompleteLesson: (dayNumber: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDay = activeState.currentDay
    val lesson = JourneysData.getDailyLesson(program.id, currentDay)
    val isAlreadyCompleted = activeState.completedDays.contains(currentDay)

    var showMilestonePopup by remember { mutableStateOf<MilestoneType?>(null) }
    val themeColor = try {
        Color(android.graphics.Color.parseColor(program.colorTheme))
    } catch (e: Exception) {
        FrostedPurplePrimary
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
                .fillMaxWidth()
                .wrapContentHeight()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = themeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Day",
                                tint = themeColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DAY $currentDay OF 30",
                                color = themeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_lesson_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Phase Badge
                Text(
                    text = lesson.phaseName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Lesson Title
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Science & Guidance Text Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = themeColor.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, themeColor.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Coaching",
                                tint = themeColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DAILY COACHING & SCIENCE",
                                style = MaterialTheme.typography.labelMedium,
                                color = themeColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lesson.lessonText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Today's Micro-Action Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.5.dp, IOSOrange.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = IOSOrange.copy(alpha = 0.15f),
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Micro-Action",
                                    tint = IOSOrange,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "TODAY'S MICRO-ACTION",
                                style = MaterialTheme.typography.labelMedium,
                                color = IOSOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lesson.microAction,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Complete Action Button
                Button(
                    onClick = {
                        onCompleteLesson(currentDay)
                        when (currentDay) {
                            7 -> showMilestonePopup = MilestoneType.PHASE_2_UNLOCK
                            21 -> showMilestonePopup = MilestoneType.HABIT_FORMED_21
                            30 -> showMilestonePopup = MilestoneType.GRADUATION_30
                            else -> onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlreadyCompleted) IOSGreen else themeColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_complete_todays_guidance")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Complete",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAlreadyCompleted) "Guidance Completed" else "Complete Today's Guidance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Milestone Popup Overlay
            showMilestonePopup?.let { milestone ->
                MilestoneCelebrationDialog(
                    milestone = milestone,
                    programTitle = program.title,
                    onDismiss = {
                        showMilestonePopup = null
                        onDismiss()
                    }
                )
            }
        }
    }
}

enum class MilestoneType {
    PHASE_2_UNLOCK,
    HABIT_FORMED_21,
    GRADUATION_30
}

@Composable
fun MilestoneCelebrationDialog(
    milestone: MilestoneType,
    programTitle: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FrostedPurplePrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue Journey 🎉", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = IOSOrange.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Milestone",
                            tint = IOSOrange,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = when (milestone) {
                        MilestoneType.PHASE_2_UNLOCK -> "Phase 2 Unlocked!"
                        MilestoneType.HABIT_FORMED_21 -> "21-Day Habit Formed!"
                        MilestoneType.GRADUATION_30 -> "30-Day Graduation!"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = when (milestone) {
                    MilestoneType.PHASE_2_UNLOCK -> "Incredible commitment! You completed 7 days of $programTitle. Phase 2: Habit Building is now active!"
                    MilestoneType.HABIT_FORMED_21 -> "Congratulations! Research confirms 21 days of repetition builds lasting neural pathways for $programTitle."
                    MilestoneType.GRADUATION_30 -> "You achieved 30 full days of $programTitle! You have earned your Graduation Trophy Badge and transformed your daily life."
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
