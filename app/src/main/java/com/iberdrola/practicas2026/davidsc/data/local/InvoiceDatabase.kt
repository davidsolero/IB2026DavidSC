package com.iberdrola.practicas2026.davidsc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.davidsc.data.local.entity.InvoiceEntity

@Database(entities = [InvoiceEntity::class], version = 1, exportSchema = false)
abstract class InvoiceDatabase : RoomDatabase() {
    abstract fun invoiceDao(): InvoiceDao
}