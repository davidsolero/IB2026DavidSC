package com.iberdrola.practicas2026.davidsc.filter

import android.content.SharedPreferences
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceFilter
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import com.iberdrola.practicas2026.davidsc.ui.invoices.InvoicesViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class InvoicesFilterViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var useCase: GetInvoicesUseCase
    private lateinit var prefs: SharedPreferences

    private val sampleInvoices = listOf(
        Invoice(1, "2026-01-10", "Factura Luz", 50.0, "Pagada", InvoiceType.LUZ, "C/Larios"),
        Invoice(2, "2026-02-15", "Factura Gas", 30.0, "Pendiente de Pago", InvoiceType.GAS, "C/Larios"),
        Invoice(3, "2026-03-20", "Factura Luz", 70.0, "Pagada", InvoiceType.LUZ, "Otra Calle"),
        Invoice(4, "2026-02-25", "Factura Luz", 90.0, "Pendiente de Pago", InvoiceType.LUZ, "Centro")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        useCase = mockk()
        prefs = mockk(relaxed = true)

        every { prefs.getBoolean(any(), any()) } returns false

        coEvery { useCase(any(), any(), any()) } returns sampleInvoices
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads all invoices`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)

        advanceUntilIdle()

        assertEquals(4, viewModel.invoices.value.size)
    }

    @Test
    fun `filter by status returns only matching invoices`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)

        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                estados = setOf("Pagada")
            )
        )

        advanceUntilIdle()

        val result = viewModel.invoices.value

        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.status == "Pagada" })
    }

    @Test
    fun `filter by date range returns correct invoices`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)

        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                desde = LocalDate.parse("2026-02-01"),
                hasta = LocalDate.parse("2026-02-28")
            )
        )

        advanceUntilIdle()

        val result = viewModel.invoices.value

        assertTrue(result.isNotEmpty())

        result.forEach {
            val date = LocalDate.parse(it.date)
            assertFalse(date.isBefore(LocalDate.parse("2026-02-01")))
            assertFalse(date.isAfter(LocalDate.parse("2026-02-28")))
        }
    }

    @Test
    fun `filter by amount returns invoices in range`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)

        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                importeMin = 40,
                importeMax = 80
            )
        )

        advanceUntilIdle()

        val result = viewModel.invoices.value

        assertTrue(result.isNotEmpty())

        result.forEach {
            assertTrue(it.amount in 40.0..80.0)
        }
    }

    @Test
    fun `combined filters work correctly`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)

        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                estados = setOf("Pagada"),
                desde = LocalDate.parse("2026-01-01"),
                hasta = LocalDate.parse("2026-12-31"),
                importeMin = 40,
                importeMax = 80
            )
        )

        advanceUntilIdle()

        val result = viewModel.invoices.value

        assertTrue(result.all { it.status == "Pagada" })
        assertTrue(result.all { it.amount in 40.0..80.0 })

        result.forEach {
            val date = LocalDate.parse(it.date)
            assertFalse(date.isBefore(LocalDate.parse("2026-01-01")))
            assertFalse(date.isAfter(LocalDate.parse("2026-12-31")))
        }
    }

    @Test
    fun `clearFilter restores full list`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)

        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                estados = setOf("Pagada")
            )
        )

        advanceUntilIdle()

        viewModel.clearFilter()

        advanceUntilIdle()

        assertEquals(4, viewModel.invoices.value.size)
    }

    @Test
    fun `isFilterActive becomes true when filter applied`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)

        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                estados = setOf("Pagada")
            )
        )

        advanceUntilIdle()

        assertTrue(viewModel.isFilterActive.value)
    }


    @Test
    fun `filter returns empty list when no invoices match`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)
        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                estados = setOf("NoExisteEstado")
            )
        )

        advanceUntilIdle()

        assertTrue(viewModel.invoices.value.isEmpty())
    }

    @Test
    fun `handles empty invoice list`() = runTest {
        coEvery { useCase(any(), any(), any()) } returns emptyList()

        val viewModel = InvoicesViewModel(useCase, prefs)
        advanceUntilIdle()

        assertTrue(viewModel.invoices.value.isEmpty())
    }


    @Test
    fun `default filter shows all invoices`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)
        advanceUntilIdle()

        viewModel.applyFilter(InvoiceFilter())
        advanceUntilIdle()

        assertEquals(4, viewModel.invoices.value.size)
    }


    @Test
    fun `invalid date invoices are excluded`() = runTest {
        val badData = listOf(
            Invoice(1, "invalid-date", "Factura", 50.0, "Pagada", InvoiceType.LUZ, "Calle")
        )

        coEvery { useCase(any(), any(), any()) } returns badData

        val viewModel = InvoicesViewModel(useCase, prefs)
        advanceUntilIdle()

        assertTrue(viewModel.invoices.value.isEmpty())
    }

    @Test
    fun `amount boundary values are included`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)
        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                importeMin = 50,
                importeMax = 50
            )
        )

        advanceUntilIdle()

        val result = viewModel.invoices.value

        assertTrue(result.all { it.amount == 50.0 })
    }

    @Test
    fun `date boundaries are included`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)
        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                desde = LocalDate.parse("2026-02-15"),
                hasta = LocalDate.parse("2026-02-15")
            )
        )

        advanceUntilIdle()

        val result = viewModel.invoices.value

        assertTrue(result.all { it.date == "2026-02-15" })
    }

    @Test
    fun `multiple statuses filter correctly`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)
        advanceUntilIdle()

        viewModel.applyFilter(
            InvoiceFilter(
                estados = setOf("Pagada", "Pendiente de Pago")
            )
        )

        advanceUntilIdle()

        val result = viewModel.invoices.value

        assertTrue(result.all {
            it.status in setOf("Pagada", "Pendiente de Pago")
        })
    }

    @Test
    fun `changing filter replaces previous filter`() = runTest {
        val viewModel = InvoicesViewModel(useCase, prefs)
        advanceUntilIdle()

        viewModel.applyFilter(InvoiceFilter(estados = setOf("Pagada")))
        advanceUntilIdle()

        viewModel.applyFilter(InvoiceFilter(estados = setOf("Pendiente de Pago")))
        advanceUntilIdle()

        val result = viewModel.invoices.value

        assertTrue(result.all { it.status == "Pendiente de Pago" })
    }
}