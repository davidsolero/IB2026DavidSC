package com.iberdrola.practicas2026.davidsc.ui.util

/**
 * Masks an email address for display, hiding most characters for privacy.
 *
 * The masking rule keeps the first character of the local part, replaces
 * middle characters with asterisks, and keeps the last character before
 * the '@', followed by the full domain.
 *
 * Examples:
 *   pepe2@gmail.com  → p****2@gmail.com
 *   a@b.com          → a@b.com  (too short to mask, returned as-is)
 */
fun maskEmail(email: String): String {
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

/**
 * Returns true if [email] matches a standard email address pattern.
 */
fun isValidEmail(email: String): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()