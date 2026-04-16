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

    // -----------------------------
    // STATUS FILTER TEST
    // -----------------------------
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

    // -----------------------------
    // DATE FILTER TEST
    // -----------------------------
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

    // -----------------------------
    // AMOUNT FILTER TEST
    // -----------------------------
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

    // -----------------------------
    // COMBINED FILTER TEST
    // -----------------------------
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

    // -----------------------------
    // CLEAR FILTER TEST
    // -----------------------------
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

    // -----------------------------
    // FILTER ACTIVE STATE
    // -----------------------------
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
}