package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LevelUpFanfareModal
import com.example.ui.screens.AvatarRpgScreen
import com.example.ui.screens.CoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FitnessTrackerScreen
import com.example.ui.screens.FoodScannerScreen
import com.example.ui.theme.AnimeFitTheme
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.DarkGrayBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AnimeFitViewModel

enum class AnimeFitTab(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Hub", Icons.Default.Home, "tab_dashboard"),
    SCANNER("AI Scan", Icons.Default.CameraAlt, "tab_scanner"),
    COACH("Coach", Icons.Default.RecordVoiceOver, "tab_coach"),
    AVATAR("Avatar", Icons.Default.MilitaryTech, "tab_avatar"),
    JOURNAL("Journal", Icons.Default.FitnessCenter, "tab_journal")
}

class MainActivity : ComponentActivity() {
    private val viewModel: AnimeFitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AnimeFitTheme {
                var currentTab by remember { mutableStateOf(AnimeFitTab.DASHBOARD) }
                val levelUpEvent by viewModel.levelUpEvent.collectAsStateWithLifecycle()

                // Custom back handling: if not on dashboard, return to dashboard
                if (currentTab != AnimeFitTab.DASHBOARD) {
                    BackHandler {
                        currentTab = AnimeFitTab.DASHBOARD
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = CyberBackground,
                    bottomBar = {
                        NavigationBar(
                            containerColor = CyberSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = DarkGrayBorder,
                                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                ),
                            tonalElevation = 8.dp
                        ) {
                            AnimeFitTab.values().forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = tab },
                                    modifier = Modifier.testTag(tab.tag),
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = NeonCyan,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextMuted
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            AnimeFitTab.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToScanner = { currentTab = AnimeFitTab.SCANNER },
                                onNavigateToAvatar = { currentTab = AnimeFitTab.AVATAR },
                                onNavigateToCoach = { currentTab = AnimeFitTab.COACH }
                            )
                            AnimeFitTab.SCANNER -> FoodScannerScreen(
                                viewModel = viewModel
                            )
                            AnimeFitTab.COACH -> CoachScreen(
                                viewModel = viewModel
                            )
                            AnimeFitTab.AVATAR -> AvatarRpgScreen(
                                viewModel = viewModel
                            )
                            AnimeFitTab.JOURNAL -> FitnessTrackerScreen(
                                viewModel = viewModel
                            )
                        }

                        // Level-Up celebration modal
                        if (levelUpEvent != null) {
                            val (level, rank) = levelUpEvent!!
                            LevelUpFanfareModal(
                                level = level,
                                rankTitle = rank,
                                onDismiss = { viewModel.dismissLevelUpEvent() }
                            )
                        }
                    }
                }
            }
        }
    }
}
