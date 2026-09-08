package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = ThemeBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = ThemeBlue.copy(alpha = 0.15f),
    onPrimaryContainer = ThemeBlue,
    secondary = ThemeOrange,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    tertiary = ThemePink,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    background = ThemeBgLight,
    onBackground = ThemeDarkText,
    surface = ThemeSurface,
    onSurface = ThemeDarkText,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE8E8ED),
    onSurfaceVariant = ThemeDarkText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent brand light theme as specified
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
