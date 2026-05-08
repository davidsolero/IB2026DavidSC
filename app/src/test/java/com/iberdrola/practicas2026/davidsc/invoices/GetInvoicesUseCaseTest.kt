package com.iberdrola.practicas2026.davidsc.invoices

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetInvoicesUseCaseTest {
    private val fakeRepository = object : InvoiceRepository {
        override suspend fun getInvoices(): List<Invoice> = listOf(
            Invoice(1, "2026-01-01", "Factura Luz", 52.3, "Pagada", InvoiceType.LUZ, "C/Larios"),
            Invoice(2, "2026-02-01", "Factura Gas", 28.4, "Pendiente de Pago", InvoiceType.GAS, "C/Larios"),
            Invoice(3, "2026-03-01", "Factura Luz", 32.5, "Pagada", InvoiceType.LUZ, "C/Larios")
        )

        override suspend fun fetchInvoicesFromNetwork(): List<Invoice> = getInvoices()
    }

    private val useCase = GetInvoicesUseCase(fakeRepository)

    @Test
    fun `filtering by LUZ returns only electricity invoices`() = runTest {
        val result = useCase(type = InvoiceType.LUZ)

        assertEquals(2, result.size)
        assertTrue(result.all { it.type == InvoiceType.LUZ })
    }

    @Test
    fun `filtering by street returns only invoices from that street`() = runTest {
        val result = useCase(street = "C/Larios")

        assertEquals(3, result.size)
    }

    @Test
    fun `filtering by type and street returns matching invoices only`() = runTest {
        val result = useCase(type = InvoiceType.GAS, street = "C/Larios")

        assertEquals(1, result.size)
        assertEquals(InvoiceType.GAS, result.first().type)
    }
}