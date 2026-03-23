package com.iberdrola.practicas2026.davidsc.di

import android.content.Context
import co.infinum.retromock.Retromock
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.data.remote.api.AssetBodyFactory
import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetromock(
        @ApplicationContext context: Context,
        retrofit: Retrofit
    ): Retromock {
        return Retromock.Builder()
            .retrofit(retrofit)
            .defaultBodyFactory(AssetBodyFactory(context.assets))  // ← aquí el cambio
            .build()
    }
    @Provides
    @Singleton
    fun provideInvoiceApi(retrofit: Retrofit, retromock: Retromock): InvoiceApi {
        return if (AppConfig.USE_MOCK_LOCAL) {
            retromock.create(InvoiceApi::class.java)
        } else {
            retrofit.create(InvoiceApi::class.java)
        }
    }
}