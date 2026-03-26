package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import kotlinx.coroutines.flow.combine
@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _useMock = MutableStateFlow(
        prefs.getBoolean("use_mock", AppConfig.useMockLocal)
    )
    val useMock: StateFlow<Boolean> = _useMock.asStateFlow()

    private val _selectedType = MutableStateFlow(InvoiceType.LUZ)
    val selectedType: StateFlow<InvoiceType> = _selectedType.asStateFlow()

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            _selectedType.combine(_useMock) { type, mock -> type to mock }
                .collect { (type, mock) ->
                    loadInvoices(type, mock)
                }
        }
    }

    fun selectType(type: InvoiceType) {
        if (_selectedType.value != type) {
            viewModelScope.launch {
                _isLoading.value = true
                _selectedType.value = type

                // delay(300)       // Optional: delay mínimo para ver el skeleton
            }
        }
    }

    fun toggleMock() {
        val newValue = !_useMock.value
        viewModelScope.launch {
            _isLoading.value = true                  // 🔹 Skeleton activo
            _useMock.value = newValue
            prefs.edit { putBoolean("use_mock", newValue) }
            AppConfig.useMockLocal = newValue
            // combine recogerá el cambio y llamará a loadInvoices
        }
    }

    // Bottom sheet
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

    companion object {
        private const val PREF_CLOSE_COUNT = "invoice_close_count"
        private const val PREF_THRESHOLD = "invoice_show_sheet_threshold"
    }

    private suspend fun loadInvoices(type: InvoiceType, useMock: Boolean) {
        _isLoading.value = true
        try {
            _invoices.value = getInvoicesUseCase(type, useMock)
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }
}