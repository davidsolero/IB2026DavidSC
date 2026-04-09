package com.iberdrola.practicas2026.davidsc.domain.model

data class Invoice(
    val id: Int,
    val startDate: String,
    val endDate : String,
    val description: String,
    val amount: Double,
    val status: String,
    val type: InvoiceType,
    val street : String
)

enum class InvoiceType {
    LUZ,
    GAS
}