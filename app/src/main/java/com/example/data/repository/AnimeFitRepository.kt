package com.example.data.repository

import com.example.data.db.AchievementDao
import com.example.data.db.AchievementEntity
import com.example.data.db.CoachMessageDao
import com.example.data.db.CoachMessageEntity
import com.example.data.db.MealDao
import com.example.data.db.MealEntity
import com.example.data.db.QuestDao
import com.example.data.db.QuestEntity
import com.example.data.db.UserStatsDao
import com.example.data.db.UserStatsEntity
import com.example.data.db.WeightLogDao
import com.example.data.db.WeightLogEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnimeFitRepository(
    private val userStatsDao: UserStatsDao,
    private val mealDao: MealDao,
    private val questDao: QuestDao,
    private val weightLogDao: WeightLogDao,
    private val achievementDao: AchievementDao,
    private val coachMessageDao: CoachMessageDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    fun getTodayString(): String = dateFormat.format(Date())

    val userStats: Flow<UserStatsEntity?> = userStatsDao.getUserStats()
    val allMeals: Flow<List<MealEntity>> = mealDao.getAllMeals()
    val todayMeals: Flow<List<MealEntity>> = mealDao.getMealsForDate(getTodayString())
    val allQuests: Flow<List<QuestEntity>> = questDao.getAllQuests()
    val weightLogs: Flow<List<WeightLogEntity>> = weightLogDao.getAllWeightLogs()
    val achievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    fun getCoachMessages(coachId: String): Flow<List<CoachMessageEntity>> =
        coachMessageDao.getMessagesForCoach(coachId)

    suspend fun initializeDefaultDataIfEmpty() {
        var stats = userStatsDao.getUserStatsOnce()
        val today = getTodayString()

        if (stats == null) {
            stats = UserStatsEntity(
                id = 1,
                playerName = "Ren Striker",
                level = 1,
                currentXp = 120,
                rankTitle = "Rookie Hero",
                streakDays = 3,
                selectedCoach = "ren",
                avatarSkin = "Cyber Core",
                avatarAura = "Electric Cyan",
                dailyCalorieTarget = 2200,
                dailyProteinTarget = 140,
                dailyCarbsTarget = 220,
                dailyFatTarget = 65,
                waterTargetMl = 2500,
                waterLoggedMl = 1250,
                stepsTarget = 8000,
                stepsLogged = 4250,
                caloriesBurnedToday = 380,
                currentWeightKg = 73.5f,
                heightCm = 176.0f,
                bodyFatPercent = 16.2f,
                waistCm = 81.0f,
                chestCm = 100.5f,
                armsCm = 36.0f,
                lastActiveDate = today
            )
            userStatsDao.insertOrUpdate(stats)

            // Initial Starter Quests
            val initialQuests = listOf(
                QuestEntity(
                    title = "Scan the Hero's Fuel",
                    description = "Use AI Camera to scan at least 2 meals today",
                    currentProgress = 1,
                    target = 2,
                    unit = "meals",
                    xpReward = 80,
                    category = "MEALS",
                    isCompleted = false,
                    isClaimed = false,
                    dateString = today
                ),
                QuestEntity(
                    title = "Elixir of Hydration",
                    description = "Drink at least 2,000ml of fresh water",
                    currentProgress = 1250,
                    target = 2000,
                    unit = "ml",
                    xpReward = 60,
                    category = "WATER",
                    isCompleted = false,
                    isClaimed = false,
                    dateString = today
                ),
                QuestEntity(
                    title = "Iron Dungeon Training",
                    description = "Log at least 30 minutes of physical workout",
                    currentProgress = 20,
                    target = 30,
                    unit = "mins",
                    xpReward = 120,
                    category = "WORKOUT",
                    isCompleted = false,
                    isClaimed = false,
                    dateString = today
                ),
                QuestEntity(
                    title = "Ninja Stride Mission",
                    description = "Reach 6,000 steps today to bolster endurance",
                    currentProgress = 4250,
                    target = 6000,
                    unit = "steps",
                    xpReward = 90,
                    category = "STEPS",
                    isCompleted = false,
                    isClaimed = false,
                    dateString = today
                )
            )
            questDao.insertQuests(initialQuests)

            // Initial Starter Sample Meals
            val sampleMeals = listOf(
                MealEntity(
                    name = "Protein Oats & Blueberries",
                    calories = 420,
                    protein = 30.0f,
                    carbs = 58.0f,
                    fat = 8.0f,
                    portionSize = "1 Bowl (320g)",
                    mealType = "Breakfast",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
                    dateString = today,
                    notes = "Complex carbohydrates loaded with antioxidants",
                    xpAwarded = 60,
                    healthGrade = "S",
                    powerTip = "Clean fuel for early dungeon raids."
                ),
                MealEntity(
                    name = "Grilled Chicken Teriyaki & Rice",
                    calories = 580,
                    protein = 46.0f,
                    carbs = 66.0f,
                    fat = 14.0f,
                    portionSize = "1 Bento (400g)",
                    mealType = "Lunch",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                    dateString = today,
                    notes = "High protein bento scan",
                    xpAwarded = 70,
                    healthGrade = "S",
                    powerTip = "Muscle recovery rate boosted by 25%."
                )
            )
            for (m in sampleMeals) {
                mealDao.insertMeal(m)
            }

            // Initial Achievements
            val starterAchievements = listOf(
                AchievementEntity(
                    id = "ach_first_scan",
                    title = "AI Eye Awakened",
                    description = "First time scanning a meal with the AI camera",
                    iconEmoji = "👁️",
                    rankRequired = "Rookie Hero",
                    xpReward = 100,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                ),
                AchievementEntity(
                    id = "ach_water_100",
                    title = "Aqua Alchemist",
                    description = "Hit your daily water target 3 days in a row",
                    iconEmoji = "💧",
                    rankRequired = "Rookie Hero",
                    xpReward = 150,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                ),
                AchievementEntity(
                    id = "ach_level_5",
                    title = "Limit Breaker I",
                    description = "Reach Level 5 and attain Fitness Warrior rank",
                    iconEmoji = "⚡",
                    rankRequired = "Fitness Warrior",
                    xpReward = 250,
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "ach_macro_master",
                    title = "Macro Grandmaster",
                    description = "Hit within 5% of your daily protein target",
                    iconEmoji = "🥩",
                    rankRequired = "Nutrition Master",
                    xpReward = 300,
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "ach_streak_7",
                    title = "Iron Will Streak",
                    description = "Maintain a 7-day uninterrupted login & log streak",
                    iconEmoji = "🔥",
                    rankRequired = "Elite Champion",
                    xpReward = 500,
                    isUnlocked = false
                ),
                AchievementEntity(
                    id = "ach_legendary",
                    title = "God of Aesthetics",
                    description = "Reach Level 25 and awaken the Legendary Avatar form",
                    iconEmoji = "👑",
                    rankRequired = "Legendary Athlete",
                    xpReward = 1000,
                    isUnlocked = false
                )
            )
            achievementDao.insertAchievements(starterAchievements)

            // Starter Weight Log
            weightLogDao.insertWeightLog(
                WeightLogEntity(
                    weightKg = 74.2f,
                    bodyFatPercent = 16.8f,
                    timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7,
                    dateString = dateFormat.format(Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7)),
                    note = "Initial hero scan"
                )
            )
            weightLogDao.insertWeightLog(
                WeightLogEntity(
                    weightKg = 73.8f,
                    bodyFatPercent = 16.5f,
                    timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 3,
                    dateString = dateFormat.format(Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 3)),
                    note = "Dungeon training week 1"
                )
            )
            weightLogDao.insertWeightLog(
                WeightLogEntity(
                    weightKg = 73.5f,
                    bodyFatPercent = 16.2f,
                    timestamp = System.currentTimeMillis(),
                    dateString = today,
                    note = "Current lean mass index"
                )
            )

            // Starter Coach Welcome Messages
            coachMessageDao.insertMessage(
                CoachMessageEntity(
                    coachId = "ren",
                    sender = "coach",
                    message = "OSS! Welcome to AnimeFit, warrior! I'm Ren! Together we'll break every limit! Scan your meals, crush your quests, and unlock your true form!",
                    category = "MOTIVATION"
                )
            )
            coachMessageDao.insertMessage(
                CoachMessageEntity(
                    coachId = "yuki",
                    sender = "coach",
                    message = "System initialized. Greetings, Agent. I am Yuki. My telemetry is tuned to your biometric efficiency. Adhere to the macro splits and victory is mathematically guaranteed.",
                    category = "MOTIVATION"
                )
            )
        }
    }

    suspend fun addMeal(meal: MealEntity): Long {
        val id = mealDao.insertMeal(meal)
        // Award XP for logging a meal
        addXp(meal.xpAwarded)
        // Update quest progress if applicable
        updateMealQuest()
        return id
    }

    suspend fun deleteMeal(id: Long) {
        mealDao.deleteMeal(id)
    }

    suspend fun logWater(amountMl: Int) {
        val current = userStatsDao.getUserStatsOnce() ?: return
        val newAmount = (current.waterLoggedMl + amountMl).coerceAtLeast(0)
        val updated = current.copy(waterLoggedMl = newAmount)
        userStatsDao.insertOrUpdate(updated)

        // Check water quest
        updateWaterQuest(newAmount)
    }

    suspend fun logWorkout(type: String, durationMinutes: Int, caloriesBurned: Int) {
        val current = userStatsDao.getUserStatsOnce() ?: return
        val newBurned = current.caloriesBurnedToday + caloriesBurned
        val xpGain = durationMinutes * 4
        userStatsDao.insertOrUpdate(current.copy(caloriesBurnedToday = newBurned))
        addXp(xpGain)
        updateWorkoutQuest(durationMinutes)
    }

    suspend fun logSteps(additionalSteps: Int) {
        val current = userStatsDao.getUserStatsOnce() ?: return
        val newSteps = current.stepsLogged + additionalSteps
        userStatsDao.insertOrUpdate(current.copy(stepsLogged = newSteps))
        updateStepsQuest(newSteps)
    }

    suspend fun logWeight(weightKg: Float, bodyFat: Float?, note: String) {
        weightLogDao.insertWeightLog(
            WeightLogEntity(
                weightKg = weightKg,
                bodyFatPercent = bodyFat,
                dateString = getTodayString(),
                note = note
            )
        )
        val current = userStatsDao.getUserStatsOnce() ?: return
        userStatsDao.insertOrUpdate(
            current.copy(
                currentWeightKg = weightKg,
                bodyFatPercent = bodyFat ?: current.bodyFatPercent
            )
        )
        addXp(75) // XP for body tracking
    }

    suspend fun updateMeasurements(waist: Float, chest: Float, arms: Float) {
        val current = userStatsDao.getUserStatsOnce() ?: return
        userStatsDao.insertOrUpdate(
            current.copy(
                waistCm = waist,
                chestCm = chest,
                armsCm = arms
            )
        )
        addXp(50)
    }

    suspend fun updateCoach(coachId: String) {
        val current = userStatsDao.getUserStatsOnce() ?: return
        userStatsDao.insertOrUpdate(current.copy(selectedCoach = coachId))
    }

    suspend fun updateAvatar(skin: String, aura: String) {
        val current = userStatsDao.getUserStatsOnce() ?: return
        userStatsDao.insertOrUpdate(current.copy(avatarSkin = skin, avatarAura = aura))
    }

    suspend fun addCoachMessage(coachId: String, sender: String, message: String, category: String = "CHAT") {
        coachMessageDao.insertMessage(
            CoachMessageEntity(
                coachId = coachId,
                sender = sender,
                message = message,
                category = category
            )
        )
    }

    suspend fun claimQuest(questId: Long, xpReward: Int) {
        questDao.claimQuest(questId)
        addXp(xpReward)
    }

    suspend fun addXp(amount: Int): Boolean {
        val current = userStatsDao.getUserStatsOnce() ?: return false
        var xp = current.currentXp + amount
        var level = current.level
        var leveledUp = false

        while (true) {
            val xpNeeded = xpForNextLevel(level)
            if (xp >= xpNeeded) {
                xp -= xpNeeded
                level += 1
                leveledUp = true
            } else {
                break
            }
        }

        val rank = calculateRankTitle(level)
        userStatsDao.insertOrUpdate(
            current.copy(
                level = level,
                currentXp = xp,
                rankTitle = rank
            )
        )
        return leveledUp
    }

    fun xpForNextLevel(level: Int): Int {
        return level * 250 + 200
    }

    fun calculateRankTitle(level: Int): String {
        return when {
            level < 5 -> "Rookie Hero"
            level < 10 -> "Fitness Warrior"
            level < 15 -> "Nutrition Master"
            level < 25 -> "Elite Champion"
            else -> "Legendary Athlete"
        }
    }

    private suspend fun updateWaterQuest(currentWaterMl: Int) {
        // Find water quest
        // If currentWaterMl >= 2000, mark complete
    }

    private suspend fun updateMealQuest() {
        // increment meal quest progress
    }

    private suspend fun updateWorkoutQuest(minutes: Int) {
        // increment workout quest
    }

    private suspend fun updateStepsQuest(steps: Int) {
        // increment steps quest
    }
}
