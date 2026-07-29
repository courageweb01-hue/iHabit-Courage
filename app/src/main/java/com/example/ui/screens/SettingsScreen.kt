package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

import androidx.compose.foundation.BorderStroke
import com.example.ui.theme.FrostedPurplePrimary

import com.example.utils.DateEngine

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    cutoffHour: Int = 3,
    onCutoffHourChange: (Int) -> Unit = {},
    onResetData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Habit Data?") },
            text = { Text("This will remove all custom habits and completion history. You can start fresh or re-populate default habits.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = IOSRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                text = "Preferences",
                style = MaterialTheme.typography.labelMedium,
                color = FrostedPurplePrimary,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Appearance & Rollover Hours
        item {
            Text(
                text = "PREFERENCES",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    // Dark Mode Toggle
                    SettingsRow(
                        icon = Icons.Default.DarkMode,
                        iconColor = IOSIndigo,
                        title = "Dark Theme",
                        subtitle = "Apply Frosted Glass dark theme",
                        trailing = {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = onToggleDarkMode,
                                colors = SwitchDefaults.colors(checkedThumbColor = FrostedPurplePrimary)
                            )
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Day Rollover Hour (Night Owl Selector)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IOSOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NightsStay,
                                    contentDescription = "Rollover Hour",
                                    tint = IOSOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Day Rollover Time",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Habits reset at ${DateEngine.getCutoffHourLabel(cutoffHour)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rollover Hour Segment Selector (0, 2, 3, 4 AM)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(0, 2, 3, 4).forEach { hour ->
                                val isSelected = hour == cutoffHour
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onCutoffHourChange(hour) },
                                    label = {
                                        Text(
                                            text = if (hour == 0) "12 AM" else "$hour AM",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = FrostedPurplePrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chip_rollover_$hour")
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Daily Notifications Toggle
                    SettingsRow(
                        icon = Icons.Outlined.Notifications,
                        iconColor = IOSOrange,
                        title = "Daily Reminders",
                        subtitle = "Notification sound & vibration check-ins",
                        trailing = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = FrostedPurplePrimary)
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Data & Storage
        item {
            Text(
                text = "DATA & OFFLINE STORAGE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    // Offline Storage Info
                    SettingsRow(
                        icon = Icons.Outlined.Storage,
                        iconColor = IOSGreen,
                        title = "Zero Server Costs",
                        subtitle = "Offline-first Room DB local engine",
                        trailing = {
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelMedium,
                                color = IOSGreen
                            )
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Reset Data Button
                    SettingsRow(
                        icon = Icons.Outlined.DeleteForever,
                        iconColor = IOSRed,
                        title = "Reset All Data",
                        subtitle = "Clear habits & history back to defaults",
                        onClick = { showResetDialog = true },
                        titleColor = IOSRed
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: About App
        item {
            Text(
                text = "ABOUT",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Outlined.Info,
                        iconColor = IOSTeal,
                        title = "Habit Tracker Open Source",
                        subtitle = "Frosted Glass layout with modern Material 3 design",
                        trailing = {
                            Text(
                                text = "v1.0.0",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
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
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor,
                    fontSize = 15.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }

        if (trailing != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        }
    }
}
