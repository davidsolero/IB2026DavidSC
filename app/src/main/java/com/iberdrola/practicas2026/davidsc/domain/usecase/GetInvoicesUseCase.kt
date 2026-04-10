package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceFilter
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import java.time.LocalDate

/**
 * Returns the list of invoices, optionally filtered by type, street and filter criteria.
 *
 * Filtering is applied in memory after fetching from the repository,
 * keeping the repository responsible only for data access.
 */
class GetInvoicesUseCase(private val repository: InvoiceRepository) {
    suspend operator fun invoke(
        type: InvoiceType? = null,
        street: String? = null,
        forceNetwork: Boolean = false,
        filter: InvoiceFilter = InvoiceFilter()
    ): List<Invoice> {
        val invoices = try {
            if (forceNetwork) repository.fetchInvoicesFromNetwork()
            else repository.getInvoices()
        } catch (e: Exception) {
            repository.getInvoices()
        }

        android.util.Log.d("GetInvoicesUseCase", "total before filter: ${invoices.size}, filter: $filter")

        val result = invoices
            .let { list -> type?.let { t -> list.filter { it.type == t } } ?: list }
            .let { list -> street?.let { s -> list.filter { it.street.equals(s, ignoreCase = true) } } ?: list }
            .let { list ->
                filter.desde?.let { desde ->
                    list.filter { LocalDate.parse(it.startDate) >= desde }
                } ?: list
            }
            .let { list ->
                filter.hasta?.let { hasta ->
                    list.filter { LocalDate.parse(it.startDate) <= hasta }
                } ?: list
            }
            .let { list ->
                filter.importeMin?.let { min ->
                    list.filter { it.amount >= min }
                } ?: list
            }
            .let { list ->
                filter.importeMax?.let { max ->
                    list.filter { it.amount <= max }
                } ?: list
            }
            .let { list ->
                if (filter.estados.isNotEmpty()) {
                    list.filter { it.status in filter.estados }
                } else list
            }

        android.util.Log.d("GetInvoicesUseCase", "total after filter: ${result.size}")

        return result
    }
}