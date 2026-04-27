package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository

/**
 * Returns the list of available contracts for the user.
 *
 * Kept as a dedicated use case to maintain layer separation
 * and allow future filtering or enrichment without modifying the repository.
 */
class GetContractsUseCase(
    private val repository: ContractRepository
) {
    suspend operator fun invoke(): List<Contract> = repository.getContracts()
}