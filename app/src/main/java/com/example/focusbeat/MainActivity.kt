package com.example.focusbeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.focusbeat.ui.navigation.FocusBeatNavHost
import com.example.focusbeat.ui.theme.FocusBeatTheme
import com.example.focusbeat.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        playerViewModel.initController(this)
        setContent {
            FocusBeatTheme {
                FocusBeatNavHost(playerViewModel = playerViewModel)
            }
        }
    }
}