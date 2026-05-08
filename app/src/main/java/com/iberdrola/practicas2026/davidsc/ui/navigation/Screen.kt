package com.iberdrola.practicas2026.davidsc.ui.navigation

import java.net.URLEncoder

object Screen {
    const val MAIN = "main_screen"
    const val INVOICES = "invoices_screen"
    const val FILTER = "filter_screen"

    const val CONTRACT_SELECTION = "contract_selection"

    const val ACTIVE_CONTRACT = "active_contract/{contractId}"
    const val ACTIVATE_CONTRACT = "activate_contract/{contractId}"
    const val MODIFY_EMAIL = "modify_email/{contractId}/{currentEmail}"

    const val OTP_VERIFICATION = "otp_verification/{email}/{flow}"
    const val CONFIRMATION = "confirmation/{flow}/{email}"


    fun activeContract(contractId: String) =
        "active_contract/$contractId"

    fun activateContract(contractId: String) =
        "activate_contract/$contractId"

    fun modifyEmail(contractId: String, currentEmail: String) =
        "modify_email/$contractId/${currentEmail.encodeForRoute()}"

    fun otpVerification(email: String, flow: String) =
        "otp_verification/${email.encodeForRoute()}/$flow"

    fun confirmation(flow: String, email: String) =
        "confirmation/$flow/${email.encodeForRoute()}"

    private fun String.encodeForRoute(): String =
        URLEncoder.encode(this, "UTF-8")
}