package com.iberdrola.practicas2026.davidsc.data.remote.api

import co.infinum.retromock.meta.MockResponse
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoicesResponse
import retrofit2.http.GET

interface InvoiceApi {
    @GET("invoices")
    @MockResponse(body = "invoices_mock.json")
    suspend fun getInvoices(): InvoicesResponse
}