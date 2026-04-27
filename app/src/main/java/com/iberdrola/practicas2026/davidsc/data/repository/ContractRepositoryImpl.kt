package com.iberdrola.practicas2026.davidsc.data.repository

import android.content.Context
import com.google.gson.Gson
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomainList
import com.iberdrola.practicas2026.davidsc.data.remote.dto.ContractDto
import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository

/**
 * Loads contract data from a local JSON asset.
 *
 * Contracts are static for the duration of the practice — no remote endpoint
 * exists, so the repository always reads from the bundled mock file.
 * No delay is simulated here because the contract selection screen is not
 * a data-intensive screen and a fake delay would hurt usability.
 */
class ContractRepositoryImpl(
    private val context: Context
) : ContractRepository {

    override suspend fun getContracts(): List<Contract> {
        val json = context.assets
            .open(CONTRACTS_MOCK_FILE)
            .bufferedReader()
            .use { it.readText() }

        return Gson()
            .fromJson(json, Array<ContractDto>::class.java)
            .toList()
            .toDomainList()
    }

    companion object {
        private const val CONTRACTS_MOCK_FILE = "contracts_mock.json"
    }
}