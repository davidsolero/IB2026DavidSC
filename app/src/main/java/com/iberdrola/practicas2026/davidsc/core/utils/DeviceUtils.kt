package com.iberdrola.practicas2026.davidsc.core.utils


import android.os.Build
import android.util.Log

object DeviceUtils {
    fun isEmulator(): Boolean {
        val result = (Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.FINGERPRINT.contains("emulator") ||
                Build.FINGERPRINT.contains("sdk_gphone") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("sdk_gphone") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                Build.BRAND.startsWith("generic") ||
                Build.DEVICE.startsWith("generic") ||
                Build.DEVICE.contains("emulator") ||
                Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("emulator") ||
                Build.PRODUCT.contains("gphone") ||
                Build.HARDWARE == "goldfish" ||
                Build.HARDWARE.contains("ranchu"))

        Log.d("DeviceUtils", "isEmulator=$result | " +
                "FINGERPRINT=${Build.FINGERPRINT} | " +
                "MODEL=${Build.MODEL} | " +
                "MANUFACTURER=${Build.MANUFACTURER} | " +
                "HARDWARE=${Build.HARDWARE}")
        return result
    }
}