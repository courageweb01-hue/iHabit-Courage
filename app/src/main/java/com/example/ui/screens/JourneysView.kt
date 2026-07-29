package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
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
import com.example.data.JourneysData
import com.example.ui.components.JourneyDetailModal
import com.example.ui.components.getIconForHabit
import com.example.ui.theme.FrostedPurplePrimary
import com.example.ui.theme.IOSBlue
import com.example.ui.theme.IOSGreen

@Composable
fun JourneysView(
    activeJourneyIds: Set<String>,
    onStartJourney: (JourneyProgram) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedProgramForModal by remember { mutableStateOf<JourneyProgram?>(null) }

    val categories = listOf("All", "Sleep", "Fitness", "Mindfulness", "Productivity", "Health", "Nutrition", "Mind")

    val filteredPrograms = JourneysData.programs.filter { program ->
        val matchesCategory = selectedCategory == "All" || program.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() ||
                program.title.contains(searchQuery, ignoreCase = true) ||
                program.tagline.contains(searchQuery, ignoreCase = true) ||
                program.category.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top Section Header
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "30-DAY HABIT ARC",
            style = MaterialTheme.typography.labelMedium,
            color = FrostedPurplePrimary,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Journeys & Programs",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Structured 30-day science-backed programs to transform your daily routines",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        // iOS Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search programs, goals or categories...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_journeys"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FrostedPurplePrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 13.sp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FrostedPurplePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Programs Grid List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 120.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredPrograms) { program ->
                val isActive = activeJourneyIds.contains(program.id)
                val cardColor = try {
                    Color(android.graphics.Color.parseColor(program.colorTheme))
                } catch (e: Exception) {
                    IOSBlue
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { selectedProgramForModal = program }
                        .testTag("journey_card_${program.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        width = if (isActive) 2.dp else 1.dp,
                        color = if (isActive) IOSGreen else MaterialTheme.colorScheme.outline
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon Box
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(cardColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconForHabit(program.iconName),
                                contentDescription = program.title,
                                tint = cardColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Info Column
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = cardColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = program.category.uppercase(),
                                        color = cardColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                if (isActive) {
                                    Surface(
                                        color = IOSGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = IOSGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "ACTIVE",
                                                color = IOSGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = program.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = program.tagline,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View Details",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet for selected journey details
    selectedProgramForModal?.let { program ->
        val isActive = activeJourneyIds.contains(program.id)
        JourneyDetailModal(
            program = program,
            isActive = isActive,
            onDismiss = { selectedProgramForModal = null },
            onStartJourney = { p ->
                onStartJourney(p)
                selectedProgramForModal = null
            }
        )
    }
}
