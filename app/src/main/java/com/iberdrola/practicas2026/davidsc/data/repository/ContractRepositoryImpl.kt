package com.iberdrola.practicas2026.davidsc.data.repository

import android.content.Context
import com.google.gson.Gson
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomainList
import com.iberdrola.practicas2026.davidsc.data.remote.dto.ContractDto
import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository

class ContractRepositoryImpl(
    private val context: Context
) : ContractRepository {

    private var cachedContracts: MutableList<Contract>? = null

    override suspend fun getContracts(): List<Contract> {
        cachedContracts?.let { return it }

        val json = context.assets
            .open(CONTRACTS_MOCK_FILE)
            .bufferedReader()
            .use { it.readText() }

        val loaded = Gson()
            .fromJson(json, Array<ContractDto>::class.java)
            .toList()
            .toDomainList()
            .toMutableList()

        cachedContracts = loaded
        return loaded
    }

    override suspend fun updateContractEmail(contractId: String, newEmail: String) {
        val cache = cachedContracts ?: return
        val index = cache.indexOfFirst { it.id == contractId }
        if (index == -1) return
        cache[index] = cache[index].copy(email = newEmail)
    }

    companion object {
        private const val CONTRACTS_MOCK_FILE = "contracts_mock.json"
    }
}