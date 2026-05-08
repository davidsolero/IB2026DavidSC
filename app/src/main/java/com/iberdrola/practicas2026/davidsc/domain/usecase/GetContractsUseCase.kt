package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository

class GetContractsUseCase(
    private val repository: ContractRepository
) {
    suspend operator fun invoke(): List<Contract> = repository.getContracts()
}