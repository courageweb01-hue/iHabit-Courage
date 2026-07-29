package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CompletionLog
import com.example.ui.CategoryStat
import com.example.ui.DayHeatmap
import com.example.ui.HabitItemUiState
import com.example.ui.theme.*

import androidx.compose.foundation.BorderStroke
import com.example.ui.theme.FrostedPurplePrimary

@Composable
fun StatsScreen(
    habits: List<HabitItemUiState>,
    logs: List<CompletionLog>,
    heatmapData: List<DayHeatmap>,
    categoryStats: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    val totalCompletions = logs.count { it.status == "COMPLETED" }
    val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
    val activeStreaksCount = habits.count { it.currentStreak > 0 }
    val todayCompletionRate = if (habits.isNotEmpty()) {
        habits.count { it.isCompleted }.toFloat() / habits.size.toFloat()
    } else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Analytics & Streaks",
                style = MaterialTheme.typography.labelMedium,
                color = FrostedPurplePrimary,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Stats",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2x2 Key Metric Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Best Streak",
                        value = "$maxStreak Days",
                        icon = Icons.Default.LocalFireDepartment,
                        iconColor = Color(0xFFFF9500),
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Completions",
                        value = totalCompletions.toString(),
                        icon = Icons.Default.CheckCircle,
                        iconColor = IOSGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Active Streaks",
                        value = "$activeStreaksCount Habits",
                        icon = Icons.Default.ElectricBolt,
                        iconColor = FrostedPurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Today Rate",
                        value = "${(todayCompletionRate * 100).toInt()}%",
                        icon = Icons.Default.Star,
                        iconColor = IOSPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 28-Day Calendar Completion Heatmap Matrix
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("heatmap_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Completion Matrix (Past 28 Days)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Consistency intensity map across all active habits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 7 columns x 4 rows matrix
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val chunks = heatmapData.chunked(7)
                        chunks.forEach { rowDays ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowDays.forEach { day ->
                                    val cellColor = when {
                                        day.completionRatio == 0f -> MaterialTheme.colorScheme.surfaceVariant
                                        day.completionRatio < 0.4f -> FrostedPurplePrimary.copy(alpha = 0.35f)
                                        day.completionRatio < 0.75f -> FrostedPurplePrimary.copy(alpha = 0.65f)
                                        else -> FrostedPurplePrimary
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(cellColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day.date.dayOfMonth.toString(),
                                            fontSize = 11.sp,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (day.completionRatio > 0.6f) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Less ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf(0.0f, 0.35f, 0.65f, 1.0f).forEach { ratio ->
                            val c = when {
                                ratio == 0f -> MaterialTheme.colorScheme.surfaceVariant
                                ratio < 0.4f -> FrostedPurplePrimary.copy(alpha = 0.35f)
                                ratio < 0.75f -> FrostedPurplePrimary.copy(alpha = 0.65f)
                                else -> FrostedPurplePrimary
                            }
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(c)
                            )
                        }
                        Text(
                            text = " More",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Category Breakdown Progress Bars
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Category Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val total = categoryStats.sumOf { it.count }.coerceAtLeast(1)

                    categoryStats.forEach { stat ->
                        val parsedColor = try {
                            Color(android.graphics.Color.parseColor(stat.colorHex))
                        } catch (e: Exception) {
                            FrostedPurplePrimary
                        }
                        val pct = stat.count.toFloat() / total.toFloat()

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stat.category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${stat.count} Habits (${(pct * 100).toInt()}%)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { pct },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = parsedColor,
                                trackColor = parsedColor.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}
