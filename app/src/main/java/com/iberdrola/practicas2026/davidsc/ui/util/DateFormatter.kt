package com.iberdrola.practicas2026.davidsc.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateFormatter {


    private val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val rangeFormatter =
        DateTimeFormatter.ofPattern("dd MMM. yyyy", Locale("es", "ES"))

    private val singleFormatter =
        DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))

    /**
     * Formats an invoice date range or single date for display.
     *
     * @param startDate String with format "yyyy-MM-dd"
     * @param endDate String with format "yyyy-MM-dd"
     * @param showEndDate Whether to show range or single date
     */
    fun formatInvoiceDate(
        startDate: String,
        endDate: String?,
        showEndDate: Boolean = false
    ): String {
        val start = LocalDate.parse(startDate, inputFormatter)

        return if (showEndDate && endDate != null) {
            val end = LocalDate.parse(endDate, inputFormatter)
            "${start.format(rangeFormatter)} - ${end.format(rangeFormatter)}"
        } else {
            start.format(singleFormatter)
        }
    }
}