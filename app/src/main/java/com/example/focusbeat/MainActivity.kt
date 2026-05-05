package com.example.focusbeat

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.focusbeat.ui.navigation.FocusBeatNavHost
import com.example.focusbeat.ui.theme.FocusBeatTheme
import com.example.focusbeat.ui.components.NotificationHelper
import com.example.focusbeat.viewmodel.PlayerViewModel
import com.example.focusbeat.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        setContent {
            FocusBeatTheme {
                FocusBeatNavHost(
                    authViewModel = authViewModel,
                    playerViewModel = playerViewModel
                )
            }
        }
    }
}