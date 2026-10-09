package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiFoodScannerService
import com.example.data.api.ScannedFoodResult
import com.example.data.db.AchievementEntity
import com.example.data.db.AppDatabase
import com.example.data.db.CoachMessageEntity
import com.example.data.db.MealEntity
import com.example.data.db.QuestEntity
import com.example.data.db.UserStatsEntity
import com.example.data.db.WeightLogEntity
import com.example.data.repository.AnimeFitRepository
import com.example.data.speech.TtsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AnimeFitViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = AnimeFitRepository(
        userStatsDao = database.userStatsDao(),
        mealDao = database.mealDao(),
        questDao = database.questDao(),
        weightLogDao = database.weightLogDao(),
        achievementDao = database.achievementDao(),
        coachMessageDao = database.coachMessageDao()
    )

    private val geminiService = GeminiFoodScannerService()
    val ttsHelper = TtsHelper(application)

    val userStats: StateFlow<UserStatsEntity?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayMeals: StateFlow<List<MealEntity>> = repository.todayMeals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeals: StateFlow<List<MealEntity>> = repository.allMeals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quests: StateFlow<List<QuestEntity>> = repository.allQuests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weightLogs: StateFlow<List<WeightLogEntity>> = repository.weightLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<AchievementEntity>> = repository.achievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCoach = MutableStateFlow("ren")
    val selectedCoach: StateFlow<String> = _selectedCoach.asStateFlow()

    val coachMessages: StateFlow<List<CoachMessageEntity>> = _selectedCoach
        .flatMapLatest { coachId -> repository.getCoachMessages(coachId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isScanningMeal = MutableStateFlow(false)
    val isScanningMeal: StateFlow<Boolean> = _isScanningMeal.asStateFlow()

    private val _scannedResult = MutableStateFlow<ScannedFoodResult?>(null)
    val scannedResult: StateFlow<ScannedFoodResult?> = _scannedResult.asStateFlow()

    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap: StateFlow<Bitmap?> = _scannedBitmap.asStateFlow()

    private val _levelUpEvent = MutableStateFlow<Pair<Int, String>?>(null)
    val levelUpEvent: StateFlow<Pair<Int, String>?> = _levelUpEvent.asStateFlow()

    private val _isCoachThinking = MutableStateFlow(false)
    val isCoachThinking: StateFlow<Boolean> = _isCoachThinking.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = ttsHelper.isSpeaking

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun onScanImage(bitmap: Bitmap) {
        _scannedBitmap.value = bitmap
        _isScanningMeal.value = true
        viewModelScope.launch {
            try {
                val result = geminiService.analyzeFoodImage(bitmap)
                _scannedResult.value = result
            } finally {
                _isScanningMeal.value = false
            }
        }
    }

    fun clearScannedResult() {
        _scannedResult.value = null
        _scannedBitmap.value = null
    }

    fun saveScannedMeal(
        foodName: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        portionSize: String,
        mealType: String,
        notes: String = ""
    ) {
        val result = _scannedResult.value
        val meal = MealEntity(
            name = foodName,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            portionSize = portionSize,
            mealType = mealType,
            dateString = repository.getTodayString(),
            notes = notes,
            xpAwarded = 60,
            healthGrade = result?.healthGrade ?: "A",
            powerTip = result?.animePowerTip ?: "Heroic nutrients replenishing energy reserves!"
        )

        viewModelScope.launch {
            repository.addMeal(meal)
            checkLevelUpFanfare()
            clearScannedResult()
        }
    }

    fun deleteMeal(id: Long) {
        viewModelScope.launch {
            repository.deleteMeal(id)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.logWater(amountMl)
        }
    }

    fun logWorkout(type: String, durationMins: Int, caloriesBurned: Int) {
        viewModelScope.launch {
            repository.logWorkout(type, durationMins, caloriesBurned)
            checkLevelUpFanfare()
        }
    }

    fun logSteps(additional: Int) {
        viewModelScope.launch {
            repository.logSteps(additional)
            checkLevelUpFanfare()
        }
    }

    fun logWeight(weightKg: Float, bodyFat: Float?, note: String = "") {
        viewModelScope.launch {
            repository.logWeight(weightKg, bodyFat, note)
            checkLevelUpFanfare()
        }
    }

    fun updateMeasurements(waist: Float, chest: Float, arms: Float) {
        viewModelScope.launch {
            repository.updateMeasurements(waist, chest, arms)
            checkLevelUpFanfare()
        }
    }

    fun setCoach(coachId: String) {
        _selectedCoach.value = coachId
        viewModelScope.launch {
            repository.updateCoach(coachId)
        }
    }

    fun updateAvatarCustomization(skin: String, aura: String) {
        viewModelScope.launch {
            repository.updateAvatar(skin, aura)
        }
    }

    fun claimQuest(quest: QuestEntity) {
        viewModelScope.launch {
            repository.claimQuest(quest.id, quest.xpReward)
            checkLevelUpFanfare()
        }
    }

    fun sendCoachMessage(userText: String) {
        if (userText.isBlank()) return
        val coachId = _selectedCoach.value

        viewModelScope.launch {
            // Add user message
            repository.addCoachMessage(coachId, "user", userText)

            _isCoachThinking.value = true
            val currentStats = userStats.value
            val contextInfo = "Hero Level: ${currentStats?.level ?: 1}, Rank: ${currentStats?.rankTitle ?: "Rookie Hero"}, Water: ${currentStats?.waterLoggedMl ?: 0}ml, Calories Burned: ${currentStats?.caloriesBurnedToday ?: 0}kcal"

            val reply = geminiService.getCoachResponse(coachId, userText, contextInfo)
            _isCoachThinking.value = false

            repository.addCoachMessage(coachId, "coach", reply)
            // Speak reply automatically with coach personality
            ttsHelper.speak(reply, coachId)
        }
    }

    fun speakText(text: String) {
        ttsHelper.speak(text, _selectedCoach.value)
    }

    fun stopSpeaking() {
        ttsHelper.stop()
    }

    fun dismissLevelUpEvent() {
        _levelUpEvent.value = null
    }

    private suspend fun checkLevelUpFanfare() {
        val stats = userStats.value ?: return
        // If currentXp >= xpForNextLevel, repository.addXp handles it.
        // We can inspect if rank / level changed and trigger fanfare
        val currentLevel = stats.level
        val rank = stats.rankTitle
        // Trigger celebratory event on milestone
        if (currentLevel > 1 && _levelUpEvent.value == null) {
            // Checked if needed
        }
    }

    fun triggerTestLevelUp() {
        viewModelScope.launch {
            val leveled = repository.addXp(300)
            val stats = userStats.value
            if (stats != null) {
                _levelUpEvent.value = Pair(stats.level, stats.rankTitle)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
