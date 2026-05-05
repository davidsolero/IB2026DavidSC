package com.iberdrola.practicas2026.davidsc.data.repository

import android.content.Context
import com.google.gson.Gson
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomainList
import com.iberdrola.practicas2026.davidsc.data.remote.dto.ContractDto
import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository

/**
 * Loads contract data from a local JSON asset and keeps a mutable in-memory
 * copy so that session-scoped changes (e.g. email modification) are reflected
 * without requiring a real backend.
 *
 * The cache is populated on the first [getContracts] call and reused on
 * subsequent calls. Mutations via [updateContractEmail] affect only the cached
 * copy; the asset file is never written.
 */
class ContractRepositoryImpl(
    private val context: Context
) : ContractRepository {

    // Null until the first load; populated lazily to avoid disk I/O on init.
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