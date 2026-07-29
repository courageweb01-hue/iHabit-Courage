package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSheet(
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        category: String,
        icon: String,
        colorHex: String,
        frequency: String,
        targetDays: Int,
        scheduleType: String,
        targetDaysList: String,
        targetCount: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Health") }
    var selectedIcon by remember { mutableStateOf("Water") }
    var selectedColor by remember { mutableStateOf("#007AFF") }
    var scheduleType by remember { mutableStateOf("DAILY") } // "DAILY", "WEEKLY_DAYS", "COUNTER"

    // For WEEKLY_DAYS: Set of day numbers (1=Mon, 2=Tue, ..., 7=Sun)
    var selectedDays by remember { mutableStateOf(setOf(1, 2, 3, 4, 5)) }

    // For COUNTER: Numerical goal target
    var targetCount by remember { mutableStateOf(8) }

    val categories = listOf("Health", "Fitness", "Mind", "Productivity", "Nutrition")
    val icons = listOf("Water", "Workout", "Book", "Meditation", "Sleep", "Journal", "Apple", "Walk")
    val colors = listOf("#007AFF", "#34C759", "#FF9500", "#FF3B30", "#AF52DE", "#5856D6", "#30B0C7", "#FF2D55")
    val dayLabels = listOf(
        1 to "M", 2 to "T", 3 to "W", 4 to "T", 5 to "F", 6 to "S", 7 to "S"
    )

    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(scrollState)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Habit",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Habit Title (e.g., Drink 8 Glasses Water)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_habit_title"),
                shape = RoundedCornerShape(14.dp)
            )

            // Habit Type Selector (Daily, Specific Days, Counter)
            Column {
                Text(
                    text = "Habit Type & Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "DAILY" to "Daily",
                        "WEEKLY_DAYS" to "Specific Days",
                        "COUNTER" to "Counter Goal"
                    ).forEach { (typeKey, label) ->
                        FilterChip(
                            selected = scheduleType == typeKey,
                            onClick = { scheduleType = typeKey },
                            label = { Text(label) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FrostedPurplePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Specific Days Picker (if WEEKLY_DAYS)
            if (scheduleType == "WEEKLY_DAYS") {
                Column {
                    Text(
                        text = "Scheduled Days",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dayLabels.forEach { (dayNum, label) ->
                            val isSelected = selectedDays.contains(dayNum)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) FrostedPurplePrimary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        selectedDays = if (isSelected) {
                                            if (selectedDays.size > 1) selectedDays - dayNum else selectedDays
                                        } else {
                                            selectedDays + dayNum
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                }
            }

            // Counter Goal Settings (if COUNTER)
            if (scheduleType == "COUNTER") {
                Column {
                    Text(
                        text = "Daily Target Count",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$targetCount per day",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FrostedPurplePrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { targetCount = (targetCount - 1).coerceAtLeast(1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease target",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = targetCount.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp
                            )

                            IconButton(
                                onClick = { targetCount += 1 },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FrostedPurplePrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase target",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Category Selection
            Column {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FrostedPurplePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Icon Selection
            Column {
                Text(
                    text = "Icon",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(icons) { ic ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedIcon == ic) FrostedPurplePrimary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (selectedIcon == ic) 2.dp else 0.dp,
                                    color = if (selectedIcon == ic) FrostedPurplePrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = ic },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconForHabit(ic),
                                contentDescription = ic,
                                tint = if (selectedIcon == ic) FrostedPurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Color Picker
            Column {
                Text(
                    text = "Theme Color",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(colors) { hex ->
                        val c = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 0.dp,
                                    color = if (selectedColor == hex) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val daysListStr = selectedDays.sorted().joinToString(",")
                        val calculatedTargetDays = if (scheduleType == "WEEKLY_DAYS") selectedDays.size else 7
                        onSave(
                            title,
                            selectedCategory,
                            selectedIcon,
                            selectedColor,
                            "EVERYDAY",
                            calculatedTargetDays,
                            scheduleType,
                            daysListStr,
                            if (scheduleType == "COUNTER") targetCount else 1
                        )
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_save_habit"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FrostedPurplePrimary)
            ) {
                Text(
                    text = "Create Habit",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
