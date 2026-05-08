package com.iberdrola.practicas2026.davidsc.data.repository

import android.content.Context
import com.google.gson.Gson
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig.MOCK_DELAY_MAX_MS
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig.MOCK_DELAY_MIN_MS
import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomain
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomainList
import com.iberdrola.practicas2026.davidsc.data.mapper.toEntity
import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import kotlinx.coroutines.delay
import kotlin.random.Random

class InvoiceRepositoryImpl(
    private val api: InvoiceApi,
    private val dao: InvoiceDao,
    private val context: Context
) : InvoiceRepository {

    override suspend fun getInvoices(): List<Invoice> {
        return if (AppConfig.useMockLocal) {
            loadFromLocalMock()
        } else {
            loadFromRemoteWithFallback()
        }
    }

    private suspend fun loadFromLocalMock(): List<Invoice> {
        delay(Random.nextLong(MOCK_DELAY_MIN_MS, MOCK_DELAY_MAX_MS))

        val json = context.assets.open("invoices.json")
            .bufferedReader()
            .use { it.readText() }

        return Gson()
            .fromJson(json, Array<InvoiceDto>::class.java)
            .toList()
            .toDomainList()
    }

    private suspend fun loadFromRemoteWithFallback(): List<Invoice> {
        return try {
            fetchInvoicesFromNetwork()
        } catch (e: Exception) {
            dao.getInvoices().map { it.toDomain() }
        }
    }

    override suspend fun fetchInvoicesFromNetwork(): List<Invoice> {
        val response = api.getInvoices()
        val invoices = response.facturas.toDomainList()
        dao.clearInvoices()
        dao.insertInvoices(invoices.map { it.toEntity() })
        return invoices
    }
}