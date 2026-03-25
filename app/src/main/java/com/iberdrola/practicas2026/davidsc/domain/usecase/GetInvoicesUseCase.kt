package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository

class GetInvoicesUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(type: InvoiceType? = null): List<Invoice> {
        val invoices = repository.getInvoices()

        return type?.let { selectedType ->
            invoices.filter { it.type == selectedType }
        } ?: invoices
    }
}