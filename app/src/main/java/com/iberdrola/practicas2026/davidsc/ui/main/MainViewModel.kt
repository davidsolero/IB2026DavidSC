package com.iberdrola.practicas2026.davidsc.ui.main

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.core.utils.AnalyticsTracker
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetStreetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStreetsUseCase: GetStreetsUseCase,
    private val prefs: SharedPreferences,
    private val analyticsTracker: AnalyticsTracker,
    @Named("io") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _streets = MutableStateFlow<List<String>>(emptyList())
    val streets: StateFlow<List<String>> = _streets.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _useMock = MutableStateFlow(prefs.getBoolean(PREF_USE_MOCK, AppConfig.useMockLocal))
    val useMock: StateFlow<Boolean> = _useMock.asStateFlow()

    fun loadStreets() {
        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            try {
                _streets.value = getStreetsUseCase()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleMock() {
        val newValue = !_useMock.value
        AppConfig.useMockLocal = newValue
        prefs.edit { putBoolean(PREF_USE_MOCK, newValue) }
        _useMock.value = newValue
        analyticsTracker.trackButtonClick(AnalyticsTracker.BUTTON_TOGGLE_MOCK)
    }

    fun onVerTodasFacturasClick() {
        analyticsTracker.trackButtonClick(AnalyticsTracker.BUTTON_VER_TODAS_FACTURAS)
    }

    fun onVerFacturasCalleClick() {
        analyticsTracker.trackButtonClick(AnalyticsTracker.BUTTON_VER_FACTURAS_CALLE)
    }

    fun onGestionarFacturaClick() {
        analyticsTracker.trackButtonClick(AnalyticsTracker.BUTTON_GESTIONAR_FACTURA)
    }


    fun forceCrash() {
        analyticsTracker.trackButtonClick(AnalyticsTracker.BUTTON_FORZAR_CRASH)
        throw RuntimeException("Crash forzado para prueba de Crashlytics")
    }
    companion object {
        private const val PREF_USE_MOCK = "use_mock"
    }
}