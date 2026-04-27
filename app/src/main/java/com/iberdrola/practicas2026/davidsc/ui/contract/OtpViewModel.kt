package com.iberdrola.practicas2026.davidsc.ui.contracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OtpViewModel @Inject constructor() : ViewModel() {

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    // True when exactly OTP_LENGTH digits have been entered
    val canContinue: StateFlow<Boolean> get() = _canContinue
    private val _canContinue = MutableStateFlow(false)

    private val _remainingResends = MutableStateFlow(MAX_RESENDS)
    val remainingResends: StateFlow<Int> = _remainingResends.asStateFlow()

    // Shown briefly after a successful resend action
    private val _resendConfirmationVisible = MutableStateFlow(false)
    val resendConfirmationVisible: StateFlow<Boolean> = _resendConfirmationVisible.asStateFlow()

    fun onCodeChange(value: String) {
        if (value.length > OTP_LENGTH) return
        if (value.isNotEmpty() && !value.all { it.isDigit() }) return
        _code.value = value
        _canContinue.value = value.length == OTP_LENGTH
    }

    fun resendCode() {
        if (_remainingResends.value <= 0) return
        _remainingResends.value -= 1
        showResendConfirmation()
    }

    private fun showResendConfirmation() {
        viewModelScope.launch {
            _resendConfirmationVisible.value = true
            delay(CONFIRMATION_VISIBLE_MILLIS)
            _resendConfirmationVisible.value = false
        }
    }

    companion object {
        const val OTP_LENGTH = 6
        const val MAX_RESENDS = 3
        private const val CONFIRMATION_VISIBLE_MILLIS = 3000L
    }
}