package com.iberdrola.practicas2026.davidsc


import android.app.Application
import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()
        AppConfig.useMockLocal = prefs.getBoolean("use_mock", false)
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Remote Config defaults remain active on failure
            }
        }
    }
}