package com.iberdrola.practicas2026.davidsc.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomain
import com.iberdrola.practicas2026.davidsc.data.mapper.toEntity
import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoicesResponse
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

    // Simulates a loading delay to replicate real network behavior in local mock mode.
    private suspend fun loadFromLocalMock(): List<Invoice> {
        delay(Random.nextLong(1000L, 3001L))

        val json = context.assets.open("invoices.json")
            .bufferedReader()
            .use { it.readText() }

        return Gson()
            .fromJson(json, Array<InvoiceDto>::class.java)
            .map { it.toDomain() }
    }

    private suspend fun loadFromRemoteWithFallback(): List<Invoice> {
        return try {
            val response: InvoicesResponse = api.getInvoices()
            val invoices = response.facturas.map { it.toDomain() }

            // Cache results for offline access.
            dao.insertInvoices(invoices.map { it.toEntity() })

            invoices
        } catch (e: Exception) {
            Log.e("InvoiceRepositoryImpl", "Remote fetch failed, loading from cache", e)
            dao.getInvoices().map { it.toDomain() }
        }
    }
}