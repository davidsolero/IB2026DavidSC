package com.iberdrola.practicas2026.davidsc.data.repository

import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.davidsc.data.mapper.toDomain
import com.iberdrola.practicas2026.davidsc.data.mapper.toEntity
import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import kotlin.collections.map

class InvoiceRepositoryImpl(
    private val api: InvoiceApi,
    private val dao: InvoiceDao
) : InvoiceRepository {

    override suspend fun getInvoices(): List<Invoice> {
        return try {
            val invoices = api.getInvoices().map { it.toDomain() }
            dao.insertInvoices(invoices.map { it.toEntity() })
            invoices
        } catch (e: Exception) {
            dao.getInvoices().map { it.toDomain() }
        }
    }
}