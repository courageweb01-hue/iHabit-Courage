package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = FrostedPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = FrostedPurpleContainer,
    onPrimaryContainer = FrostedOnPurpleContainer,
    secondary = IOSGreen,
    onSecondary = Color.White,
    tertiary = IOSOrange,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceSecondary,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0x77FFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = FrostedPurplePrimary,
    onPrimary = DarkTextPrimary,
    primaryContainer = FrostedPurplePrimary.copy(alpha = 0.35f),
    onPrimaryContainer = FrostedPurpleContainer,
    secondary = IOSGreen,
    onSecondary = DarkTextPrimary,
    tertiary = IOSOrange,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceSecondary,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0x33FFFFFF)
)

@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
