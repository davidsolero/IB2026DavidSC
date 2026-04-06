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
            .baseUrl("http://10.0.2.2:3001/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideInvoiceApi(
        @ApplicationContext context: Context,
        retrofit: Retrofit
    ): InvoiceApi {
        // The mock toggle at runtime is handled in InvoiceRepositoryImpl by reading
        // AppConfig.useMockLocal on each call. Retromock is only wired here for the
        // initial app launch state — runtime changes do not rebuild this dependency.
        return if (AppConfig.useMockLocal) {
            Retromock.Builder()
                .retrofit(retrofit)
                .defaultBodyFactory(AssetBodyFactory(context.assets))
                .build()
                .create(InvoiceApi::class.java)
        } else {
            retrofit.create(InvoiceApi::class.java)
        }
    }
}