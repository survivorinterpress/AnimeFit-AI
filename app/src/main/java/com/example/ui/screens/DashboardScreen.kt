package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.MealEntity
import com.example.data.db.QuestEntity
import com.example.data.db.UserStatsEntity
import com.example.ui.AnimeAssets
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.MacroPill
import com.example.ui.components.NeonProgressBar
import com.example.ui.components.XpLevelBadge
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
import com.example.ui.theme.NeonWater
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AnimeFitViewModel

@Composable
fun DashboardScreen(
    viewModel: AnimeFitViewModel,
    onNavigateToScanner: () -> Unit,
    onNavigateToAvatar: () -> Unit,
    onNavigateToCoach: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val todayMeals by viewModel.todayMeals.collectAsStateWithLifecycle()
    val quests by viewModel.quests.collectAsStateWithLifecycle()

    val currentStats = stats ?: UserStatsEntity()

    val totalCaloriesToday = todayMeals.sumOf { it.calories }
    val totalProteinToday = todayMeals.map { it.protein }.sum()
    val totalCarbsToday = todayMeals.map { it.carbs }.sum()
    val totalFatToday = todayMeals.map { it.fat }.sum()

    val xpForNext = currentStats.level * 250 + 200
    val xpProgress = (currentStats.currentXp.toFloat() / xpForNext).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top Hero Profile Header ---
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
                        Column {
                            Text(
                                text = "ANIME HERO PROFILE",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = currentStats.playerName,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Streak Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF261805),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonFlame)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = NeonFlame,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentStats.streakDays} DAY STREAK",
                                    color = NeonFlame,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        XpLevelBadge(
                            level = currentStats.level,
                            rankTitle = currentStats.rankTitle
                        )

                        Text(
                            text = "XP: ${currentStats.currentXp} / $xpForNext",
                            color = NeonAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    NeonProgressBar(
                        progress = xpProgress,
                        startColor = NeonCyan,
                        endColor = NeonAmber,
                        height = 10.dp
                    )
                }
            }
        }

        // --- 2. Interactive Avatar Showcase Card ---
        item {
            CyberCard(
                borderColor = NeonMagenta,
                backgroundColor = Color(0xFF101626)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAvatar() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, NeonMagenta, RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = AnimeAssets.AvatarHero),
                            contentDescription = "Avatar Hero",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentStats.avatarSkin,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${currentStats.avatarAura}",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "Visual Form Evolution Active",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to customize outfit, auras & power tier →",
                            color = NeonMagenta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // --- 3. AI Camera Scanner Action Banner ---
        item {
            CyberCard(
                borderColor = NeonCyan,
                backgroundColor = Color(0xFF0C1F2E)
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
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI CAMERA SCANNER",
                                    color = NeonCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Scan food with camera to analyze calories, protein & macros in real time!",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = onNavigateToScanner,
                            modifier = Modifier.testTag("scan_meal_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text(
                                text = "SCAN",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 4. Calorie HUD & Burn Stats ---
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
                        Text(
                            text = "CALORIE ENERGY HUD",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "${currentStats.caloriesBurnedToday} kcal burned",
                            color = NeonFlame,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "$totalCaloriesToday",
                                color = TextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "/ ${currentStats.dailyCalorieTarget} kcal target",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }

                        val remaining = currentStats.dailyCalorieTarget - totalCaloriesToday
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CyberSurfaceVariant
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (remaining >= 0) "$remaining" else "+${-remaining}",
                                    color = if (remaining >= 0) NeonCyan else NeonMagenta,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (remaining >= 0) "Kcal Left" else "Over Budget",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    val calProgress = (totalCaloriesToday.toFloat() / currentStats.dailyCalorieTarget.coerceAtLeast(1)).coerceIn(0f, 1f)
                    NeonProgressBar(
                        progress = calProgress,
                        startColor = NeonCyan,
                        endColor = if (totalCaloriesToday > currentStats.dailyCalorieTarget) NeonMagenta else NeonLime,
                        height = 8.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Macronutrient Split
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MacroPill(
                            label = "Protein",
                            current = totalProteinToday,
                            target = currentStats.dailyProteinTarget.toFloat(),
                            unit = "g",
                            color = NeonMagenta,
                            modifier = Modifier.weight(1f)
                        )
                        MacroPill(
                            label = "Carbs",
                            current = totalCarbsToday,
                            target = currentStats.dailyCarbsTarget.toFloat(),
                            unit = "g",
                            color = NeonAmber,
                            modifier = Modifier.weight(1f)
                        )
                        MacroPill(
                            label = "Fats",
                            current = totalFatToday,
                            target = currentStats.dailyFatTarget.toFloat(),
                            unit = "g",
                            color = NeonLime,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // --- 5. Quick Water Intake Station ---
        item {
            CyberCard(
                borderColor = Color(0x3338BDF8),
                backgroundColor = Color(0xFF0F1B2B)
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Opacity,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "AQUA ELIXIR",
                                color = NeonWater,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${currentStats.waterLoggedMl} / ${currentStats.waterTargetMl} ml",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.addWater(250) },
                            modifier = Modifier.testTag("add_water_250_btn"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonWater)
                        ) {
                            Text("+250ml", color = NeonWater, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.addWater(500) },
                            modifier = Modifier.testTag("add_water_500_btn"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonWater)
                        ) {
                            Text("+500ml", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 6. Daily Quests Section ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY MISSIONS & QUESTS",
                    color = NeonAmber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "${quests.count { it.isCompleted }} / ${quests.size} Done",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        items(quests) { quest ->
            QuestItemCard(quest = quest, onClaim = { viewModel.claimQuest(quest) })
        }

        // --- 7. Coach Banner ---
        item {
            CyberCard(
                borderColor = NeonCyan,
                backgroundColor = Color(0xFF121B2F)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCoach() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val coachDrawable = if (currentStats.selectedCoach == "ren") AnimeAssets.CoachRen else AnimeAssets.CoachYuki
                    val coachName = if (currentStats.selectedCoach == "ren") "Coach Ren (Shonen Fire)" else "Coach Yuki (Tactical Cyber)"

                    Image(
                        painter = painterResource(id = coachDrawable),
                        contentDescription = "Coach Portrait",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, NeonCyan, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = coachName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "AI Voice Coach Ready • Tap for advice & workout talk →",
                            color = NeonCyan,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestItemCard(
    quest: QuestEntity,
    onClaim: () -> Unit
) {
    val progressRatio = (quest.currentProgress.toFloat() / quest.target.coerceAtLeast(1)).coerceIn(0f, 1f)
    val isComplete = quest.currentProgress >= quest.target

    CyberCard(
        borderColor = if (quest.isClaimed) DarkGrayBorder else if (isComplete) NeonLime else CyberCardBorder,
        backgroundColor = if (quest.isClaimed) Color(0xFF0E131E) else CyberSurface
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
                    Text(
                        text = quest.title,
                        color = if (quest.isClaimed) TextMuted else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = quest.description,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (quest.isClaimed) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1B2333)
                    ) {
                        Text(
                            text = "CLAIMED",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (isComplete) {
                    Button(
                        onClick = onClaim,
                        modifier = Modifier.testTag("claim_quest_${quest.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CLAIM +${quest.xpReward} XP",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberSurfaceVariant
                    ) {
                        Text(
                            text = "+${quest.xpReward} XP",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${quest.currentProgress} / ${quest.target} ${quest.unit}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "${(progressRatio * 100).toInt()}%",
                    color = if (isComplete) NeonLime else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            NeonProgressBar(
                progress = progressRatio,
                startColor = NeonCyan,
                endColor = if (isComplete) NeonLime else NeonMagenta,
                height = 6.dp
            )
        }
    }
}
