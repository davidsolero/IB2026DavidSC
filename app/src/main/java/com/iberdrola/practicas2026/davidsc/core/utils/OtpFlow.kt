package com.iberdrola.practicas2026.davidsc.core.utils

/**
 * Constants identifying which electronic invoice flow the user is following.
 *
 * Passed as a route argument so [OtpVerificationScreen] and [ConfirmationScreen]
 * can adapt their copy without requiring a shared ViewModel.
 */
object OtpFlow {
    const val ACTIVATE = "activate"
    const val MODIFY = "modify"
}