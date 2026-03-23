package com.iberdrola.practicas2026.davidsc.data.remote.api

import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import retrofit2.http.GET

interface InvoiceApi {
    @GET("invoices")
    suspend fun getInvoices(): List<InvoiceDto>
}