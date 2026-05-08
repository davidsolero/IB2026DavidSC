package com.iberdrola.practicas2026.davidsc.domain.model

data class Contract(
    val id: String,
    val type: ContractType,
    val isActive: Boolean,
    val holder: String,
    val address: String,
    val email: String?
)