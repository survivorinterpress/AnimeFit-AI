package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.AchievementEntity
import com.example.data.db.UserStatsEntity
import com.example.ui.AnimeAssets
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.NeonProgressBar
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
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AnimeFitViewModel

@Composable
fun AvatarRpgScreen(
    viewModel: AnimeFitViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val currentStats = stats ?: UserStatsEntity()

    val auraColor = when (currentStats.avatarAura) {
        "Magenta Flare" -> NeonMagenta
        "Golden Saiyan" -> NeonAmber
        "Void Violet" -> NeonPurple
        else -> NeonCyan
    }

    val infiniteTransition = rememberInfiniteTransition(label = "aura_glow")
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .testTag("avatar_rpg_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Avatar Visual Evolution Centerpiece ---
        item {
            CyberCard(
                borderColor = auraColor,
                backgroundColor = Color(0xFF0C1322)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HERO AVATAR EVOLUTION",
                        color = auraColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Avatar Portrait with Glowing Aura Frame
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .shadow(20.dp, CircleShape, spotColor = auraColor)
                            .border(3.dp, auraColor, CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = AnimeAssets.AvatarHero),
                            contentDescription = "Avatar Character",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${currentStats.playerName} • Lv. ${currentStats.level}",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyberSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            text = "RANK: ${currentStats.rankTitle.uppercase()}",
                            color = NeonAmber,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Power attributes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AttributeBadge(label = "STRENGTH", value = "${40 + currentStats.level * 8}")
                        AttributeBadge(label = "ENDURANCE", value = "${50 + currentStats.level * 6}")
                        AttributeBadge(label = "AGILITY", value = "${35 + currentStats.level * 7}")
                        AttributeBadge(label = "KI FOCUS", value = "${100 + currentStats.level * 15}")
                    }
                }
            }
        }

        // --- 2. Evolution Stages Roadmap ---
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
                    Text(
                        text = "RANK EVOLUTION TIER LADDER",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val ranks = listOf(
                        Triple("Rookie Hero", "Lv. 1-4", "Basic Training Suit & Cyan Sparks"),
                        Triple("Fitness Warrior", "Lv. 5-9", "Battle Wristbands & Magenta Flare"),
                        Triple("Nutrition Master", "Lv. 10-14", "Tactical Cyber Armor & Ki Mastery"),
                        Triple("Elite Champion", "Lv. 15-24", "Super Saiyan Golden Aura & Speed Trails"),
                        Triple("Legendary Athlete", "Lv. 25+", "God-Tier Cyber Phoenix Transformation")
                    )

                    ranks.forEachIndexed { idx, (rankName, lvReq, perk) ->
                        val isCurrentOrPast = currentStats.rankTitle == rankName || (idx == 0 && currentStats.level >= 1)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrentOrPast) NeonCyan else Color(0xFF222B3D)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCurrentOrPast) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = rankName,
                                        color = if (isCurrentOrPast) TextPrimary else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = lvReq,
                                        color = if (isCurrentOrPast) NeonAmber else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = perk,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Avatar Customizer (Skins & Auras) ---
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
                    Text(
                        text = "CUSTOMIZE AVATAR GEAR & AURAS",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Combat Outfit Skin:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Cyber Core", "Neon Striker", "Shinobi Shadow", "Titan Armor").forEach { skin ->
                            val isSel = currentStats.avatarSkin == skin
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) NeonCyan else CyberSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonCyan else DarkGrayBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.updateAvatarCustomization(skin, currentStats.avatarAura) }
                            ) {
                                Text(
                                    text = skin,
                                    color = if (isSel) Color.Black else TextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Power Aura Effect:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Electric Cyan", "Magenta Flare", "Golden Saiyan", "Void Violet").forEach { aura ->
                            val isSel = currentStats.avatarAura == aura
                            val col = when (aura) {
                                "Magenta Flare" -> NeonMagenta
                                "Golden Saiyan" -> NeonAmber
                                "Void Violet" -> NeonPurple
                                else -> NeonCyan
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) col else CyberSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) col else DarkGrayBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.updateAvatarCustomization(currentStats.avatarSkin, aura) }
                            ) {
                                Text(
                                    text = aura,
                                    color = if (isSel) Color.Black else TextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Achievements & Trophy Hall ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACHIEVEMENTS & BADGES",
                    color = NeonAmber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "${achievements.count { it.isUnlocked }} / ${achievements.size} Unlocked",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        items(achievements) { ach ->
            AchievementItemCard(achievement = ach)
        }

        // --- 5. Test Level-Up Demonstration Button ---
        item {
            Button(
                onClick = { viewModel.triggerTestLevelUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_levelup_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GAIN +300 XP & TEST LEVEL-UP FANFARE",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun AttributeBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = NeonCyan,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AchievementItemCard(achievement: AchievementEntity) {
    CyberCard(
        borderColor = if (achievement.isUnlocked) NeonAmber else DarkGrayBorder,
        backgroundColor = if (achievement.isUnlocked) Color(0xFF141A29) else Color(0xFF0F1420)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (achievement.isUnlocked) Color(0xFF332408) else Color(0xFF1B2233))
                    .border(1.dp, if (achievement.isUnlocked) NeonAmber else DarkGrayBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = achievement.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = achievement.title,
                        color = if (achievement.isUnlocked) TextPrimary else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (achievement.isUnlocked) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = achievement.description,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberSurfaceVariant
            ) {
                Text(
                    text = "+${achievement.xpReward} XP",
                    color = if (achievement.isUnlocked) NeonAmber else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
