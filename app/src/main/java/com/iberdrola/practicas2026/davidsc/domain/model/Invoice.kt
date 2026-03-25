package com.iberdrola.practicas2026.davidsc.domain.model

data class Invoice(
    val id: Int,
    val date: String,
    val description: String,
    val amount: Double,
    val status: String,
    val type: InvoiceType
)

enum class InvoiceType {
    LUZ,
    GAS
}