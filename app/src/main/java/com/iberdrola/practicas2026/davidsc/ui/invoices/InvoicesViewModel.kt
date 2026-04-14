package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceFilter
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val prefs: SharedPreferences
) : ViewModel() {

    // -----------------------------
    // SOURCE CONFIG
    // -----------------------------
    private val _useMock = MutableStateFlow(
        prefs.getBoolean(PREF_USE_MOCK, AppConfig.useMockLocal)
    )
    val useMock: StateFlow<Boolean> = _useMock.asStateFlow()

    private val _selectedType = MutableStateFlow(InvoiceType.LUZ)
    val selectedType = _selectedType.asStateFlow()

    private val _selectedStreet = MutableStateFlow<String?>(AppConfig.mockStreet)
    val selectedStreet = _selectedStreet.asStateFlow()

    // -----------------------------
    // DATA SOURCE (NO FILTER)
    // -----------------------------
    private val _allInvoices = MutableStateFlow<List<Invoice>>(emptyList())
    val allInvoices = _allInvoices.asStateFlow()

    // -----------------------------
    // FILTER STATE
    // -----------------------------
    private val _activeFilter = MutableStateFlow(InvoiceFilter())
    val activeFilter = _activeFilter.asStateFlow()

    // -----------------------------
    // UI STATE
    // -----------------------------
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // -----------------------------
    // DERIVED VALUES (FILTERED)
    // -----------------------------
    private val _filteredInvoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices = _filteredInvoices.asStateFlow()

    private val _minAmount = MutableStateFlow(0)
    val minAmount = _minAmount.asStateFlow()

    private val _maxAmount = MutableStateFlow(0)
    val maxAmount = _maxAmount.asStateFlow()

    val isFilterActive: StateFlow<Boolean> = _activeFilter
        .map { it != InvoiceFilter() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // -----------------------------
    // INIT
    // -----------------------------
    init {
        val savedMock = prefs.getBoolean(PREF_USE_MOCK, false)
        AppConfig.useMockLocal = savedMock
        _useMock.value = savedMock

        // 1) FETCH ONLY WHEN SOURCE CHANGES
        viewModelScope.launch {
            combine(
                _selectedType,
                _selectedStreet,
                _useMock
            ) { type, street, useMock ->
                Triple(type, street, useMock)
            }.collect { (type, street, useMock) ->
                loadInvoices(type, street, useMock)
            }
        }

        // 2) FILTER IN MEMORY (NO FETCH)
        viewModelScope.launch {
            combine(
                _allInvoices,
                _activeFilter
            ) { invoices, filter ->
                applyFilter(invoices, filter)
            }.collect {
                _filteredInvoices.value = it
            }
        }
    }

    // -----------------------------
    // PUBLIC ACTIONS
    // -----------------------------
    fun selectType(type: InvoiceType) {
        if (_selectedType.value != type) {
            _selectedType.value = type
        }
    }

    fun toggleMock() {
        val newValue = !_useMock.value
        AppConfig.useMockLocal = newValue
        prefs.edit { putBoolean(PREF_USE_MOCK, newValue) }
        _useMock.value = newValue
    }

    fun applyFilter(filter: InvoiceFilter) {
        _activeFilter.value = filter
    }

    fun clearFilter() {
        _activeFilter.value = InvoiceFilter()
    }

    // -----------------------------
    // BACK / RATING LOGIC
    // -----------------------------
    fun onBackPressed(): Boolean {
        val count = prefs.getInt(PREF_CLOSE_COUNT, 0) + 1
        val threshold = prefs.getInt(PREF_THRESHOLD, 1)
        prefs.edit { putInt(PREF_CLOSE_COUNT, count) }
        return count >= threshold
    }

    fun onRated() = resetSheet(10)
    fun onRespondLater() = resetSheet(3)
    fun onSheetDismissed() = resetSheet(1)

    private fun resetSheet(threshold: Int) {
        prefs.edit {
            putInt(PREF_CLOSE_COUNT, 0)
            putInt(PREF_THRESHOLD, threshold)
        }
    }

    // -----------------------------
    // FETCH ONLY HERE
    // -----------------------------
    private suspend fun loadInvoices(
        type: InvoiceType,
        street: String?,
        useMock: Boolean
    ) {
        _isLoading.value = true
        _error.value = null

        try {
            val invoices = getInvoicesUseCase(
                type,
                street,
                forceNetwork = !useMock
            )

            _allInvoices.value = invoices.sortedByDescending { it.date }

            val min = invoices.minOfOrNull { it.amount.toInt() } ?: 0
            val max = invoices.maxOfOrNull { it.amount.toInt() } ?: 0

            _minAmount.value = min
            _maxAmount.value = max

        } catch (e: Exception) {
            _allInvoices.value = emptyList()
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }


    private fun applyFilter(
        invoices: List<Invoice>,
        filter: InvoiceFilter
    ): List<Invoice> {

        return invoices.filter { invoice ->

            val invoiceDate = try {
                java.time.LocalDate.parse(invoice.date)
            } catch (e: Exception) {
                return@filter false
            }

            val fromOk =
                filter.desde == null || !invoiceDate.isBefore(filter.desde)

            val toOk =
                filter.hasta == null || !invoiceDate.isAfter(filter.hasta)

            val minOk =
                filter.importeMin == null || invoice.amount >= filter.importeMin

            val maxOk =
                filter.importeMax == null || invoice.amount <= filter.importeMax

            val statusOk =
                filter.estados.isEmpty() || invoice.status in filter.estados

            fromOk && toOk && minOk && maxOk && statusOk
        }
    }

    companion object {
        private const val PREF_USE_MOCK = "use_mock"
        private const val PREF_CLOSE_COUNT = "invoice_close_count"
        private const val PREF_THRESHOLD = "invoice_show_sheet_threshold"
    }
}