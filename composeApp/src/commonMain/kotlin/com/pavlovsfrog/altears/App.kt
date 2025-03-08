package com.pavlovsfrog.altears

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview

// More minimal, modern "hacker" inspired color scheme
private val LightColors = lightColorScheme(
    primary = Color(0xFF00B8D4),          // Bright cyan
    onPrimary = Color(0xFF000000),        // Black text on primary
    primaryContainer = Color(0xFFE0F7FA), // Light cyan container
    onPrimaryContainer = Color(0xFF001E26),
    secondary = Color(0xFF607D8B),        // Blue grey
    onSecondary = Color(0xFFFFFFFF),      // White text on secondary
    secondaryContainer = Color(0xFFECEFF1),// Light blue grey container
    onSecondaryContainer = Color(0xFF1A2327),
    tertiary = Color(0xFF4A148C),         // Deep purple
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEDE7F6),
    onTertiaryContainer = Color(0xFF1A0033),
    error = Color(0xFFB00020),            // Standard error
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF5F5F5),       // Slight off-white background
    onBackground = Color(0xFF121212),     // Very dark grey on background
    surface = Color(0xFFFFFFFF),          // Pure white surface
    onSurface = Color(0xFF121212),
    surfaceVariant = Color(0xFFF0F0F0),   // Light grey surface variant
    onSurfaceVariant = Color(0xFF222222), // Darker grey on surface variant
    outline = Color(0xFF78909C)           // Light blue grey outline
)

// Dark, hacker-inspired theme with minimal distractions
private val DarkColors = darkColorScheme(
    primary = Color(0xFF00E5FF),          // Bright cyan
    onPrimary = Color(0xFF000000),        // Black text on primary
    primaryContainer = Color(0xFF003544),  // Dark cyan container
    onPrimaryContainer = Color(0xFFB2EBF2),
    secondary = Color(0xFF78909C),        // Blue grey
    onSecondary = Color(0xFF121212),
    secondaryContainer = Color(0xFF263238),// Very dark blue grey
    onSecondaryContainer = Color(0xFFCFD8DC),
    tertiary = Color(0xFF9575CD),         // Light purple
    onTertiary = Color(0xFF121212),
    tertiaryContainer = Color(0xFF311B92),// Deep purple container
    onTertiaryContainer = Color(0xFFD1C4E9),
    error = Color(0xFFCF6679),            // Material dark error
    onError = Color(0xFF121212),
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF121212),       // Very dark grey background
    onBackground = Color(0xFFE0E0E0),     // Light grey on background
    surface = Color(0xFF1E1E1E),          // Slightly lighter surface
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),   // Mid-dark grey surface variant
    onSurfaceVariant = Color(0xFFBDBDBD), // Medium grey on surface variant
    outline = Color(0xFF546E7A)           // Medium blue grey outline
)

/**
 * Returns whether the device is currently in dark theme according to the platform.
 * This should be used instead of isSystemInDarkTheme() directly to allow platform-specific implementations.
 */
@Composable
expect fun isAppInDarkTheme(): Boolean

@Composable
@Preview
fun App(sdk: AltEarsSdk) {
    val isDarkTheme = isAppInDarkTheme()
    
    MaterialTheme(
        colorScheme = if (isDarkTheme) DarkColors else LightColors,
        typography = AppTypography
    ) {
        MainScreen(sdk)
    }
}