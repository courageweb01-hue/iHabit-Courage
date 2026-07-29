package com.example.data

data class ActiveJourneyState(
    val journeyId: String,
    val startDate: String,
    val currentDay: Int = 1,
    val completedDays: Set<Int> = emptySet(),
    val status: String = "active"
) {
    val progressPercent: Float
        get() = (completedDays.size.toFloat() / 30f).coerceIn(0f, 1f)

    val activePhaseName: String
        get() = when {
            currentDay <= 7 -> "Phase 1: Baseline"
            currentDay <= 21 -> "Phase 2: Habit Formed"
            else -> "Phase 3: Graduation"
        }
}
