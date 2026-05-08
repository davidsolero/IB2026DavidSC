package com.iberdrola.practicas2026.davidsc.ui.util

fun maskEmail(email: String?): String {
    if (email.isNullOrBlank()) return ""
    val atIndex = email.indexOf('@')
    if (atIndex <= 1) return email

    val local = email.substring(0, atIndex)
    val domain = email.substring(atIndex)

    if (local.length <= 2) return email

    val masked = local.first() +
            "*".repeat(local.length - 2) +
            local.last()

    return masked + domain
}

fun isValidEmail(email: String): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()