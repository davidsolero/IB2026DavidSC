package com.iberdrola.practicas2026.davidsc.data.mapper

import android.R.attr.type
import com.iberdrola.practicas2026.davidsc.data.local.entity.InvoiceEntity
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType

fun InvoiceDto.toDomain(): Invoice {
    val inferredType = type.toInvoiceType()
    return Invoice(
        id = id,
        date = date,
        description = description,
        amount = amount,
        status = status,
        type = inferredType
    )
}

fun Invoice.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id,
        date = date,
        description = description,
        amount = amount,
        status = status,
        type = type.name
    )
}

fun String.toInvoiceType(): InvoiceType {
    return when (this.lowercase()) {
        "luz" -> InvoiceType.LUZ
        "gas" -> InvoiceType.GAS
        else -> throw IllegalArgumentException("Unknown type: $this")
    }
}

fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = id,
        date = date,
        description = description,
        amount = amount,
        status = status,
        type = type.toInvoiceType() // usa la función que ya tienes
    )
}