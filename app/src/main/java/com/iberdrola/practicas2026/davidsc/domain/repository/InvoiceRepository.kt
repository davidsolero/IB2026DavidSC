package com.iberdrola.practicas2026.davidsc.domain.repository

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice

interface InvoiceRepository {
    suspend fun getInvoices(): List<Invoice>
}