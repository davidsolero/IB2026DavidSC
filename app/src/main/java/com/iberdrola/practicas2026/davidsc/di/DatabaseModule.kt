package com.iberdrola.practicas2026.davidsc.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.iberdrola.practicas2026.davidsc.data.local.InvoiceDatabase
import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InvoiceDatabase {
        return Room.databaseBuilder(
            context,
            InvoiceDatabase::class.java,
            "invoice_database"
        )
            // Destructive migration is acceptable here as this is a development build.
            // In production, explicit migrations should be defined instead.
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideInvoiceDao(database: InvoiceDatabase): InvoiceDao {
        return database.invoiceDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
}