package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActiveJourneyState
import com.example.data.JourneyProgram
import com.example.ui.theme.FrostedPurplePrimary
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSGreen

@Composable
fun ActiveJourneyBanner(
    activeState: ActiveJourneyState,
    program: JourneyProgram,
    onOpenDailyLesson: () -> Unit,
    onPauseJourney: () -> Unit,
    onViewRoadmap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColor = try {
        Color(android.graphics.Color.parseColor(program.colorTheme))
    } catch (e: Exception) {
        FrostedPurplePrimary
    }

    val isCompletedToday = activeState.completedDays.contains(activeState.currentDay)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .testTag("active_journey_banner_${program.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, themeColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            themeColor.copy(alpha = 0.12f),
                            themeColor.copy(alpha = 0.04f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            // Top Badge Row: Journey Category + Phase + Day Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = themeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = activeState.activePhaseName.uppercase(),
                            color = themeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Day ${activeState.currentDay} of 30",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = if (isCompletedToday) IOSGreen.copy(alpha = 0.15f) else themeColor.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${(activeState.progressPercent * 100).toInt()}% Done",
                        color = if (isCompletedToday) IOSGreen else themeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Title & Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(themeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForHabit(program.iconName),
                        contentDescription = program.title,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = program.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = program.tagline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { activeState.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = themeColor,
                trackColor = themeColor.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpenDailyLesson,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompletedToday) IOSGreen else themeColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("btn_todays_guided_lesson")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Lesson",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCompletedToday) "Completed Today" else "Today's Guided Lesson",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onViewRoadmap,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, themeColor.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Text(
                        text = "Roadmap",
                        fontSize = 12.sp,
                        color = themeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onPauseJourney,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PauseCircle,
                        contentDescription = "Pause Journey",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
