package com.iberdrola.practicas2026.davidsc.core.utils

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(
    private val analytics: FirebaseAnalytics
) {

    fun trackScreenView(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }

    fun trackButtonClick(buttonName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
            param(FirebaseAnalytics.Param.ITEM_ID, buttonName)
        }
    }

    companion object {
        const val BUTTON_VER_TODAS_FACTURAS = "ver_todas_facturas"
        const val BUTTON_VER_FACTURAS_CALLE = "ver_facturas_calle"
        const val BUTTON_GESTIONAR_FACTURA = "gestionar_factura_electronica"
        const val BUTTON_TOGGLE_MOCK = "toggle_mock"
        const val BUTTON_APLICAR_FILTROS = "aplicar_filtros"
        const val BUTTON_BORRAR_FILTROS = "borrar_filtros"
        const val BUTTON_MODIFICAR_EMAIL = "modificar_email"
        const val BUTTON_REENVIAR_OTP = "reenviar_otp"
        const val BUTTON_FORZAR_CRASH = "forzar_crash"
    }
}