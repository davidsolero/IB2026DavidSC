package com.iberdrola.practicas2026.davidsc.data.mapper

import com.iberdrola.practicas2026.davidsc.data.remote.dto.ContractDto
import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.model.ContractType

fun ContractDto.toDomainOrNull(): Contract? {
    val id = id ?: return null
    val type = type?.toContractTypeOrNull() ?: return null
    val isActive = isActive ?: return null

    return Contract(
        id = id,
        type = type,
        isActive = isActive,
        email = email.orEmpty(),
        address = address.orEmpty(),
        holder = holder.orEmpty()
    )
}

fun List<ContractDto>.toDomainList(): List<Contract> = mapNotNull { it.toDomainOrNull() }

fun String.toContractTypeOrNull(): ContractType? = when (this.lowercase()) {
    "luz" -> ContractType.LUZ
    "gas" -> ContractType.GAS
    else -> null
}