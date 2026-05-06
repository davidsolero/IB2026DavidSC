package com.iberdrola.practicas2026.davidsc.di

import android.content.Context
import android.util.Log
import co.infinum.retromock.Retromock
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.core.utils.DeviceUtils
import com.iberdrola.practicas2026.davidsc.data.remote.api.AssetBodyFactory
import com.iberdrola.practicas2026.davidsc.data.remote.api.InvoiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        val emulator = DeviceUtils.isEmulator()

        val url = if (emulator) {
            "https://10.0.2.2:3001/"
        } else {
            "https://localhost:3001/"
        }

        Log.d("NetworkModule", "isEmulator=$emulator, BASE_URL=$url")
        return url
    }


    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
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

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor {message->
            Log.d("message", message)
        }.apply { level =HttpLoggingInterceptor.Level.BODY }
        val cf = CertificateFactory.getInstance("X.509")
        val certInput = context.resources.openRawResource(R.raw.certificado)
        val certificate = cf.generateCertificate(certInput)
        certInput.close()

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)

        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(keyStore)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)

        return OkHttpClient.Builder()
            .sslSocketFactory(
                sslContext.socketFactory,
                tmf.trustManagers[0] as X509TrustManager
            )
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d("OkHttp", "${request.method} ${request.url}")
                try {
                    val response = chain.proceed(request)
                    Log.d("OkHttp", "Response ${response.code} ${request.url}")
                    response
                } catch (e: Exception) {
                    Log.e("OkHttp", "Error connecting to ${request.url}: ${e.javaClass.simpleName}: ${e.message}")
                    throw e
                }
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .build()
    }


}


