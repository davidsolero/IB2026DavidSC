package com.iberdrola.practicas2026.davidsc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ContractDto(
    @SerializedName("id") val id: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("isActive") val isActive: Boolean?,
    @SerializedName("email") val email: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("holder") val holder: String?
)