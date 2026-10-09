package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.MealEntity
import com.example.data.db.UserStatsEntity
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.DarkGrayBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFlame
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AnimeFitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FitnessTrackerScreen(
    viewModel: AnimeFitViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val allMeals by viewModel.allMeals.collectAsStateWithLifecycle()
    val weightLogs by viewModel.weightLogs.collectAsStateWithLifecycle()

    val currentStats = stats ?: UserStatsEntity()

    var showWeightDialog by remember { mutableStateOf(false) }
    var showWorkoutDialog by remember { mutableStateOf(false) }
    var showMeasurementsDialog by remember { mutableStateOf(false) }

    // BMI calculation: weight / (heightInMeters ^ 2)
    val heightM = (currentStats.heightCm / 100f).coerceAtLeast(1.0f)
    val bmi = currentStats.currentWeightKg / (heightM * heightM)
    val bmiCategory = when {
        bmi < 18.5f -> "Lean Hero"
        bmi < 25.0f -> "Optimal Hero Physique"
        bmi < 30.0f -> "Heavy Armor Striker"
        else -> "Titan Mass Class"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .testTag("fitness_tracker_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Weight & BMI Telemetry Card ---
        item {
            CyberCard(
                borderColor = NeonCyan,
                backgroundColor = CyberSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonitorWeight,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WEIGHT & BMI TELEMETRY",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Button(
                            onClick = { showWeightDialog = true },
                            modifier = Modifier.testTag("log_weight_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+ LOG WEIGHT", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "CURRENT WEIGHT",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${currentStats.currentWeightKg} kg",
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Body Fat: ${currentStats.bodyFatPercent}%",
                                color = NeonAmber,
                                fontSize = 12.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "BMI GAUGE",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f", bmi),
                                color = NeonLime,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = bmiCategory,
                                color = NeonLime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Weight Evolution Sparkline / Recent Log Points
                    Text(
                        text = "RECENT WEIGH-INS:",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        weightLogs.take(3).forEach { log ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyberSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "${log.weightKg}kg", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = log.dateString.takeLast(5), color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Body Measurements Card ---
        item {
            CyberCard(
                borderColor = DarkGrayBorder,
                backgroundColor = CyberSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = null,
                                tint = NeonMagenta,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BODY DIMENSIONS",
                                color = NeonMagenta,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showMeasurementsDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("UPDATE", color = NeonMagenta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MeasurementItem(label = "WAIST", value = "${currentStats.waistCm} cm")
                        MeasurementItem(label = "CHEST", value = "${currentStats.chestCm} cm")
                        MeasurementItem(label = "BICEPS", value = "${currentStats.armsCm} cm")
                    }
                }
            }
        }

        // --- 3. Workout Logger Banner ---
        item {
            CyberCard(
                borderColor = NeonFlame,
                backgroundColor = Color(0xFF191018)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonFlame),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "WORKOUT TRAINING LOG",
                                color = NeonFlame,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Burn calories & claim hero XP",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = { showWorkoutDialog = true },
                        modifier = Modifier.testTag("log_workout_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonFlame),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("+ LOG", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // --- 4. Nutrition Journal Header ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NUTRITION JOURNAL MEALS",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "${allMeals.size} Total Entries",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        if (allMeals.isEmpty()) {
            item {
                CyberCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No meals logged yet. Use AI Camera Scanner to add meals!",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(allMeals) { meal ->
                MealJournalCard(
                    meal = meal,
                    onDelete = { viewModel.deleteMeal(meal.id) }
                )
            }
        }
    }

    // --- Dialogs ---
    if (showWeightDialog) {
        var weightInput by remember { mutableStateOf(currentStats.currentWeightKg.toString()) }
        var bodyFatInput by remember { mutableStateOf(currentStats.bodyFatPercent.toString()) }

        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Log Body Weight & Telemetry", color = NeonCyan) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.fillMaxWidth().testTag("weight_dialog_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = bodyFatInput,
                        onValueChange = { bodyFatInput = it },
                        label = { Text("Body Fat (%) - Optional") },
                        modifier = Modifier.fillMaxWidth().testTag("bodyfat_dialog_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightInput.toFloatOrNull() ?: currentStats.currentWeightKg
                        val bf = bodyFatInput.toFloatOrNull()
                        viewModel.logWeight(w, bf, "User log entry")
                        showWeightDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("SAVE (+75 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showWeightDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = CyberSurface
        )
    }

    if (showMeasurementsDialog) {
        var waist by remember { mutableStateOf(currentStats.waistCm.toString()) }
        var chest by remember { mutableStateOf(currentStats.chestCm.toString()) }
        var arms by remember { mutableStateOf(currentStats.armsCm.toString()) }

        AlertDialog(
            onDismissRequest = { showMeasurementsDialog = false },
            title = { Text("Update Measurements (cm)", color = NeonMagenta) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = waist, onValueChange = { waist = it }, label = { Text("Waist (cm)") })
                    OutlinedTextField(value = chest, onValueChange = { chest = it }, label = { Text("Chest (cm)") })
                    OutlinedTextField(value = arms, onValueChange = { arms = it }, label = { Text("Arms/Biceps (cm)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateMeasurements(
                            waist.toFloatOrNull() ?: currentStats.waistCm,
                            chest.toFloatOrNull() ?: currentStats.chestCm,
                            arms.toFloatOrNull() ?: currentStats.armsCm
                        )
                        showMeasurementsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                ) {
                    Text("SAVE (+50 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showMeasurementsDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = CyberSurface
        )
    }

    if (showWorkoutDialog) {
        var workoutType by remember { mutableStateOf("Dungeon Weightlifting") }
        var duration by remember { mutableStateOf("35") }
        var calsBurned by remember { mutableStateOf("280") }

        AlertDialog(
            onDismissRequest = { showWorkoutDialog = false },
            title = { Text("Log Workout Session", color = NeonFlame) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = workoutType, onValueChange = { workoutType = it }, label = { Text("Exercise / Session Name") })
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (Minutes)") })
                    OutlinedTextField(value = calsBurned, onValueChange = { calsBurned = it }, label = { Text("Est. Calories Burned (kcal)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = duration.toIntOrNull() ?: 30
                        val cals = calsBurned.toIntOrNull() ?: 200
                        viewModel.logWorkout(workoutType, mins, cals)
                        showWorkoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonFlame)
                ) {
                    Text("LOG WORKOUT", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showWorkoutDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = CyberSurface
        )
    }
}

@Composable
fun MeasurementItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun MealJournalCard(
    meal: MealEntity,
    onDelete: () -> Unit
) {
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(meal.timestamp))

    CyberCard(
        borderColor = DarkGrayBorder,
        backgroundColor = CyberSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = meal.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E283D)
                        ) {
                            Text(
                                text = meal.mealType,
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${meal.portionSize} • $timeStr",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${meal.calories} kcal",
                        color = NeonAmber,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Meal",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Macro badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroTag(label = "P", value = "${meal.protein.toInt()}g", color = NeonMagenta)
                MacroTag(label = "C", value = "${meal.carbs.toInt()}g", color = NeonCyan)
                MacroTag(label = "F", value = "${meal.fat.toInt()}g", color = NeonLime)

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF162520)
                ) {
                    Text(
                        text = "GRADE ${meal.healthGrade}",
                        color = NeonLime,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (meal.powerTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "💡 ${meal.powerTip}",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun MacroTag(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = CyberSurfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = color, fontWeight = FontWeight.Black, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
    }
}
