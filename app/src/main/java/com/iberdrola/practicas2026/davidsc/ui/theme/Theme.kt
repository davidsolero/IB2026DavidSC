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
 * This file is kept even though it currently only wraps the default MaterialTheme.
 * Reason: it serves as a central point to customize colors, typography, or shapes
 * of the project later on without having to modify each individual component.
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