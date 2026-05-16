package com.iberdrola.practicas2026.davidsc.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateFormatter {

    private val inputFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val compactFormatter =
        DateTimeFormatter.ofPattern("dd MMM. yyyy", Locale("es", "ES"))

    private val longFormatter =
        DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))

    fun formatInvoiceDate(
        date: String,
        useCompactFormat: Boolean = false
    ): String {

        val parsed = LocalDate.parse(date, inputFormatter)

        return if (useCompactFormat) {
            parsed.format(compactFormatter)
        } else {
            parsed.format(longFormatter)
        }
    }


    fun formatCompact(date: String): String {
        val parsed = LocalDate.parse(date, inputFormatter)
        return parsed.format(compactFormatter)
    }

    fun formatRange(first: String, last: String): String {
        return "${formatCompact(first)} - ${formatCompact(last)}"
    }
}