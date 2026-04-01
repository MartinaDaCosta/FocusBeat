package com.example.focusbeat.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary             = Primary,
    onPrimary           = TextOnPrimary,
    primaryContainer    = PrimaryContainer,
    onPrimaryContainer  = PrimaryDark,
    secondary           = AccentLilac,
    onSecondary         = TextOnPrimary,
    tertiary            = AccentSoft,
    background          = BackgroundLight,
    onBackground        = TextPrimary,
    surface             = SurfaceLight,
    onSurface           = TextPrimary,
    surfaceVariant      = SurfaceVariantLight,
    onSurfaceVariant    = TextSecondary,
    outline             = PrimaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary             = PrimaryLight,
    onPrimary           = TextPrimary,
    primaryContainer    = PrimaryDark,
    onPrimaryContainer  = TextOnPrimary,
    secondary           = AccentLilac,
    background          = BackgroundDark,
    onBackground        = TextOnPrimary,
    surface             = SurfaceDark,
    onSurface           = TextOnPrimary,
    surfaceVariant      = Color(0xFF2E2A45),
    onSurfaceVariant    = Color(0xFFB0AACB),
    outline             = Color(0xFF6B6491)
)

@Composable
fun FocusBeatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}