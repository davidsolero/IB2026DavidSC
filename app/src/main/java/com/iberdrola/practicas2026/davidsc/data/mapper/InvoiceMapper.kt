package com.iberdrola.practicas2026.davidsc.data.mapper

import com.iberdrola.practicas2026.davidsc.data.local.entity.InvoiceEntity
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType

fun InvoiceDto.toDomain(): Invoice {
    val inferredType = type.toInvoiceType()
    return Invoice(
        id = id,
        startDate = startDate,
        endDate = endDate,
        description = description,
        amount = amount,
        status = status,
        type = inferredType,
        street = street
    )
}

fun Invoice.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id,
        startDate = startDate,
        endDate = endDate,
        description = description,
        amount = amount,
        status = status,
        type = type.name,
        street = street
    )
}

fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = id,
        startDate = startDate,
        endDate = endDate,
        description = description,
        amount = amount,
        status = status,
        type = type.toInvoiceType(),
        street = street
    )
}


/**
 * Converts a raw string from the API or database to an [InvoiceType].
 * Throws [IllegalArgumentException] if the value is not a recognized type.
 * This is intentional — unknown types indicate a data contract violation.
 */
fun String.toInvoiceType(): InvoiceType {
    return when (this.lowercase()) {
        "luz" -> InvoiceType.LUZ
        "gas" -> InvoiceType.GAS
        else -> throw IllegalArgumentException("Unknown type: $this")
    }
}

