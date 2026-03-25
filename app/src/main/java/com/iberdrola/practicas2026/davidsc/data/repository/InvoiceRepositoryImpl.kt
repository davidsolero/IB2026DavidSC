package com.iberdrola.practicas2026.davidsc.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomain
import com.iberdrola.practicas2026.davidsc.data.mapper.toEntity
import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
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
        return if (AppConfig.USE_MOCK_LOCAL) {
            // 🔹 Simular tiempo de carga
            delay(Random.nextLong(1000L, 3001L))

            // 🔹 Leer JSON de assets (array plano)
            val json = context.assets.open("invoices.json")
                .bufferedReader()
                .use { it.readText() }

            val invoiceDtos = Gson()
                .fromJson(
                    json,
                    Array<com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto>::class.java
                ).toList()

            Log.d("InvoiceRepo", "Local mock invoices loaded: ${invoiceDtos.size}")

            invoiceDtos.map { it.toDomain() }

        } else {
            try {
                // 🔹 API devuelve InvoicesResponse
                val response: InvoicesResponse = api.getInvoices()
                val invoiceDtos = response.facturas
                Log.d("InvoiceRepo", "Remote invoices fetched: ${invoiceDtos.size}")

                val invoices = invoiceDtos.map { it.toDomain() }

                // 🔹 Guardar en BD local
                dao.insertInvoices(invoices.map { it.toEntity() })

                invoices
            } catch (e: Exception) {
                // Log general
                Log.e("InvoiceRepo", "Error fetching invoices from remote", e)

                // Log detallado
                Log.e("InvoiceRepo", "Error type: ${e::class.java.simpleName}")
                Log.e("InvoiceRepo", "Message: ${e.message}")
                e.cause?.let { Log.e("InvoiceRepo", "Cause: ${it::class.java.simpleName} -> ${it.message}") }

                // Si quieres, también imprimir la pila de llamadas completa como string
                val stackTraceString = Log.getStackTraceString(e)
                Log.e("InvoiceRepo", "Stack trace: $stackTraceString")

                // 🔹 Fallback a BD local
                val fallback = dao.getInvoices().map { it.toDomain() }
                Log.d("InvoiceRepo", "Fallback invoices loaded: ${fallback.size}")

                fallback
            }
        }
    }
}