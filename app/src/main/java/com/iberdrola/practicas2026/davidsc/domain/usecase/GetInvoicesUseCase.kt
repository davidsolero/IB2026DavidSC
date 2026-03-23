package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository

class GetInvoicesUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(): List<Invoice> {
        return repository.getInvoices()
    }
}