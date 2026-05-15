package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.core.utils.AnalyticsTracker
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig.RATING_THRESHOLD_DISMISSED
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig.RATING_THRESHOLD_LATER
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig.RATING_THRESHOLD_RATED
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceFilter
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val prefs: SharedPreferences,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _useMock = MutableStateFlow(
        prefs.getBoolean(PREF_USE_MOCK, AppConfig.useMockLocal)
    )
    val useMock: StateFlow<Boolean> = _useMock.asStateFlow()

    private val _selectedType = MutableStateFlow(InvoiceType.LUZ)
    val selectedType = _selectedType.asStateFlow()

    private val _selectedStreet = MutableStateFlow<String?>(AppConfig.mockStreet)
    val selectedStreet = _selectedStreet.asStateFlow()

    private val _allInvoices = MutableStateFlow<List<Invoice>>(emptyList())
    val allInvoices = _allInvoices.asStateFlow()

    private val _activeFilter = MutableStateFlow(InvoiceFilter())
    val activeFilter = _activeFilter.asStateFlow()

    private val _amountFilterByType = MutableStateFlow(
        mapOf(
            InvoiceType.LUZ to InvoiceFilter(),
            InvoiceType.GAS to InvoiceFilter()
        )
    )
    private val _filteredInvoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices = _filteredInvoices.asStateFlow()

    private val _minAmount = MutableStateFlow(0)
    val minAmount = _minAmount.asStateFlow()

    private val _maxAmount = MutableStateFlow(0)
    val maxAmount = _maxAmount.asStateFlow()


    private val _amountFilterAdjusted =
        MutableSharedFlow<AmountFilterEvent>(extraBufferCapacity = 1)
    val amountFilterAdjusted: SharedFlow<AmountFilterEvent> = _amountFilterAdjusted
    private val _isGasEnabled = MutableStateFlow(
        getInvoicesUseCase.isGasEnabled()
    )
    val isGasTabVisible: StateFlow<Boolean> = _isGasEnabled.asStateFlow()
    val isFilterActive: StateFlow<Boolean> =
        combine(_activeFilter) { filter ->
            val f = filter[0]
            f.desde != null ||
                    f.hasta != null ||
                    f.estados.isNotEmpty() ||
                    (f.importeMin != null && f.importeMin != _minAmount.value) ||
                    (f.importeMax != null && f.importeMax != _maxAmount.value)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val hasInvoicesForSelectedType: StateFlow<Boolean> =
        combine(_allInvoices, _selectedType) { invoices, type ->
            invoices.any { it.type == type }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {

        val savedMock = prefs.getBoolean(PREF_USE_MOCK, false)
        AppConfig.useMockLocal = savedMock
        _useMock.value = savedMock

        observeSourceChanges()
        observeFiltering()
    }

    private fun observeSourceChanges() {
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
    }

    private fun observeFiltering() {
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

    private val _tabSwitchAmountEvent = MutableSharedFlow<Pair<Int, Int>>(extraBufferCapacity = 1)
    val tabSwitchAmountEvent: SharedFlow<Pair<Int, Int>> = _tabSwitchAmountEvent

    fun selectType(type: InvoiceType) {
        if (_selectedType.value != type) {
            val currentFilter = _activeFilter.value
            val isAtExtremes = (currentFilter.importeMin == null || currentFilter.importeMin == _minAmount.value) &&
                    (currentFilter.importeMax == null || currentFilter.importeMax == _maxAmount.value)

            _amountFilterByType.value = _amountFilterByType.value.toMutableMap().apply {
                put(_selectedType.value, if (isAtExtremes) InvoiceFilter() else InvoiceFilter(
                    importeMin = currentFilter.importeMin,
                    importeMax = currentFilter.importeMax
                ))
            }
            val savedAmountFilter = _amountFilterByType.value[type] ?: InvoiceFilter()
            val hadAmountFilter =
                (savedAmountFilter.importeMin != null && savedAmountFilter.importeMin != _minAmount.value) ||
                        (savedAmountFilter.importeMax != null && savedAmountFilter.importeMax != _maxAmount.value)

            _activeFilter.value = currentFilter.copy(
                importeMin = savedAmountFilter.importeMin,
                importeMax = savedAmountFilter.importeMax
            )

            _selectedType.value = type

            if (hadAmountFilter) {
                _tabSwitchAmountEvent.tryEmit(
                    Pair(
                        savedAmountFilter.importeMin ?: _minAmount.value,
                        savedAmountFilter.importeMax ?: _maxAmount.value
                    )
                )
            }
        }
    }


    fun toggleMock() {
        val newValue = !_useMock.value
        AppConfig.useMockLocal = newValue
        prefs.edit { putBoolean(PREF_USE_MOCK, newValue) }
        _useMock.value = newValue
    }

    fun applyFilter(filter: InvoiceFilter) {
        val current = _activeFilter.value

        if (current == filter) return
        if (filter == InvoiceFilter() && current == InvoiceFilter()) return

        _activeFilter.value = filter
    }

    fun clearFilter() {
        _activeFilter.value = InvoiceFilter()
    }

    fun onBackPressed(): Boolean {
        val count = prefs.getInt(PREF_CLOSE_COUNT, 0) + 1
        val threshold = prefs.getInt(PREF_THRESHOLD, 1)

        prefs.edit { putInt(PREF_CLOSE_COUNT, count) }

        return count >= threshold
    }

    fun onRated() = resetSheet(RATING_THRESHOLD_RATED)
    fun onRespondLater() = resetSheet(RATING_THRESHOLD_LATER)
    fun onSheetDismissed() = resetSheet(RATING_THRESHOLD_DISMISSED)

    private fun resetSheet(threshold: Int) {
        prefs.edit {
            putInt(PREF_CLOSE_COUNT, 0)
            putInt(PREF_THRESHOLD, threshold)
        }
    }

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

            val rawMin = invoices.minOfOrNull { it.amount }?.toFloat() ?: 0f
            val rawMax = invoices.maxOfOrNull { it.amount }?.toFloat() ?: 0f

            val min = kotlin.math.floor(rawMin).toInt()
            val max = kotlin.math.ceil(rawMax).toInt().coerceAtLeast(min + 1)

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
                filter.importeMin == null ||
                        invoice.amount >= (if (filter.importeMin == filter.importeMax) filter.importeMin - 1 else filter.importeMin)

            val maxOk =
                filter.importeMax == null ||
                        invoice.amount <= (if (filter.importeMin == filter.importeMax) filter.importeMax + 1 else filter.importeMax)

            val statusOk =
                filter.estados.isEmpty() || invoice.status in filter.estados

            fromOk && toOk && minOk && maxOk && statusOk
        }
    }

    fun onAplicarFiltrosClick() {
        analyticsTracker.trackButtonClick(AnalyticsTracker.BUTTON_APLICAR_FILTROS)
    }

    fun onBorrarFiltrosClick() {
        analyticsTracker.trackButtonClick(AnalyticsTracker.BUTTON_BORRAR_FILTROS)
    }

    sealed class AmountFilterEvent {
        data class Adjusted(val newMin: Int, val newMax: Int) : AmountFilterEvent()
        object Reset : AmountFilterEvent()
    }

    companion object {
        private const val PREF_USE_MOCK = "use_mock"
        private const val PREF_CLOSE_COUNT = "invoice_close_count"
        private const val PREF_THRESHOLD = "invoice_show_sheet_threshold"
    }
}