package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AnimeCyberColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004D5A),
    onPrimaryContainer = Color(0xFF9EFFFF),
    
    secondary = NeonMagenta,
    onSecondary = Color(0xFF3E001B),
    secondaryContainer = Color(0xFF650030),
    onSecondaryContainer = Color(0xFFFFD9E2),
    
    tertiary = NeonAmber,
    onTertiary = Color(0xFF3A2400),
    tertiaryContainer = Color(0xFF6B4500),
    onTertiaryContainer = Color(0xFFFFDF9E),
    
    background = CyberBackground,
    onBackground = TextPrimary,
    
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    outline = DarkGrayBorder,
    outlineVariant = CyberCardBorder
)

@Composable
fun AnimeFitTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AnimeCyberColorScheme,
        typography = Typography,
        content = content
    )
}
