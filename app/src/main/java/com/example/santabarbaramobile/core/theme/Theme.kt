package com.example.santabarbaramobile.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorSurface = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = SignalRedDark,
    secondary = ArchiveGold,
    tertiary = Mist,
    background = Slate950,
    surface = Slate900,
    surfaceVariant = Slate800,
    onPrimary = Slate950,
    onSecondary = Slate950,
    onBackground = Paper,
    onSurface = Paper,
    onSurfaceVariant = Mist
)

private val LightColorScheme = lightColorScheme(
    primary = SignalRed,
    secondary = ArchiveGold,
    tertiary = Slate800,
    background = Paper,
    surface = ColorSurface,
    surfaceVariant = Mist,
    onPrimary = Paper,
    onSecondary = Ink,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = SoftGray
)

@Composable
fun SantaBarbaraMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
