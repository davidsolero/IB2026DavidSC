package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository

/**
 * Returns the list of unique streets present across all invoices.
 *
 * Derives streets from invoice data instead of requiring a dedicated endpoint,
 * keeping the repository interface minimal while the data contract evolves.
 */
class GetStreetsUseCase(
    private val getInvoices: GetInvoicesUseCase
) {
    suspend operator fun invoke(): List<String> {
        return getInvoices()
            .map { it.street }
            .distinct()
            .sorted()
    }
}