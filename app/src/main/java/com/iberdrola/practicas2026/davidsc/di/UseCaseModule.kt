package com.iberdrola.practicas2026.davidsc.di

import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetStreetsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetInvoicesUseCase(
        repository: InvoiceRepository
    ): GetInvoicesUseCase {
        return GetInvoicesUseCase(repository)
    }

    @Provides
    fun provideGetStreetsUseCase(
        repository: InvoiceRepository
    ): GetStreetsUseCase {
        return GetStreetsUseCase(repository)
    }
}