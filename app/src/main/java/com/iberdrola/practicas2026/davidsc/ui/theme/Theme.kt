package com.iberdrola.practicas2026.davidsc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

@Composable
fun IB2026DavidSCTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WhiteColorScheme,
        content = content
    )
}