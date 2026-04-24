package com.iberdrola.practicas2026.davidsc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.iberdrola.practicas2026.davidsc.R


/**
 * IB2026DavidSCTheme
 *
 * Custom Material 3 theme wrapper for the application.
 *
 * This theme centralizes design system configuration, including:
 * - Color scheme (currently a minimal white-based avoiding pink scheme)
 * - Typography (custom IberPangea font family applied globally)
 *
 * Although currently simple, this file acts as the single entry point for
 * future theming evolution (dark mode, semantic colors, spacing system, etc.)
 * without requiring changes across individual composables.
 *
 * Keeping this abstraction ensures consistent UI styling across the app
 * and decouples design decisions from feature implementations.
 */

private val WhiteColorScheme = lightColorScheme(
    background = Color.White,
    surface = Color.White
)

val IberPangeaFont = FontFamily(
    Font(R.font.iber_pangea, FontWeight.Normal),
    Font(R.font.pangea_afrikan_trial_medium, FontWeight.Medium),
    Font(R.font.pangea_afrikan_trial_semibold, FontWeight.SemiBold),
    Font(R.font.pangea_afrikan_trial_bold, FontWeight.Bold)
)
val DefaultTypography = Typography()
val AppTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = IberPangeaFont),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = IberPangeaFont),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = IberPangeaFont),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = IberPangeaFont),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = IberPangeaFont),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = IberPangeaFont),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = IberPangeaFont),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = IberPangeaFont),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = IberPangeaFont),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = IberPangeaFont),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = IberPangeaFont),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = IberPangeaFont),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = IberPangeaFont),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = IberPangeaFont),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = IberPangeaFont),
)
@Composable
fun IB2026DavidSCTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WhiteColorScheme,
        typography = AppTypography,
        content = content
    )
}