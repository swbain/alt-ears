package com.pavlovsfrog.altears

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview

private val DarkColors = darkColorScheme(
    primary = Color(0xFF86CBFC),
    onPrimary = Color(0xFF003351),
    primaryContainer = Color(0xFF004B74),
    onPrimaryContainer = Color(0xFFCDE5FF),
    secondary = Color(0xFFBAC8DA),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3C4858),
    onSecondaryContainer = Color(0xFFD6E4F7),
    tertiary = Color(0xFFD8BEE4),
    onTertiary = Color(0xFF3B2948),
    tertiaryContainer = Color(0xFF533F5F),
    onTertiaryContainer = Color(0xFFF5DAFF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF42474E),
    onSurfaceVariant = Color(0xFFC2C7CF),
    outline = Color(0xFF8C9199)
)

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = DarkColors
    ) {
        Scaffold {
            MainScreen()
        }
    }
}