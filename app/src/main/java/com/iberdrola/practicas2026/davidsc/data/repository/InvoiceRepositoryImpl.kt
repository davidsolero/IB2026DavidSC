package com.iberdrola.practicas2026.davidsc.data.repository

import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomain
import com.iberdrola.practicas2026.davidsc.data.mapper.toEntity
import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import kotlinx.coroutines.delay
import kotlin.random.Random
import android.content.Context
import android.util.Log
import com.google.gson.Gson


class InvoiceRepositoryImpl(
    private val api: InvoiceApi,
    private val dao: InvoiceDao,
    private val context: Context
) : InvoiceRepository {

    override suspend fun getInvoices(): List<Invoice> {
        return if (AppConfig.USE_MOCK_LOCAL) {
            // Simular tiempo de carga
            delay(Random.nextLong(1000L, 3001L))

            // Leer JSON de assets
            val json = context.assets.open("invoices.json")
                .bufferedReader()
                .use { it.readText() }

            Gson().fromJson(json, Array<Invoice>::class.java).toList()

        } else {
            try {
                val response = api.getInvoices()
                Log.d("InvoiceRepo", "Response received: ${response.facturas.size} invoices")
                val invoices = response.facturas.map { it.toDomain() }
                dao.insertInvoices(invoices.map { it.toEntity() })
                invoices
            } catch (e: Exception) {
                Log.e("InvoiceRepo", "Error fetching invoices from Mockoon", e)
                dao.getInvoices().map { it.toDomain() }
            }
        }
    }
}