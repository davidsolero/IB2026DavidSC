package com.iberdrola.practicas2026.davidsc.di

import android.content.Context
import com.iberdrola.practicas2026.davidsc.data.repository.ContractRepositoryImpl
import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetContractsUseCase
import com.iberdrola.practicas2026.davidsc.domain.usecase.UpdateContractEmailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContractModule {

    @Provides
    @Singleton
    fun provideContractRepository(
        @ApplicationContext context: Context
    ): ContractRepository {
        return ContractRepositoryImpl(context)
    }

    @Provides
    fun provideGetContractsUseCase(
        repository: ContractRepository
    ): GetContractsUseCase {
        return GetContractsUseCase(repository)
    }

    @Provides
    fun provideUpdateContractEmailUseCase(
        repository: ContractRepository
    ): UpdateContractEmailUseCase {
        return UpdateContractEmailUseCase(repository)
    }
}