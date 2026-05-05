package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository

/**
 * Updates the email address linked to an electronic billing contract.
 *
 * Keeping this as a dedicated use case respects the Single Responsibility
 * Principle: [GetContractsUseCase] queries, this use case mutates.
 */
class UpdateContractEmailUseCase(
    private val repository: ContractRepository
) {
    suspend operator fun invoke(contractId: String, newEmail: String) {
        repository.updateContractEmail(contractId, newEmail)
    }
}
