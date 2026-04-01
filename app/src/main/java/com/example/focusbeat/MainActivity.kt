package com.example.focusbeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.focusbeat.ui.theme.FocusBeatTheme
import com.example.focusbeat.ui.navigation.FocusBeatNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            FocusBeatTheme {
                FocusBeatNavHost()
            }
        }
    }
}