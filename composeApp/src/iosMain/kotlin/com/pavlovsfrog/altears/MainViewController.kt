package com.pavlovsfrog.altears

import androidx.compose.ui.window.ComposeUIViewController
import com.pavlovsfrog.altears.cache.IOSDatabaseDriverFactory

fun MainViewController() = ComposeUIViewController {
    App(sdk = AltEarsSdk(IOSDatabaseDriverFactory()))
}