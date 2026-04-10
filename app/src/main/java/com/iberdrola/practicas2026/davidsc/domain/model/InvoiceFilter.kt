package com.iberdrola.practicas2026.davidsc.domain.model

import java.time.LocalDate

/**
 * Represents the active filter criteria for the invoice list.
 *
 * A null value or empty set means that criterion is not applied.
 * Default construction produces a filter with no restrictions.
 */
data class InvoiceFilter(
    val desde: LocalDate? = null,
    val hasta: LocalDate? = null,
    val importeMin: Double? = null,
    val importeMax: Double? = null,
    val estados: Set<String> = emptySet()
)