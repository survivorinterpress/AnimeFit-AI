package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "Shadow Striker",
    val level: Int = 1,
    val currentXp: Int = 120,
    val rankTitle: String = "Rookie Hero",
    val streakDays: Int = 3,
    val selectedCoach: String = "ren", // "ren" or "yuki"
    val avatarSkin: String = "Cyber Core", // "Cyber Core", "Neon Striker", "Shinobi Shadow", "Titan Armor"
    val avatarAura: String = "Electric Cyan", // "Electric Cyan", "Magenta Flare", "Golden Saiyan", "Void Violet"
    val dailyCalorieTarget: Int = 2200,
    val dailyProteinTarget: Int = 140,
    val dailyCarbsTarget: Int = 220,
    val dailyFatTarget: Int = 65,
    val waterTargetMl: Int = 2500,
    val waterLoggedMl: Int = 1250,
    val stepsTarget: Int = 8000,
    val stepsLogged: Int = 5420,
    val caloriesBurnedToday: Int = 420,
    val currentWeightKg: Float = 73.5f,
    val heightCm: Float = 176.0f,
    val bodyFatPercent: Float = 16.2f,
    val waistCm: Float = 81.0f,
    val chestCm: Float = 100.5f,
    val armsCm: Float = 36.0f,
    val lastActiveDate: String = ""
)

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val portionSize: String,
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack"
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // "yyyy-MM-dd"
    val notes: String = "",
    val xpAwarded: Int = 50,
    val healthGrade: String = "A",
    val powerTip: String = ""
)

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val target: Int,
    val unit: String,
    val xpReward: Int,
    val category: String, // "WATER", "WORKOUT", "MEALS", "STEPS"
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val dateString: String
)

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val bodyFatPercent: Float?,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String,
    val note: String = ""
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val rankRequired: String,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity(tableName = "coach_messages")
data class CoachMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coachId: String, // "ren" or "yuki"
    val sender: String, // "coach" or "user"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "CHAT" // "CHAT", "MOTIVATION", "WORKOUT", "NUTRITION"
)
