package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository

/**
 * Returns the list of invoices, optionally filtered by type and street.
 *
 * Filtering is applied in memory after fetching from the repository,
 * keeping the repository responsible only for data access.
 */
class GetInvoicesUseCase(private val repository: InvoiceRepository) {
    suspend operator fun invoke(
        type: InvoiceType? = null,
        street: String? = null,
        forceNetwork: Boolean = false
    ): List<Invoice> {
        return try {
            if (forceNetwork) repository.fetchInvoicesFromNetwork()
            else repository.getInvoices()
        } catch (e: Exception) {
            repository.getInvoices() // fallback a caché
        }
            .let { list -> type?.let { t -> list.filter { it.type == t } } ?: list }
            .let { list -> street?.let { s -> list.filter { it.street.equals(s, ignoreCase = true) } } ?: list }
    }
}