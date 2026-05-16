package com.iberdrola.practicas2026.davidsc.data.mapper

import com.iberdrola.practicas2026.davidsc.data.local.entity.InvoiceEntity
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType

fun InvoiceDto.toDomainOrNull(): Invoice? {
    val id = id ?: return null
    val date = emissionDate ?: return null
    val type = type?.toInvoiceTypeOrNull() ?: return null

    return Invoice(
        id = id,
        date = date,
        description = description ?: "",
        amount = amount ?: 0.0,
        status = status ?: "Desconocido",
        type = type,
        street = street ?: ""
    )
}

fun List<InvoiceDto>.toDomainList(): List<Invoice> =
    mapNotNull { it.toDomainOrNull() }

fun Invoice.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id,
        date = date,
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
        date = date,
        description = description,
        amount = amount,
        status = status,
        type = type.toInvoiceType(),
        street = street
    )
}

fun String.toInvoiceTypeOrNull(): InvoiceType? {
    return when (this.lowercase()) {
        "luz" -> InvoiceType.LUZ
        "gas" -> InvoiceType.GAS
        else -> null
    }
}

fun String.toInvoiceType(): InvoiceType {
    return when (this.lowercase()) {
        "luz" -> InvoiceType.LUZ
        "gas" -> InvoiceType.GAS
        else -> throw IllegalArgumentException("Unknown type: $this")
    }

}

