package com.pavlovsfrog.altears

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pavlovsfrog.altears.cache.AndroidDatabaseDriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(sdk = AltEarsSdk(AndroidDatabaseDriverFactory(this)))
        }
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}