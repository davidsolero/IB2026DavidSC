package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.SharedPreferences
import android.util.Log
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

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val prefs: SharedPreferences
) : ViewModel() {

    // Tipo seleccionado (Luz / Gas)
    private val _selectedType = MutableStateFlow(InvoiceType.LUZ)
    val selectedType: StateFlow<InvoiceType> = _selectedType.asStateFlow()

    // Lista de facturas
    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    // Estados UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        observeInvoices()
    }

    private fun observeInvoices() {
        viewModelScope.launch {
            _selectedType.collect { type ->
                _isLoading.value = true
                try {
                    val fetched = getInvoicesUseCase(type)
                    Log.d("InvoicesVM", "Fetched invoices (raw): $fetched")
                    _invoices.value = fetched
                    Log.d("InvoicesVM", "Invoices loaded for $type: ${_invoices.value}")
                } catch (e: Exception) {
                    _error.value = e.message
                    Log.e("InvoicesVM", "Error fetching invoices", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    // 🔹 Cambiar entre Luz y Gas
    fun selectType(type: InvoiceType) {
        _selectedType.value = type
    }

    // 🔹 Lógica del bottom sheet
    fun onBackPressed(): Boolean {
        val count = prefs.getInt(PREF_CLOSE_COUNT, 0) + 1
        val threshold = prefs.getInt(PREF_THRESHOLD, 1)
        prefs.edit { putInt(PREF_CLOSE_COUNT, count) }
        return count >= threshold
    }

    fun onRated() {
        prefs.edit {
            putInt(PREF_CLOSE_COUNT, 0)
                .putInt(PREF_THRESHOLD, 10)
        }
    }

    fun onRespondLater() {
        prefs.edit {
            putInt(PREF_CLOSE_COUNT, 0)
                .putInt(PREF_THRESHOLD, 3)
        }
    }

    fun onSheetDismissed() {
        prefs.edit {
            putInt(PREF_CLOSE_COUNT, 0)
                .putInt(PREF_THRESHOLD, 1)
        }
    }

    companion object {
        private const val PREF_CLOSE_COUNT = "invoice_close_count"
        private const val PREF_THRESHOLD = "invoice_show_sheet_threshold"
    }


}