package com.iberdrola.practicas2026.davidsc.data.mapper

import com.iberdrola.practicas2026.davidsc.data.local.entity.InvoiceEntity
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice

fun InvoiceDto.toDomain(): Invoice {
    return Invoice(
        id = id,
        date = date,
        description = description,
        amount = amount,
        status = status
    )
}

fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = id,
        date = date,
        description = description,
        amount = amount,
        status = status
    )
}

fun Invoice.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id,
        date = date,
        description = description,
        amount = amount,
        status = status
    )
}