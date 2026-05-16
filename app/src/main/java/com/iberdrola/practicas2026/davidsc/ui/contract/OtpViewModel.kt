package com.iberdrola.practicas2026.davidsc.ui.contract

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.core.utils.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {
    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    private val _canContinue = MutableStateFlow(false)
    val canContinue: StateFlow<Boolean> = _canContinue.asStateFlow()

    private val _remainingResends = MutableStateFlow(MAX_RESENDS)
    val remainingResends: StateFlow<Int> = _remainingResends.asStateFlow()

    private val _resendConfirmationVisible = MutableStateFlow(false)
    val resendConfirmationVisible: StateFlow<Boolean> =
        _resendConfirmationVisible.asStateFlow()

    private val _isResending = MutableStateFlow(false)
    val isResending: StateFlow<Boolean> = _isResending.asStateFlow()

    private val _isOtpBlocked = MutableStateFlow(false)
    val isOtpBlocked: StateFlow<Boolean> = _isOtpBlocked.asStateFlow()

    private val _remainingTime = MutableStateFlow(0L)
    val remainingTime: StateFlow<Long> = _remainingTime.asStateFlow()

    private var otpBlockedUntil: Long = 0L
    private var countdownJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            otpBlockedUntil = sharedPreferences.getLong(PREF_KEY_OTP_BLOCK, 0L)

            resetIfExpired()
            updateBlockedState()

            if (isBlockedNow()) {
                startCountdown()
            }
        }
    }

    fun onCodeChange(value: String) {
        if (value.length > OTP_LENGTH) return
        if (value.isNotEmpty() && !value.all { it.isDigit() }) return

        _code.value = value
        _canContinue.value = value.length == OTP_LENGTH
    }

    fun resendCode() {
        analyticsTracker.trackButtonClick(AnalyticsTracker.BUTTON_REENVIAR_OTP)
        updateBlockedState()

        if (_remainingResends.value <= 0 || isBlockedNow()) return

        _remainingResends.value -= 1

        if (_remainingResends.value == 0) {
            otpBlockedUntil = System.currentTimeMillis() + OTP_BLOCK_DURATION

            sharedPreferences.edit()
                .putLong(PREF_KEY_OTP_BLOCK, otpBlockedUntil)
                .apply()

            startCountdown()
        }

        viewModelScope.launch {
            _isResending.value = true
            delay(RESEND_LOADING_MILLIS)
            _isResending.value = false
            _resendConfirmationVisible.value = true
        }

        updateBlockedState()
    }

    fun hideResendConfirmation() {
        _resendConfirmationVisible.value = false
    }

    private fun startCountdown() {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            while (true) {
                val diff = otpBlockedUntil - System.currentTimeMillis()

                if (diff <= 0) {
                    _remainingTime.value = 0L

                    otpBlockedUntil = 0L
                    sharedPreferences.edit()
                        .remove(PREF_KEY_OTP_BLOCK)
                        .apply()

                    _remainingResends.value = MAX_RESENDS
                    _isOtpBlocked.value = false

                    break
                }

                _remainingTime.value = diff
                _isOtpBlocked.value = true

                delay(1000)
            }
        }
    }

    private fun resetIfExpired() {
        if (otpBlockedUntil != 0L &&
            System.currentTimeMillis() >= otpBlockedUntil
        ) {
            otpBlockedUntil = 0L
            _remainingResends.value = MAX_RESENDS

            sharedPreferences.edit()
                .remove(PREF_KEY_OTP_BLOCK)
                .apply()
        }
    }

    private fun updateBlockedState() {
        _isOtpBlocked.value = isBlockedNow()
    }

    private fun isBlockedNow(): Boolean {
        return System.currentTimeMillis() < otpBlockedUntil
    }

    companion object {
        const val OTP_LENGTH = 6
        const val MAX_RESENDS = 3

        private const val RESEND_LOADING_MILLIS = 1500L

        private val OTP_BLOCK_DURATION = 24 * 60 * 60 * 1000L

        private const val PREF_KEY_OTP_BLOCK = "otp_block_until"
    }
}