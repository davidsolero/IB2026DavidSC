package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
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

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _invoices.value = getInvoicesUseCase()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

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