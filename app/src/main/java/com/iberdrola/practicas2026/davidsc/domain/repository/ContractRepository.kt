package com.iberdrola.practicas2026.davidsc.domain.repository

import com.iberdrola.practicas2026.davidsc.domain.model.Contract

interface ContractRepository {
    suspend fun getContracts(): List<Contract>
}