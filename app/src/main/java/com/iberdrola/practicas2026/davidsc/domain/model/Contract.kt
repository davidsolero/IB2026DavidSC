package com.iberdrola.practicas2026.davidsc.domain.model

/**
 * Represents an energy supply contract belonging to the user.
 *
 * [isActive] indicates whether electronic billing is currently enabled.
 * [email] is the address currently registered for electronic billing,
 * present only when [isActive] is true.
 */
data class Contract(
    val id: String,
    val type: ContractType,
    val isActive: Boolean,
    val holder: String,
    val address: String,
    val email: String?
)