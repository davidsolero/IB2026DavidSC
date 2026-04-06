package com.iberdrola.practicas2026.davidsc.core.utils

/**
 * Global application configuration flags.
 * These values are set at runtime and shared across layers.
 *
 * Note: useMockLocal is persisted via SharedPreferences and restored on app start.
 * mockStreet filters invoices by street in mock mode. Null means no filter is applied.
 */
object AppConfig {
    var useMockLocal = false
    var mockStreet: String? = null
}