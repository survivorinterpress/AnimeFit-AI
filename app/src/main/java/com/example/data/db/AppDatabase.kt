package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserStatsEntity::class,
        MealEntity::class,
        QuestEntity::class,
        WeightLogEntity::class,
        AchievementEntity::class,
        CoachMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userStatsDao(): UserStatsDao
    abstract fun mealDao(): MealDao
    abstract fun questDao(): QuestDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun achievementDao(): AchievementDao
    abstract fun coachMessageDao(): CoachMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "animefit_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
