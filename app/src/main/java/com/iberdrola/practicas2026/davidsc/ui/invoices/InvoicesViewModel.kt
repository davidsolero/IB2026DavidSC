package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceFilter
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import androidx.core.content.edit

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _useMock = MutableStateFlow(prefs.getBoolean(PREF_USE_MOCK, AppConfig.useMockLocal))
    val useMock: StateFlow<Boolean> = _useMock.asStateFlow()

    private val _selectedType = MutableStateFlow(InvoiceType.LUZ)
    val selectedType: StateFlow<InvoiceType> = _selectedType.asStateFlow()

    private val _selectedStreet = MutableStateFlow<String?>(AppConfig.mockStreet)
    val selectedStreet: StateFlow<String?> = _selectedStreet.asStateFlow()

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _activeFilter = MutableStateFlow(InvoiceFilter())
    val activeFilter: StateFlow<InvoiceFilter> = _activeFilter.asStateFlow()

    // Derived from all loaded invoices regardless of active filter,
    // so the slider always shows the full range of available amounts.
    private val _allInvoices = MutableStateFlow<List<Invoice>>(emptyList())

    private val _minAmount = MutableStateFlow(0.0)
    val minAmount: StateFlow<Double> = _minAmount.asStateFlow()

    private val _maxAmount = MutableStateFlow(0.0)
    val maxAmount: StateFlow<Double> = _maxAmount.asStateFlow()
    init {
        val savedMock = prefs.getBoolean(PREF_USE_MOCK, false)
        AppConfig.useMockLocal = savedMock
        _useMock.value = savedMock

        viewModelScope.launch {
            combine(
                _selectedType,
                _selectedStreet,
                _useMock,
                _activeFilter
            ) { values ->
                val type = values[0] as InvoiceType
                val street = values[1] as String?
                val filter = values[3] as InvoiceFilter
                Triple(type, street, filter)
            }.collect { (type, street, filter) ->
                android.util.Log.d("InvoicesViewModel", "collect triggered, filter: $filter")
                loadInvoices(type, street, filter)
            }
        }
    }

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
        android.util.Log.d("InvoicesViewModel", "Filter applied: $filter")
    }

    fun clearFilter() {
        _activeFilter.value = InvoiceFilter()
    }

    /**
     * Called when the user navigates back from the invoices screen.
     * Returns true when the rating sheet should be shown based on the configured threshold.
     */
    fun onBackPressed(): Boolean {
        val count = prefs.getInt(PREF_CLOSE_COUNT, 0) + 1
        val threshold = prefs.getInt(PREF_THRESHOLD, 1)
        prefs.edit { putInt(PREF_CLOSE_COUNT, count) }
        return count >= threshold
    }

    fun onRated() = resetSheet(threshold = 10)

    fun onRespondLater() = resetSheet(threshold = 3)

    fun onSheetDismissed() = resetSheet(threshold = 1)

    private fun resetSheet(threshold: Int) {
        prefs.edit {
            putInt(PREF_CLOSE_COUNT, 0)
            putInt(PREF_THRESHOLD, threshold)
        }
    }

    private suspend fun loadInvoices(type: InvoiceType, street: String?, filter: InvoiceFilter) {
        android.util.Log.d("InvoicesViewModel", "loadInvoices called with filter: $filter")
        _isLoading.value = true
        _error.value = null
        try {
            // Fetch unfiltered to keep the slider range stable across filter changes.
            _allInvoices.value = getInvoicesUseCase(type, street, forceNetwork = !AppConfig.useMockLocal)
            _minAmount.value = _allInvoices.value.minOfOrNull { it.amount } ?: 0.0
            _maxAmount.value = _allInvoices.value.maxOfOrNull { it.amount } ?: 0.0
            _invoices.value = getInvoicesUseCase(type, street, forceNetwork = false, filter = filter)
        } catch (e: Exception) {
            _invoices.value = emptyList()
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    companion object {
        private const val PREF_USE_MOCK = "use_mock"
        private const val PREF_CLOSE_COUNT = "invoice_close_count"
        private const val PREF_THRESHOLD = "invoice_show_sheet_threshold"
    }



}