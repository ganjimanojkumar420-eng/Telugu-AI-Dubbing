package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CinematicDarkColorScheme = darkColorScheme(
    primary = CinemaGold,
    onPrimary = TextOnGold,
    primaryContainer = CinemaGoldDark,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricCyan,
    onSecondary = TextOnGold,
    secondaryContainer = ElectricCyanDim,
    onSecondaryContainer = ElectricCyanBright,
    tertiary = RubyCrimson,
    onTertiary = TextPrimary,
    background = AbyssDark,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainerDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderHighlight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force cinematic dark UI
    dynamicColor: Boolean = false, // Keep intentional cinematic theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CinematicDarkColorScheme,
        typography = Typography,
        content = content
    )
}
