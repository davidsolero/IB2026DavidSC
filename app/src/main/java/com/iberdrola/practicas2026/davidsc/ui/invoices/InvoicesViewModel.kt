package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    init {
        val savedMock = prefs.getBoolean(PREF_USE_MOCK, false)
        AppConfig.useMockLocal = savedMock
        _useMock.value = savedMock

        viewModelScope.launch {
            combine(_selectedType, _selectedStreet, _useMock) { type, street, _ ->
                Pair(type, street)
            }.collect { (type, street) ->
                loadInvoices(type, street)
            }
        }
    }

    fun selectType(type: InvoiceType) {
        if (_selectedType.value != type) {
            _selectedType.value = type
        }
    }

    fun selectStreet(street: String?) {
        if (_selectedStreet.value != street) {
            _selectedStreet.value = street
        }
    }

    fun toggleMock() {
        val newValue = !_useMock.value
        AppConfig.useMockLocal = newValue
        prefs.edit { putBoolean(PREF_USE_MOCK, newValue) }
        _useMock.value = newValue
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

    /**
     * Formats an invoice date range or single date for display.
     * If [showEndDate] is true, returns a range formatted as "dd MMM. yyyy - dd MMM. yyyy".
     * Otherwise returns the start date formatted as "d de MMMM".
     */
    fun formatInvoiceDate(invoice: Invoice, showEndDate: Boolean = false): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = LocalDate.parse(invoice.startDate, formatter)
        return if (showEndDate) {
            val end = LocalDate.parse(invoice.endDate, formatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMM. yyyy", Locale("es", "ES"))
            "${start.format(outputFormatter)} - ${end.format(outputFormatter)}"
        } else {
            start.format(DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES")))
        }
    }

    private fun resetSheet(threshold: Int) {
        prefs.edit {
            putInt(PREF_CLOSE_COUNT, 0)
            putInt(PREF_THRESHOLD, threshold)
        }
    }

    private suspend fun loadInvoices(type: InvoiceType, street: String?) {
        _isLoading.value = true
        try {
            _invoices.value = getInvoicesUseCase(type, street)
        } catch (e: Exception) {
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