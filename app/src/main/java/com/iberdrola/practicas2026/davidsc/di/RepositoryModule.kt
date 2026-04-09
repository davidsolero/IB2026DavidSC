package com.iberdrola.practicas2026.davidsc.di

import android.content.Context
import com.iberdrola.practicas2026.davidsc.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
import com.iberdrola.practicas2026.davidsc.data.repository.InvoiceRepositoryImpl
import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideInvoiceRepository(
        api: InvoiceApi,
        dao: InvoiceDao,
        @ApplicationContext context: Context
    ): InvoiceRepository {
        return InvoiceRepositoryImpl(api, dao, context)
    }
}