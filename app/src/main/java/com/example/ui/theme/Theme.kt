package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = TextDark,
    primaryContainer = CardBackground,
    onPrimaryContainer = ElectricCyan,
    secondary = BrightCyan,
    onSecondary = TextDark,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextWhite,
    tertiary = NeonGreen,
    onTertiary = TextDark,
    background = ObsidianNavy,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = CardBorder,
    error = CrimsonError,
    onError = TextWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek cyber dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
