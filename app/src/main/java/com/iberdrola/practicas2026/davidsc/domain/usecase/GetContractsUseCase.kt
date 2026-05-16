package com.iberdrola.practicas2026.davidsc.domain.usecase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.model.ContractType
import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository

class GetContractsUseCase(
    private val repository: ContractRepository,
    private val remoteConfig: FirebaseRemoteConfig
) {
    suspend operator fun invoke(): List<Contract> {
        val gasEnabled = remoteConfig.getBoolean(KEY_GAS_ENABLED)
        return repository.getContracts()
            .filter { it.type != ContractType.GAS || gasEnabled }
    }

    companion object {
        const val KEY_GAS_ENABLED = "gas_contracts_enabled"
    }
}