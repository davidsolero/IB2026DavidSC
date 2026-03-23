package com.iberdrola.practicas2026.davidsc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class InvoiceDto(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("description") val description: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("status") val status: String
)
