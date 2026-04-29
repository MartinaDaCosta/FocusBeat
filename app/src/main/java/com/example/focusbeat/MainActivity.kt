package com.example.focusbeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusbeat.ui.navigation.FocusBeatNavHost
import com.example.focusbeat.ui.theme.FocusBeatTheme
import com.example.focusbeat.viewmodel.AuthViewModel
import com.example.focusbeat.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FocusBeatTheme {
                val playerViewModel: PlayerViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                FocusBeatNavHost(
                    playerViewModel = playerViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}