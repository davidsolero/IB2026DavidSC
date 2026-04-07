package com.iberdrola.practicas2026.davidsc.utils

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository

class FakeInvoiceRepository(
    private val invoices: List<Invoice>
) : InvoiceRepository {
    override suspend fun getInvoices(): List<Invoice> = invoices
}
