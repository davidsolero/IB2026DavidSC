package com.iberdrola.practicas2026.davidsc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iberdrola.practicas2026.davidsc.data.local.entity.InvoiceEntity

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices")
    suspend fun getInvoices(): List<InvoiceEntity>


    @Query("SELECT * FROM invoices WHERE street = :streetName")
    suspend fun getInvoicesByStreet(streetName: String): List<InvoiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<InvoiceEntity>)
}