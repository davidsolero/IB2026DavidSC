package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository

class UpdateContractEmailUseCase(
    private val repository: ContractRepository
) {
    suspend operator fun invoke(contractId: String, newEmail: String) {
        repository.updateContractEmail(contractId, newEmail)
    }
}
