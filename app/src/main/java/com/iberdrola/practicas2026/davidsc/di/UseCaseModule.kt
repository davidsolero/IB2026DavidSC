package com.iberdrola.practicas2026.davidsc.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
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
        repository: InvoiceRepository,
        remoteConfig: FirebaseRemoteConfig
    ): GetInvoicesUseCase {
        return GetInvoicesUseCase(repository, remoteConfig)
    }

    @Provides
    fun provideGetStreetsUseCase(
        getInvoicesUseCase: GetInvoicesUseCase
    ): GetStreetsUseCase {
        return GetStreetsUseCase(getInvoicesUseCase)
    }
}