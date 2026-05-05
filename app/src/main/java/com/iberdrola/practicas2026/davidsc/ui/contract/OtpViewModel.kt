package com.iberdrola.practicas2026.davidsc.ui.contract

import android.content.SharedPreferences
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
class OtpViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    init {
        otpBlockedUntil = sharedPreferences.getLong(PREF_KEY_OTP_BLOCK, 0L)
        resetIfExpired()
    }
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
    private val _isResending = MutableStateFlow(false)
    val isResending: StateFlow<Boolean> = _isResending.asStateFlow()
    fun onCodeChange(value: String) {
        if (value.length > OTP_LENGTH) return
        if (value.isNotEmpty() && !value.all { it.isDigit() }) return
        _code.value = value
        _canContinue.value = value.length == OTP_LENGTH
    }

    fun resendCode() {
        if (_remainingResends.value <= 0) return

        _remainingResends.value -= 1

        if (_remainingResends.value == 0) {
            otpBlockedUntil = System.currentTimeMillis() + 24 * 60 * 60 * 1000L

            sharedPreferences.edit()
                .putLong(PREF_KEY_OTP_BLOCK, otpBlockedUntil)
                .apply()
        }

        viewModelScope.launch {
            _isResending.value = true
            delay(RESEND_LOADING_MILLIS)
            _isResending.value = false
            showResendConfirmation()
        }
    }



    private fun resetIfExpired() {
        if (otpBlockedUntil != 0L &&
            System.currentTimeMillis() >= otpBlockedUntil
        ) {
            _remainingResends.value = MAX_RESENDS
            otpBlockedUntil = 0L

            sharedPreferences.edit()
                .remove(PREF_KEY_OTP_BLOCK)
                .apply()
        }
    }

    private fun showResendConfirmation() {
        viewModelScope.launch {
            _resendConfirmationVisible.value = true
            //delay(CONFIRMATION_VISIBLE_MILLIS)
            //_resendConfirmationVisible.value = false
        }
    }
    fun hideResendConfirmation() {
        _resendConfirmationVisible.value = false
    }

    fun isOtpBlocked(): Boolean {
        return System.currentTimeMillis() < otpBlockedUntil
    }
    companion object {
        const val OTP_LENGTH = 6
        const val MAX_RESENDS = 3
        private const val RESEND_LOADING_MILLIS = 1500L
        private const val CONFIRMATION_VISIBLE_MILLIS = 3000L
        private var otpBlockedUntil: Long = 0L

        private val PREF_KEY_OTP_BLOCK = "otp_block_until"
    }


}