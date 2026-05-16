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

fun isValidEmail(email: String): Boolean {
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false

    val parts = email.split("@")
    if (parts.size != 2) return false

    val local = parts[0]
    val domain = parts[1]
    
    if (local.length > 64 || domain.length > 255) return false
    if (local.startsWith(".") || local.endsWith(".")) return false
    if (domain.startsWith(".") || domain.endsWith(".")) return false
    if (".." in email) return false

    val domainParts = domain.split(".")
    if (domainParts.size < 2) return false
    if (domainParts.last().length < 2) return false

    return true
}