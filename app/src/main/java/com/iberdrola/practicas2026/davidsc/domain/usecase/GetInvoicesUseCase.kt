package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
class GetInvoicesUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(
        type: InvoiceType? = null,
        street: String? = null,
        useMock: Boolean
    ): List<Invoice> {
        val invoices = repository.getInvoices()

        val selectedStreet = if (useMock) AppConfig.mockStreet else street

        return invoices
            .let { list -> type?.let { t -> list.filter { it.type == t } } ?: list }
            .let { list -> selectedStreet?.let { s -> list.filter { it.street.equals(s, ignoreCase = true) } } ?: list }
    }
}