package com.example.data

data class TimelinePhase(
    val name: String,
    val goal: String
)

data class JourneyHabit(
    val title: String,
    val category: String,
    val icon: String,
    val colorHex: String,
    val scheduleType: String = "DAILY",
    val targetCount: Int = 1
)

data class DailyLesson(
    val day: Int,
    val title: String,
    val lessonText: String,
    val microAction: String,
    val phaseName: String
)

data class JourneyProgram(
    val id: String,
    val title: String,
    val tagline: String,
    val category: String,
    val iconName: String,
    val colorTheme: String,
    val essay: String,
    val proTips: List<String>,
    val keyResults: List<String>,
    val moreToExpect: String,
    val phase1: TimelinePhase,
    val phase2: TimelinePhase,
    val phase3: TimelinePhase,
    val defaultReminderTime: String,
    val associatedHabits: List<JourneyHabit>
)

object JourneysData {
    fun getDailyLesson(programId: String, dayNumber: Int): DailyLesson {
        val day = dayNumber.coerceIn(1, 30)
        val phaseName = when {
            day <= 7 -> "Phase 1: Adaptive Assessment"
            day <= 21 -> "Phase 2: Habit Building (21-Day Rule)"
            else -> "Phase 3: Consolidating & Graduation"
        }

        return when (programId) {
            "bedtime_ritual" -> getBedtimeLesson(day, phaseName)
            "meditation_peace" -> getMeditationLesson(day, phaseName)
            "work_productivity" -> getProductivityLesson(day, phaseName)
            "stress_relief" -> getStressLesson(day, phaseName)
            "self_confidence" -> getConfidenceLesson(day, phaseName)
            "fasting_weight_loss" -> getFastingLesson(day, phaseName)
            "less_phone_sugar_free" -> getDetoxLesson(day, phaseName)
            "walk_everyday" -> getWalkLesson(day, phaseName)
            "morning_energy" -> getMorningLesson(day, phaseName)
            "office_fitness" -> getOfficeLesson(day, phaseName)
            else -> DailyLesson(
                day = day,
                title = "Day $day: Daily Milestone",
                lessonText = "Focus on small, consistent steps today. Building sustainable routines requires daily repetition.",
                microAction = "Complete your daily habit checklist and reflect on your progress.",
                phaseName = phaseName
            )
        }
    }

    private fun getBedtimeLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "Audit Evening Screen Usage", "Blue light from smartphone screens suppresses pineal melatonin production, delaying natural sleep signals. Tonight, power down your devices 45 minutes before bedtime.", "Turn off all phone screens by 10:15 PM and place device in another room.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Wind-Down Baseline", "You have established your 7-day baseline screen buffer! Notice how your eyelids feel heavier earlier in the evening.", "Dim bedroom lighting to 20% luminance starting at 9:30 PM.", phase)
        21 -> DailyLesson(21, "21-Day Rule: Sleep Neural Path Formed", "Congratulations! 21 consecutive days of wind-down rituals have rewired your brain's evening neuro-associations.", "Perform 5 minutes of deep belly breathing in bed with lights off.", phase)
        30 -> DailyLesson(30, "Graduation: Rest Master Unlocked", "You have completed the full 30-day Bedtime Ritual Journey! Your sleep architecture is now optimized for deep slow-wave recovery.", "Celebrate your Rest Master badge and maintain your evening sanctuary routine.", phase)
        else -> DailyLesson(day, "Day $day: Evening Sanctuary", "Consistency in bedroom temperature (65°F / 18°C) and light levels signals your hypothalamus to initiate core cooling for deep sleep.", "Keep room cool and read 10 pages of a printed physical book.", phase)
    }

    private fun getMeditationLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "The Breath Anchor", "Mindfulness isn't stopping your thoughts; it is changing your relationship to mental chatter. Sit comfortably and anchor awareness to the physical rise of your breath.", "Set a timer for 5 minutes of unguided nasal breath awareness.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Calm Awareness", "7 days of daily breath practice has begun downregulating amygdala stress reactivity. Mental pauses are becoming natural.", "Extend today's sitting meditation to 8 quiet minutes.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: Habit Formed", "You have reached 21 days of daily meditation! The prefrontal cortex pathways governing self-regulation are now significantly strengthened.", "Complete a 10-minute mindfulness session before starting work.", phase)
        30 -> DailyLesson(30, "Graduation: Zen Master Unlocked", "30 days of consistent mindfulness practice! You have cultivated an enduring inner sanctuary of peace and focus.", "Reflect on your emotional resilience progress and claim your Zen Master badge.", phase)
        else -> DailyLesson(day, "Day $day: Mindful Observation", "When your mind wanders into planning or worry, gently label the thought as 'thinking' and return awareness to your body without judgment.", "Practice 2-minute micro-mindfulness during your lunch break.", phase)
    }

    private fun getProductivityLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "Define Top 3 Non-Negotiables", "Multitasking is a cognitive illusion that fragments focus. Before opening email or Slack, write down the 3 high-impact tasks for today.", "Write your Top 3 priorities on a sticky note and place it beside your monitor.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Focus Foundation", "7 days of structured prioritization! Your brain is learning to protect high-leverage work before administrative chaos kicks in.", "Execute 2 uninterrupted 25-minute Pomodoro focus sprints today.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: Deep Work Mastery", "21 days of protected focus! Attention residue is minimized, allowing you to achieve flow state effortlessly.", "Complete 4 Pomodoro blocks with phone stored in another room.", phase)
        30 -> DailyLesson(30, "Graduation: Focus Master Unlocked", "Full 30-day Productivity Transformation complete! You have doubled your output while lowering daily mental fatigue.", "Claim your Focus Master badge and maintain your daily deep work blocks.", phase)
        else -> DailyLesson(day, "Day $day: Eliminate Friction", "Batch communication checks to designated intervals (e.g., 11 AM and 4 PM) rather than responding to incoming pings continuously.", "Disable non-essential desktop push notifications during deep work.", phase)
    }

    private fun getStressLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "The Physiological Sigh Reset", "Two quick nasal inhales followed by one long vocalized mouth exhale instantly activates the vagus nerve, reducing heart rate in real time.", "Perform 3 consecutive physiological sighs right now.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Nervous System Reset", "7 days of somatic breathing awareness! You are developing real-time stress brake controls.", "Write a 5-minute evening stress-dump journal before bed.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: Resilience Anchor", "21 days of active stress management! Chronic cortisol spikes are replaced by steady emotional equilibrium.", "Take a 10-minute quiet walk outside without headphones during lunch.", phase)
        30 -> DailyLesson(30, "Graduation: Serenity Champion Unlocked", "30-day Stress Relief Journey completed! You are equipped with somatic and cognitive tools for any high-pressure scenario.", "Claim your Serenity badge and celebrate your emotional resilience.", phase)
        else -> DailyLesson(day, "Day $day: Somatic Grounding", "Notice physical tension in your shoulders or jaw right now. Inhale deeply, release the tension on the exhale, and drop your shoulders.", "Execute a 4-7-8 breathing exercise before dinner.", phase)
    }

    private fun getConfidenceLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "Audit Your Internal Dialogue", "Self-doubt grows when micro-wins are ignored. Confidence is built by acknowledging daily efforts and replacing harsh self-talk with constructive support.", "Log 3 micro-wins in your daily journal before going to sleep.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Self-Affirmation Baseline", "7 days of win-tracking! Your subconscious neural pathways are shifting from self-criticism to self-efficacy.", "Practice 2 minutes of broad, upright posture before your next conversation.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: Unshakeable Trust", "21 days of deliberate self-confidence practices! You are standing tall in your self-worth and taking bold action.", "Take 1 small courage step today that pushes you outside your comfort zone.", phase)
        30 -> DailyLesson(30, "Graduation: Confidence Champion Unlocked", "30 days of empowering self-belief completed! You have built an enduring foundation of self-trust.", "Claim your Confidence Champion badge and celebrate your transformation.", phase)
        else -> DailyLesson(day, "Day $day: Courage Micro-Action", "Catch negative self-criticism in real-time today. Pause, reframe the thought neutrally, and remind yourself of past successes.", "Speak up first in your next team meeting or social group.", phase)
    }

    private fun getFastingLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "Hydration & Fasting Window", "Intermittent fasting lowers blood insulin, triggering cellular autophagy and body fat utilization. Keep fasts clean with black coffee, tea, and water.", "Complete a comfortable 14-hour overnight fast starting at 7:00 PM.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Metabolic Adaptation", "7 days of metabolic flexibility! Hunger waves during non-eating hours are diminishing as your body adapts to fat burning.", "Transition to the full 16:8 fasting window today.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: Autophagy Master", "21 days of 16:8 fasting consistency! Blood sugar levels are stable, eliminating afternoon energy crashes.", "Break fast with a high-protein meal of eggs, avocado, or lean protein.", phase)
        30 -> DailyLesson(30, "Graduation: Fasting Master Unlocked", "30 days of Intermittent Fasting completed! You have unlocked optimal metabolic health and sustained vitality.", "Claim your Fasting Master badge and enjoy your vibrant energy.", phase)
        else -> DailyLesson(day, "Day $day: Clean Fasting Focus", "During fasting hours, sip sparkling water with a pinch of sea salt to maintain electrolyte balance and prevent mild headaches.", "Drink at least 4 glasses of water during your morning fasting hours.", phase)
    }

    private fun getDetoxLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "Dopamine Receptor Reset", "Hyper-palatable snacks and social media feeds trigger high dopamine spikes followed by crashes. Detoxing restores natural focus and real-world joy.", "Cap total phone screen time to under 2 hours today.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Brain Fog Lifting", "7 days of digital & sugar discipline! Baseline dopamine levels are stabilizing and mental clarity is returning.", "Eliminate all refined sugar snacks and sugary beverages today.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: Dopamine Freedom", "21 days of clean living! Craving signals are muted, allowing deep presence in reading, conversation, and work.", "Enforce a strict digital curfew starting 1 hour before bedtime.", phase)
        30 -> DailyLesson(30, "Graduation: Dopamine Master Unlocked", "30-Day Less Phone, Sugar-Free Reset complete! You have reclaimed 2+ daily hours and restored physical vitality.", "Claim your Dopamine Master badge and celebrate your reclaimed mental freedom.", phase)
        else -> DailyLesson(day, "Day $day: Healthy Dopamine Swap", "When tempted to scroll or grab sugar, swap it immediately for a 5-minute walk, fresh berries, or a glass of cold water.", "Keep phone out of arm's reach while sitting at your desk.", phase)
    }

    private fun getWalkLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "Post-Meal Movement Magic", "Walking for 10 minutes after meals shuttles glucose into active muscles, suppressing postprandial blood sugar spikes by up to 30%.", "Take a brisk 10-minute stroll immediately after lunch.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Step Baseline Built", "7 days of daily walking! Your joint lubrication and cardiovascular endurance are already responding positively.", "Hit 7,000 steps today with a post-dinner neighborhood walk.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: 10k Step Routine", "21 consecutive days of movement! 10,000 steps per day feels natural, giving you constant calorie burn and mental energy.", "Achieve 10,000 steps today and listen to an inspiring podcast while walking.", phase)
        30 -> DailyLesson(30, "Graduation: Walker Master Unlocked", "30-Day Walking Journey complete! You have integrated low-friction cardiovascular movement into your lifestyle.", "Claim your Walker Master badge and keep stepping forward every day.", phase)
        else -> DailyLesson(day, "Day $day: Cumulative Movement", "Park further away, take the stairs, and make phone calls while walking to comfortably rack up daily steps without extra workout time.", "Take a 5-minute walking break every 90 minutes.", phase)
    }

    private fun getMorningLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "Morning Cortisol Awakening", "Viewing natural outdoor sunlight within 30 minutes of waking triggers a healthy cortisol surge and sets your night melatonin timer.", "Step outside for 10 minutes of direct morning sunlight before looking at your phone.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Circadian Synchronized", "7 days of morning sunlight & hydration! Grogginess is disappearing and nighttime sleep onset is becoming faster.", "Drink 500ml water immediately upon waking today.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: Morning Power Arc", "21 days of morning mastery! Your first hour of the day is a sacred sanctuary of high energy and calm purpose.", "Combine 10 mins sunlight with 5 mins light bodyweight mobility stretching.", phase)
        30 -> DailyLesson(30, "Graduation: Sunrise Champion Unlocked", "30-Day Energy Morning Routine complete! You dominate every morning with physical vitality and sharp focus.", "Claim your Sunrise Champion badge and keep owning your mornings.", phase)
        else -> DailyLesson(day, "Day $day: Hydrate Before Caffeine", "Drink 500ml of clean room-temperature water before your first cup of coffee to replenish overnight fluid loss.", "Delay morning coffee by 60 minutes after waking for maximum caffeine impact.", phase)
    }

    private fun getOfficeLesson(day: Int, phase: String): DailyLesson = when (day) {
        1 -> DailyLesson(1, "Postural Ergonomics Check", "Prolonged sitting shortens hip flexors and pulls shoulders forward. Standing up every 60 minutes restores spinal alignment and cerebral blood flow.", "Set an hourly desk reminder to stand up and stretch for 60 seconds.", phase)
        7 -> DailyLesson(7, "Phase 1 Complete: Agility Baseline", "7 days of office desk resets! Noticeable reduction in neck strain and afternoon energy slumps.", "Perform seated spinal twists and chest openers during your afternoon stretch break.", phase)
        21 -> DailyLesson(21, "21-Day Milestone: Posture Realigned", "21 days of office fitness consistency! Spine stiffness and lower back fatigue are virtually eliminated.", "Perform 3 minutes of desk shoulder rolls and hip flexor stretches twice today.", phase)
        30 -> DailyLesson(30, "Graduation: Ergonomic Master Unlocked", "30-Day Keep Fit at Office Journey complete! You finish every workday feeling agile, energetic, and pain-free.", "Claim your Ergonomic Master badge and maintain your desk movement habits.", phase)
        else -> DailyLesson(day, "Day $day: Desk Mobility Reset", "Stand up during phone calls and squeeze your shoulder blades together 10 times to counteract computer slouching.", "Do 10 standing calf raises while waiting at the office water cooler.", phase)
    }

    val programs: List<JourneyProgram> = listOf(
        JourneyProgram(
            id = "bedtime_ritual",
            title = "Bedtime Ritual",
            tagline = "Optimize sleep hygiene for deep restorative rest",
            category = "Sleep",
            iconName = "Sleep",
            colorTheme = "#5856D6",
            essay = """
                Quality sleep is the fundamental bedrock of human performance, cognitive clarity, and emotional resilience. Modern artificial lighting and smartphone screens emit high-intensity blue light that suppresses pineal gland melatonin production, delaying natural circadian sleep signals by up to two hours.
                
                By establishing a structured 60-minute wind-down ritual before bed, you send clear neural signals to your parasympathetic nervous system that it is safe to downshift. Dimming room ambient light, disconnecting from work communications, and cooling your bedroom temperature creates the ideal physiological environment for restorative slow-wave sleep.
                
                Over 30 days, this program conditions your brain to associate consistent evening cues with rapid sleep onset. You will experience deeper REM cycles, reduced midnight awakenings, and awaken with natural morning vigor without relying on aggressive alarms.
            """.trimIndent(),
            proTips = listOf(
                "Dim ambient overhead lights 1 hour before sleeping and switch off all screen devices.",
                "Keep your bedroom ambient temperature cool (around 65°F / 18°C) to facilitate core body temperature drop.",
                "Avoid consuming caffeine after 2:00 PM and avoid heavy meals within 3 hours of bedtime."
            ),
            keyResults = listOf(
                "Up to 40% increase in deep slow-wave sleep duration",
                "Reduced sleep latency (falling asleep within 15 minutes)",
                "Waking up naturally refreshed without morning brain fog"
            ),
            moreToExpect = "Expect initial resistance during the first 3 days as screen cravings surface. By week two, your evening calm will become an indispensable retreat.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Establish a strict 60-minute screen-free buffer before sleeping"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Anchor consistent sleep and wake times with temperature & light control"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Lock in automatic evening wind-down cues and earn your Rest Master badge"),
            defaultReminderTime = "09:30 PM",
            associatedHabits = listOf(
                JourneyHabit("No Screens 1 Hour Before Bed", "Sleep", "Sleep", "#5856D6"),
                JourneyHabit("Nightly Wind-Down & Dim Lights", "Sleep", "Meditation", "#5856D6"),
                JourneyHabit("Bedtime by 11:00 PM", "Sleep", "Bedtime", "#5856D6")
            )
        ),
        JourneyProgram(
            id = "meditation_peace",
            title = "Meditation for Peace of Mind",
            tagline = "Cultivate calm, reduce anxiety & enhance focus",
            category = "Mindfulness",
            iconName = "Meditation",
            colorTheme = "#AF52DE",
            essay = """
                Neuroscientific research demonstrates that consistent daily mindfulness meditation physically alters brain structure through neuroplasticity. Just ten minutes of daily practice reduces gray-matter density in the amygdala—the brain's fear and stress center—while strengthening prefrontal cortex connectivity.
                
                Mindfulness is not about stopping thoughts, but training your capacity to observe mental chatter without immediate emotional reactivity. By anchoring awareness to the physical sensation of breath, you build cognitive space between external stressors and your internal response.
                
                Through this 30-day guided arc, you will cultivate a grounded emotional sanctuary. Daily stressors will no longer trigger immediate fight-or-flight reactions, unlocking lasting inner peace, focus, and mental clarity.
            """.trimIndent(),
            proTips = listOf(
                "Start with just 5 to 10 minutes at the exact same time each morning.",
                "Anchor your focus on the physical rise and fall of your abdomen or nostrils.",
                "When your mind wanders, gently note 'thinking' and return to the breath without judgment."
            ),
            keyResults = listOf(
                "Noticeable drop in daily baseline anxiety and tension",
                "Enhanced impulse control and emotional regulation",
                "Sharper daily focus and sustained attention span"
            ),
            moreToExpect = "You will transition from reacting automatically to stressors to responding with deliberate calm and composure.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Complete 5 minutes of focused breath awareness each morning"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Expand to 10–15 minutes and integrate mid-day mindful breath resets"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Master effortless daily mindfulness and earn your Zen Master badge"),
            defaultReminderTime = "08:00 AM",
            associatedHabits = listOf(
                JourneyHabit("10 Min Morning Meditation", "Mindfulness", "Meditation", "#AF52DE"),
                JourneyHabit("Mindful Breathing Reset", "Mindfulness", "SelfImprovement", "#AF52DE")
            )
        ),
        JourneyProgram(
            id = "work_productivity",
            title = "Increase Productivity at Work",
            tagline = "Master deep focus and eliminate daily distractions",
            category = "Productivity",
            iconName = "Book",
            colorTheme = "#FF9500",
            essay = """
                High cognitive output is not determined by working longer hours, but by protecting uninterrupted periods of deep work. Constantly switching between tasks, emails, and notifications creates severe attention residue, degrading mental throughput by up to 40%.
                
                Structuring work into dedicated 25-minute Pomodoro sprints allows your brain to operate at peak focus without cognitive exhaustion. Pair this with clear daily priority setting to ensure high-leverage tasks are completed before low-value administrative work takes over.
                
                This 30-day program reprograms your professional workflow. By systematically eliminating friction and batching distractions, you will accomplish more meaningful output in fewer hours with significantly less mental fatigue.
            """.trimIndent(),
            proTips = listOf(
                "Identify and write down your top 3 non-negotiable priorities every morning before opening email.",
                "Use 25-minute timed Pomodoro blocks with phone placed in Do Not Disturb mode.",
                "Batch communication checks to designated times (e.g., 11 AM and 4 PM) rather than constant checking."
            ),
            keyResults = listOf(
                "2x increase in high-priority task completion rate",
                "Substantial reduction in workday mental fatigue",
                "Clear separation between productive work and personal recharge time"
            ),
            moreToExpect = "You will feel in complete command of your workday schedule instead of constantly reacting to notifications.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Track daily tasks and execute 2 focused Pomodoro blocks"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Execute 4 Pomodoro sprints daily and batch all email/chat checks"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Sustain peak deep-work routine and earn your Focus Master badge"),
            defaultReminderTime = "09:00 AM",
            associatedHabits = listOf(
                JourneyHabit("Plan Top 3 Priorities Daily", "Productivity", "EditNote", "#FF9500"),
                JourneyHabit("25 Min Pomodoro Work Block", "Productivity", "MenuBook", "#FF9500", "COUNTER", 2),
                JourneyHabit("Zero Inbox Check Before 10 AM", "Productivity", "CheckCircle", "#FF9500")
            )
        ),
        JourneyProgram(
            id = "stress_relief",
            title = "Relief Stress in Tough Situations",
            tagline = "Build emotional resilience under high pressure",
            category = "Mindfulness",
            iconName = "Journal",
            colorTheme = "#30B0C7",
            essay = """
                Stress is a natural biological mechanism designed to mobilize energy for acute challenges. However, chronic workplace and personal stressors keep blood cortisol and adrenaline chronically elevated, resulting in exhaustion, impaired decision-making, and emotional burnout.
                
                Leveraging physiological physiological sighing—two quick nasal inhales followed by a long vocalized exhale—instantly stimulates the vagus nerve, reducing heart rate within seconds. Combining immediate somatic resets with evening reflective journaling helps process subconscious tension before sleep.
                
                Over 30 days, you will rewire your nervous system's response to friction. When high-pressure demands arise, you will maintain calm composure, clear judgment, and steady physical energy.
            """.trimIndent(),
            proTips = listOf(
                "Perform 3 consecutive physiological sighs (double inhale, long exhale) whenever feeling overwhelmed.",
                "Spend 5 minutes every evening writing a 'stress dump' journal to externalize worries.",
                "Take brief 2-minute physical walk breaks every 90 minutes to discharge physical tension."
            ),
            keyResults = listOf(
                "Rapid physical recovery during high-stress triggers",
                "Reduced evening overthinking and improved sleep transition",
                "Greater emotional steadiness in personal and work conversations"
            ),
            moreToExpect = "Challenging scenarios will feel like manageable puzzles rather than overwhelming threats.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Practice 4-7-8 breathing reset twice daily"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Integrate evening stress-dump journaling and physical walk resets"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Solidify stress resilience habits and earn your Serenity badge"),
            defaultReminderTime = "07:30 PM",
            associatedHabits = listOf(
                JourneyHabit("Box Breathing Exercise", "Mindfulness", "SelfImprovement", "#30B0C7"),
                JourneyHabit("Evening Stress Dump Journal", "Mindfulness", "EditNote", "#30B0C7")
            )
        ),
        JourneyProgram(
            id = "self_confidence",
            title = "Self-Confidence Builder",
            tagline = "Transform self-talk & step into your true potential",
            category = "Mind",
            iconName = "Translate",
            colorTheme = "#FF2D55",
            essay = """
                Self-confidence is not an innate genetic trait; it is a learned habit reinforced by self-validating actions and intentional internal dialogue. Imposter syndrome and self-doubt thrive when we focus exclusively on micro-failures while ignoring cumulative daily progress.
                
                Neuro-Linguistic studies show that consciously tracking daily micro-wins re-programs subconscious neural pathways toward self-efficacy. Combining daily win-logging with upright physical posture sends immediate neurochemical signals of confidence to the brain.
                
                This 30-day transformation builds an unshakeable inner foundation. You will replace self-limiting beliefs with grounded self-trust, empowering you to embrace new challenges with courage.
            """.trimIndent(),
            proTips = listOf(
                "Log at least 3 distinct daily wins every evening, no matter how small.",
                "Practice upright, broad physical posture for 2 minutes before key meetings or social events.",
                "Catch negative self-criticism in real-time and reframe it with neutral, constructive feedback."
            ),
            keyResults = listOf(
                "Measurable increase in self-worth and self-advocacy",
                "Dramatically reduced fear of failure and public speaking hesitation",
                "Greater resilience when receiving constructive feedback"
            ),
            moreToExpect = "You will stop waiting for external validation and begin trusting your internal judgment.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Record 3 daily wins and practice posture alignment"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Actively reframe inner critic thoughts and take 1 daily courage step"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Embody bold self-trust and earn your Confidence Champion badge"),
            defaultReminderTime = "08:30 AM",
            associatedHabits = listOf(
                JourneyHabit("Daily Affirmation & Posture Check", "Mind", "Translate", "#FF2D55"),
                JourneyHabit("Log 3 Wins Before Sleep", "Mind", "EditNote", "#FF2D55")
            )
        ),
        JourneyProgram(
            id = "fasting_weight_loss",
            title = "Fasting to Lose Weight Easily",
            tagline = "Unlock metabolic flexibility with intermittent fasting",
            category = "Nutrition",
            iconName = "Apple",
            colorTheme = "#FF3B30",
            essay = """
                Intermittent fasting is one of the most scientifically validated protocols for metabolic health and weight management. By extending the overnight non-eating window to 16 hours, your body shifts from burning dietary glucose to utilizing stored body fat for cellular fuel.
                
                Fasting triggers autophagy—a cellular cleanup process where old, damaged cell components are broken down and recycled. Furthermore, fasting stabilizes blood insulin levels, eliminating the dramatic energy crashes and intense sugar cravings triggered by frequent snacking.
                
                Over 30 days, your metabolism will adapt smoothly to a 16:8 fasting window. You will enjoy steady physical energy, effortless weight management, and liberated mental focus.
            """.trimIndent(),
            proTips = listOf(
                "Maintain strict hydration with water, black coffee, or unflavored tea during fasts.",
                "Break your fast with high-protein and nutrient-dense whole foods to prevent glucose spikes.",
                "Start with a comfortable 14-hour fast for the first 3 days before advancing to 16 hours."
            ),
            keyResults = listOf(
                "Consistent reduction in visceral body fat",
                "Elimination of mid-afternoon energy slumps and hunger spikes",
                "Improved insulin sensitivity and cellular autophagy"
            ),
            moreToExpect = "Initial hunger waves during fasting hours will subside completely as your body shifts to fat adaptation.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Ease into a 14:10 fasting schedule with 8 glasses of water"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Progress to full 16:8 fasting protocol and high-protein breaking meal"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Lock in metabolic flexibility and earn your Fasting Master badge"),
            defaultReminderTime = "07:00 PM",
            associatedHabits = listOf(
                JourneyHabit("16:8 Intermittent Fasting Window", "Nutrition", "Restaurant", "#FF3B30"),
                JourneyHabit("Hydrate 8 Glasses Water", "Nutrition", "WaterDrop", "#FF3B30", "COUNTER", 8)
            )
        ),
        JourneyProgram(
            id = "less_phone_sugar_free",
            title = "Less Phone, More Sugar-Free Days",
            tagline = "Break dopamine addiction and reclaim your time",
            category = "Health",
            iconName = "Water",
            colorTheme = "#007AFF",
            essay = """
                Hyper-palatable refined sugars and social media algorithms exploit identical dopamine pathways in the human brain. Continuous exposure to short-form video feeds and sugary snacks creates high dopamine spikes followed by immediate crashes, producing chronic fatigue and brain fog.
                
                Detoxing from digital overstimulation and refined sugar resets baseline dopamine receptor sensitivity. When dopamine levels normalize, everyday activities like reading, deep conversation, and real-world movement become deeply rewarding once again.
                
                This 30-day digital and dietary reset restores your mental bandwidth. You will reclaim 2+ hours of daily screen time and enjoy steady, sustained physical vitality.
            """.trimIndent(),
            proTips = listOf(
                "Place your smartphone in another room or out of sight while working or relaxing.",
                "Replace sugary snacks with fresh whole berries, nuts, or sparkling water.",
                "Set strict app daily time limits and turn off non-essential push notifications."
            ),
            keyResults = listOf(
                "Reclaimed 2+ hours of productive daily screen time",
                "Clear skin, reduced body inflammation, and zero sugar crashes",
                "Deeper mental presence and enhanced real-world relationships"
            ),
            moreToExpect = "After a 4-day dopamine detox phase, your brain fog will clear, yielding exceptional focus.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Cap phone screen time to 2h and eliminate sugary sodas"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Implement complete zero-refined-sugar protocol and digital curfew"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Sustain balanced lifestyle habits and earn your Dopamine Detox badge"),
            defaultReminderTime = "01:00 PM",
            associatedHabits = listOf(
                JourneyHabit("Under 2 Hours Screen Time", "Health", "WaterDrop", "#007AFF"),
                JourneyHabit("Zero Refined Sugar Snacks", "Health", "Restaurant", "#007AFF")
            )
        ),
        JourneyProgram(
            id = "walk_everyday",
            title = "Walk Every Day for Health",
            tagline = "Achieve 10,000 daily steps for cardiovascular vitality",
            category = "Fitness",
            iconName = "Walk",
            colorTheme = "#34C759",
            essay = """
                Human physiology evolved for low-intensity continuous daily movement, yet modern sedentary lifestyle habits keep most adults seated for over 9 hours daily. Walking 10,000 steps per day stimulates lymphatic circulation, joint lubrication, and cardiovascular resilience without causing athletic strain.
                
                Post-meal walking is particularly potent: a brief 10-minute stroll after eating lowers postprandial blood glucose spikes by up to 30%, shuttling glucose directly into active muscle tissue without requiring heavy insulin secretion.
                
                Through this 30-day walking journey, you will integrate low-friction movement into your daily life. You will build cardiovascular stamina, burn excess calories naturally, and elevate your daily mood.
            """.trimIndent(),
            proTips = listOf(
                "Take a 10-minute brisk walk immediately after lunch and dinner.",
                "Take stairs instead of elevators and park further away from building entrances.",
                "Listen to educational podcasts or audiobooks during longer evening walks."
            ),
            keyResults = listOf(
                "Enhanced cardiovascular endurance and lower resting heart rate",
                "Improved post-meal digestion and blood sugar control",
                "Natural daily calorie burn without joint strain"
            ),
            moreToExpect = "Daily walking will quickly evolve from a planned exercise into your favorite way to unwind.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Target 6,000 steps daily with 1 post-meal walk"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Scale up to 10,000 daily steps and 2 post-meal walks"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Maintain 10k daily step routine and earn your Walker Master badge"),
            defaultReminderTime = "05:30 PM",
            associatedHabits = listOf(
                JourneyHabit("10,000 Daily Steps", "Fitness", "DirectionsWalk", "#34C759", "COUNTER", 10),
                JourneyHabit("Post-Meal 10 Min Walk", "Fitness", "DirectionsWalk", "#34C759")
            )
        ),
        JourneyProgram(
            id = "morning_energy",
            title = "Energy-Boosting Morning Routine",
            tagline = "Kickstart your day with sunlight, movement & water",
            category = "Fitness",
            iconName = "Workout",
            colorTheme = "#FFCC00",
            essay = """
                How you spend the first 60 minutes of your morning sets your neurological baseline for the entire day. Reaching for your phone immediately upon waking floods your brain with stress hormones and reactive impulses, trapping you in a passive state.
                
                Viewing natural sunlight within 30 minutes of waking triggers a healthy cortisol awakening response, resetting your internal circadian clock and setting a automatic timer for nighttime melatonin release 16 hours later. Pairing sunlight with immediate 500ml hydration rehydrates organs after overnight fluid loss.
                
                This 30-day program establishes a powerhouse morning ritual. You will eliminate morning grogginess, ignite metabolic rate, and dominate your daily goals with high energy.
            """.trimIndent(),
            proTips = listOf(
                "Drink a full glass of water (500ml) immediately upon stepping out of bed.",
                "Get 10 minutes of direct outdoor sunlight before looking at digital screens.",
                "Perform 5 minutes of light mobility stretching or pushups to activate circulation."
            ),
            keyResults = listOf(
                "Immediate elimination of morning brain fog and grogginess",
                "Sustained physical energy throughout the entire morning",
                "Significantly improved sleep quality at night due to circadian alignment"
            ),
            moreToExpect = "You will look forward to waking up early, transforming mornings into your most empowering hour.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Drink 500ml water and get 5 mins outdoor sunlight upon waking"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Add 10 mins light morning movement and zero screen time first 30 mins"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Master complete morning power routine and earn your Sunrise Champion badge"),
            defaultReminderTime = "06:45 AM",
            associatedHabits = listOf(
                JourneyHabit("500ml Water Upon Waking", "Fitness", "WaterDrop", "#FFCC00"),
                JourneyHabit("10 Min Morning Sunlight & Stretch", "Fitness", "FitnessCenter", "#FFCC00")
            )
        ),
        JourneyProgram(
            id = "office_fitness",
            title = "Keep Fit at Office",
            tagline = "Desk-friendly mobility and posture resets",
            category = "Fitness",
            iconName = "Shower",
            colorTheme = "#5AC8FA",
            essay = """
                Prolonged workplace sitting causes severe muscular imbalances, including tight hip flexors, weakened gluteal muscles, and forward-head posture. Over time, this ergonomic strain leads to chronic lower back discomfort and tension headaches.
                
                Micro-movement breaks taken every 45 to 60 minutes break up static muscular tension, boost cerebral blood flow, and enhance cognitive performance. Simple desk mobility drills keep your joints lubricated and posture aligned throughout the workday.
                
                Over 30 days, you will eliminate posture fatigue and stay energized at your desk. You will finish workdays with vibrant energy rather than stiffness and exhaustion.
            """.trimIndent(),
            proTips = listOf(
                "Set a subtle silent timer to stand up and stretch every 60 minutes.",
                "Perform neck rolls, chest openers, and seated spinal twists right at your desk.",
                "Use a refillable water bottle to encourage frequent trips to the water cooler."
            ),
            keyResults = listOf(
                "Complete relief from office neck, shoulder, and back tightness",
                "Higher afternoon mental focus and stamina",
                "Improved overall posture and spinal alignment"
            ),
            moreToExpect = "You will end your workdays feeling physically agile rather than stiff and exhausted.",
            phase1 = TimelinePhase("Days 1–7: Adaptive Assessment", "Take 2 hourly standing stretch breaks during office hours"),
            phase2 = TimelinePhase("Days 8–21: Habit Building (The 21-Day Rule)", "Perform desk mobility routine every 60 mins and stay hydrated"),
            phase3 = TimelinePhase("Days 22–30: Consolidating & Graduation", "Embed seamless office agility habits and earn your Ergonomic Master badge"),
            defaultReminderTime = "02:00 PM",
            associatedHabits = listOf(
                JourneyHabit("Hourly Desk Stretch Break", "Fitness", "Shower", "#5AC8FA"),
                JourneyHabit("Stand Up & Move 5 Mins", "Fitness", "DirectionsWalk", "#5AC8FA")
            )
        )
    )
}
