package com.iberdrola.practicas2026.davidsc.main

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetStreetsUseCase
import com.iberdrola.practicas2026.davidsc.utils.FakeInvoiceRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetStreetsUseCaseTest {

    @Test
    fun `returns unique sorted streets`() = runBlocking {
        val invoices = listOf(
            Invoice(
                1,
                "2026-01-01",
                "Luz Enero",
                50.0,
                "PAID",
                InvoiceType.LUZ,
                "Calle A"
            ),
            Invoice(2, "2026-02-01",  "Gas Febrero", 30.0, "PAID", InvoiceType.GAS, "Calle B"),
            Invoice(3, "2026-03-01", "Luz Marzo", 60.0, "PAID", InvoiceType.LUZ, "Calle A") // repetida
        )

        val fakeRepository = FakeInvoiceRepository(invoices)

        val getInvoicesUseCase = GetInvoicesUseCase(fakeRepository)

        val useCase = GetStreetsUseCase(getInvoicesUseCase)

        val streets = useCase()

        assertEquals(listOf("Calle A", "Calle B"), streets)
    }
}
