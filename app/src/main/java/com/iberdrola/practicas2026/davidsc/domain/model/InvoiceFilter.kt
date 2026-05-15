package com.iberdrola.practicas2026.davidsc.domain.model

import java.time.LocalDate


data class InvoiceFilter(
    val desde: LocalDate? = null,
    val hasta: LocalDate? = null,
    val importeMin: Int? = null,
    val importeMax: Int? = null,
    val estados: Set<String> = emptySet()
)