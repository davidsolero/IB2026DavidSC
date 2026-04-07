package com.iberdrola.practicas2026.davidsc.invoices

import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import com.iberdrola.practicas2026.davidsc.ui.invoices.InvoicesViewModel
import com.iberdrola.practicas2026.davidsc.utils.FakeSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InvoicesViewModelTest {

    private lateinit var viewModel: InvoicesViewModel
    private lateinit var fakePrefs: FakeSharedPreferences

    private val testDispatcher = StandardTestDispatcher()

    private val fakeInvoices = listOf(
        Invoice(1, "2026-01-01", "2026-01-31", "Factura Luz", 50.0, "Pagada", InvoiceType.LUZ, "C/Larios"),
        Invoice(2, "2026-02-01", "2026-02-28", "Factura Gas", 30.0, "Pendiente", InvoiceType.GAS, "C/Larios")
    )

    private val fakeUseCase = GetInvoicesUseCase(
        repository = object : InvoiceRepository {
            override suspend fun getInvoices(): List<Invoice> = fakeInvoices
        }
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakeSharedPreferences()

        viewModel = InvoicesViewModel(fakeUseCase, fakePrefs)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Carga inicial
    @Test
    fun `init loads invoices`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.invoices.value.size) // default LUZ filter
        assertTrue(viewModel.invoices.value.all { it.type == InvoiceType.LUZ })
    }

    // 2. Cambio de tipo
    @Test
    fun `selectType updates invoices`() = runTest {
        viewModel.selectType(InvoiceType.GAS)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.invoices.value.size)
        assertEquals(InvoiceType.GAS, viewModel.invoices.value.first().type)
    }

    // 3. Loading state
    @Test
    fun `loading is set correctly`() = runTest {
        viewModel.selectType(InvoiceType.GAS)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }

    // 4. Error handling
    @Test
    fun `error is set when use case fails`() = runTest {
        val failingUseCase = GetInvoicesUseCase(
            repository = object : InvoiceRepository {
                override suspend fun getInvoices(): List<Invoice> {
                    throw RuntimeException("Boom")
                }
            }
        )

        viewModel = InvoicesViewModel(failingUseCase, fakePrefs)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Boom", viewModel.error.value)
    }

    //  5. toggleMock guarda en prefs
    @Test
    fun `toggleMock updates prefs and state`() {
        val initial = viewModel.useMock.value

        viewModel.toggleMock()

        assertEquals(!initial, viewModel.useMock.value)
        assertEquals(!initial, fakePrefs.getBoolean("use_mock", false))
    }

    // onBackPressed lógica
    @Test
    fun `onBackPressed returns true when threshold reached`() {
        fakePrefs.putInt("invoice_show_sheet_threshold", 2)

        val first = viewModel.onBackPressed()
        val second = viewModel.onBackPressed()

        assertFalse(first)
        assertTrue(second)
    }
}