package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsportsDarkColorScheme = darkColorScheme(
    primary = EsportsRedPrimary,
    onPrimary = Color.White,
    primaryContainer = EsportsRedVariant,
    onPrimaryContainer = Color.White,
    secondary = EsportsGold,
    onSecondary = Color.Black,
    secondaryContainer = EsportsGoldLight,
    onSecondaryContainer = Color.Black,
    tertiary = EsportsRedGlow,
    onTertiary = Color.White,
    background = EsportsBackground,
    onBackground = EsportsTextPrimary,
    surface = EsportsSurface,
    onSurface = EsportsTextPrimary,
    surfaceVariant = EsportsSurfaceVariant,
    onSurfaceVariant = EsportsTextSecondary,
    outline = EsportsBorder,
    error = EsportsError,
    onError = Color.White
)

@Composable
fun SamarEsportsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsportsDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SamarEsportsTheme(content = content)
}
