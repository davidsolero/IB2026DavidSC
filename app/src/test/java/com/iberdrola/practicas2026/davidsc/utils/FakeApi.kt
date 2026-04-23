package com.iberdrola.practicas2026.davidsc.utils

import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoicesResponse

class FakeApi : InvoiceApi {

    var shouldFail = false

    override suspend fun getInvoices(): InvoicesResponse {
        if (shouldFail) throw RuntimeException("API error")

        return InvoicesResponse(
            numFacturas = 2,
            facturas = listOf(
                InvoiceDto(
                    1,
                    "2026-01-01",
                    "2026-01-31",
                    "2026-01-31",
                    "Factura Luz",
                    50.0,
                    "Pagada",
                    "luz",
                    "C/Larios"
                ),
                InvoiceDto(
                    2,
                    "2026-02-01",
                    "2026-02-28",
                    "2026-01-31",
                    "Factura Gas",
                    30.0,
                    "Pendiente",
                    "gas",
                    "C/Larios"
                )
            )
        )
    }
}