package com.iberdrola.practicas2026.davidsc.domain.repository

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType

interface InvoiceRepository {
    suspend fun getInvoices(): List<Invoice>
    suspend fun fetchInvoicesFromNetwork(): List<Invoice>
}