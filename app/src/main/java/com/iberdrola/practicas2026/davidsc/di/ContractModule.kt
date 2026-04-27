package com.iberdrola.practicas2026.davidsc.di

import android.content.Context
import com.iberdrola.practicas2026.davidsc.data.repository.ContractRepositoryImpl
import com.iberdrola.practicas2026.davidsc.domain.repository.ContractRepository
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetContractsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object ContractModule {

    @Provides
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
}