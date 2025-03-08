package com.pavlovsfrog.altears

import androidx.compose.runtime.Composable
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle

@Composable
actual fun isAppInDarkTheme(): Boolean {
    // Check if the iOS device is in dark mode
    val currentStyle = UIScreen.mainScreen.traitCollection.userInterfaceStyle
    return currentStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
}