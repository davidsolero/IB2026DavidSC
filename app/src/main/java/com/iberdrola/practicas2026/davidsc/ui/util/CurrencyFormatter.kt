package com.iberdrola.practicas2026.davidsc.ui.util

import java.text.NumberFormat
import java.util.Locale

class CurrencyFormatter {

    private val formatter = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

    fun format(amount: Double): String {
        return formatter.format(amount)
    }
}