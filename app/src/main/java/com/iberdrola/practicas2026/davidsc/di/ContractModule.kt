package com.iberdrola.practicas2026.davidsc.di

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
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
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val config = FirebaseRemoteConfig.getInstance()
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (AppConfig.DEBUG) 0 else 3600)
            .build()
        config.setConfigSettingsAsync(settings)
        config.setDefaultsAsync(mapOf(GetContractsUseCase.KEY_GAS_ENABLED to true))
        return config
    }

    @Provides
    fun provideGetContractsUseCase(
        repository: ContractRepository,
        remoteConfig: FirebaseRemoteConfig
    ): GetContractsUseCase {
        return GetContractsUseCase(repository, remoteConfig)
    }

    @Provides
    fun provideUpdateContractEmailUseCase(
        repository: ContractRepository
    ): UpdateContractEmailUseCase {
        return UpdateContractEmailUseCase(repository)
    }
}