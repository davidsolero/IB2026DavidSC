package com.iberdrola.practicas2026.davidsc.utils

import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.davidsc.data.local.entity.InvoiceEntity

class FakeDao : InvoiceDao {

    var savedInvoices: List<InvoiceEntity> = emptyList()

    override suspend fun getInvoices(): List<InvoiceEntity> {
        return savedInvoices
    }

    override suspend fun insertInvoices(invoices: List<InvoiceEntity>) {
        savedInvoices = invoices
    }

    override suspend fun clearInvoices() {
        savedInvoices = emptyList()
    }
}