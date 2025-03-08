package com.pavlovsfrog.altears

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
actual fun isAppInDarkTheme(): Boolean = isSystemInDarkTheme()