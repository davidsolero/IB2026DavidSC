package com.iberdrola.practicas2026.davidsc.domain.usecase

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