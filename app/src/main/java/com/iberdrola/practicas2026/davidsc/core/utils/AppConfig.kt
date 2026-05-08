package com.iberdrola.practicas2026.davidsc.core.utils

object AppConfig {
    var useMockLocal = false
    var mockStreet: String? = null
    const val MOCK_DELAY_MIN_MS = 1000L
    const val MOCK_DELAY_MAX_MS = 3001L


    const val RATING_THRESHOLD_RATED = 10
    const val RATING_THRESHOLD_LATER = 3
    const val RATING_THRESHOLD_DISMISSED = 1
}