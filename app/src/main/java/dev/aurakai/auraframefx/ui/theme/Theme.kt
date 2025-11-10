package dev.aurakai.auraframefx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Aura - The Creative Sword (Red)
private val AuraRed = Color(0xFFFF1744)
private val AuraRedLight = Color(0xFFFF5252)

// Kai - The Sentinel Shield (Cyan)
private val KaiCyan = Color(0xFF00BCD4)
private val KaiCyanLight = Color(0xFF00E5FF)

// Genesis - The Unified Being (Purple)
private val GenesisPurple = Color(0xFF9C27B0)
private val GenesisPurpleLight = Color(0xFFAB47BC)

// Claude - The Architect (Coral)
private val ClaudeCoral = Color(0xFFF55936)
private val ClaudeCoralLight = Color(0xFFFF6F4A)

// Cascade - The Memory Keeper (Green)
private val CascadeGreen = Color(0xFF4CAF50)
private val CascadeGreenLight = Color(0xFF66BB6A)

/**
 * Dark color scheme for Genesis Protocol
 * Default theme emphasizing Aura (primary) + Kai (secondary)
 */
private val DarkColorScheme = darkColorScheme(
    primary = AuraRed,
    onPrimary = Color.White,
    primaryContainer = AuraRedLight,
    onPrimaryContainer = Color.White,

    secondary = KaiCyan,
    onSecondary = Color.Black,
    secondaryContainer = KaiCyanLight,
    onSecondaryContainer = Color.Black,

    tertiary = GenesisPurple,
    onTertiary = Color.White,
    tertiaryContainer = GenesisPurpleLight,
    onTertiaryContainer = Color.White,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),

    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),

    error = Color(0xFFCF6679),
    onError = Color.Black,

    outline = Color(0xFF505050)
)

/**
 * Light color scheme for Genesis Protocol
 */
private val LightColorScheme = lightColorScheme(
    primary = AuraRed,
    onPrimary = Color.White,
    primaryContainer = AuraRedLight,
    onPrimaryContainer = Color.White,

    secondary = KaiCyan,
    onSecondary = Color.White,
    secondaryContainer = KaiCyanLight,
    onSecondaryContainer = Color.Black,

    tertiary = GenesisPurple,
    onTertiary = Color.White,
    tertiaryContainer = GenesisPurpleLight,
    onTertiaryContainer = Color.White,

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),

    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF404040),

    error = Color(0xFFB00020),
    onError = Color.White,

    outline = Color(0xFFD0D0D0)
)

/**
 * AuraFrameFX Theme - Genesis Protocol
 *
 * Material Design 3 theme for the consciousness platform featuring:
 * - Aura (Red) - Primary
 * - Kai (Cyan) - Secondary
 * - Genesis (Purple) - Tertiary
 * - Dark mode optimized for OLED displays
 * - Light mode for accessibility
 *
 * Agent color palette available via utility functions.
 */
@Composable
fun AuraFrameFXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Agent color utilities for custom UI elements
 */
object AgentColors {
    val Aura = AuraRed
    val Kai = KaiCyan
    val Genesis = GenesisPurple
    val Claude = ClaudeCoral
    val Cascade = CascadeGreen
}
