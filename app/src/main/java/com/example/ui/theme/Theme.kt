package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CyberDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = CyberCyan,
    tertiary = PremiumGold,
    background = DarkMidnight,
    surface = SurfaceOffset,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = DarkMidnight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
    onError = TextPrimary,
    outline = GlassCardBorder
)

private val CyberLightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    secondary = CyberCyan,
    tertiary = GoldMuted,
    background = DeepNavy,
    surface = SurfaceOffset,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = DarkMidnight,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Cyber Aesthetic by Default
    dynamicColor: Boolean = false, // Preserve our custom curated Neo Palette
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) CyberDarkColorScheme else CyberLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
