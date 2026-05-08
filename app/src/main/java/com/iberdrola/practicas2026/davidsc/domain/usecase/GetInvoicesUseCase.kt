package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceFilter
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import java.time.LocalDate

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

        val result = invoices
            .let { list -> type?.let { t -> list.filter { it.type == t } } ?: list }
            .let { list ->
                street?.let { s ->
                    list.filter {
                        it.street.equals(
                            s,
                            ignoreCase = true
                        )
                    }
                } ?: list
            }
            .let { list ->
                val desde = filter.desde
                val hasta = filter.hasta
                if (desde == null && hasta == null) list
                else list.filter { invoice ->
                    val invoiceDate = LocalDate.parse(invoice.date)

                    (desde == null || !invoiceDate.isBefore(desde)) &&
                            (hasta == null || !invoiceDate.isAfter(hasta))
                }
            }
            .let { list ->
                filter.importeMin?.let { min ->
                    list.filter { it.amount.toInt() >= min }
                } ?: list
            }
            .let { list ->
                filter.importeMax?.let { max ->
                    list.filter { it.amount.toInt() <= max }
                } ?: list
            }
            .let { list ->
                if (filter.estados.isNotEmpty()) {
                    list.filter { it.status in filter.estados }
                } else list
            }

        return result
    }
}