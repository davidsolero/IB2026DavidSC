package com.iberdrola.practicas2026.davidsc.di

import com.iberdrola.practicas2026.davidsc.domain.repository.InvoiceRepository
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetInvoicesUseCase
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
}