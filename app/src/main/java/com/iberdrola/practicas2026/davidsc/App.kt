package com.iberdrola.practicas2026.davidsc

import android.app.Application
import android.content.SharedPreferences
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        AppConfig.useMockLocal = prefs.getBoolean("use_mock", false)
    }
}