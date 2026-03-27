package com.iberdrola.practicas2026.davidsc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: Int,
    val startDate: String,
    val endDate: String,
    val description: String,
    val amount: Double,
    val status: String,
    val type: String,
    val street: String
)