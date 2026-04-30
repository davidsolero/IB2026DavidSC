package com.iberdrola.practicas2026.davidsc.ui.navigation

import android.os.Handler
import android.os.Looper
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

class SafeNavController(
    private val navController: NavHostController
) {

    private var isNavigating = false
    private val handler = Handler(Looper.getMainLooper())

    fun navigate(route: String) {
        if (isNavigating) return

        lock()
        navController.navigate(route)
        unlockWithDelay()
    }

    fun navigate(
        route: String,
        builder: NavOptionsBuilder.() -> Unit
    ) {
        if (isNavigating) return

        lock()
        navController.navigate(route, builder)
        unlockWithDelay()
    }

    fun popBackStack(): Boolean {
        if (isNavigating) return false

        lock()
        val result = navController.popBackStack()
        unlockWithDelay()
        return result
    }

    fun popTo(route: String, inclusive: Boolean = false) {
        if (isNavigating) return

        lock()
        navController.popBackStack(route, inclusive)
        unlockWithDelay()
    }

    fun currentRoute(): String? {
        return navController.currentDestination?.route
    }

    fun canNavigate(): Boolean = !isNavigating

    private fun lock() {
        isNavigating = true
    }

    private fun unlockWithDelay() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            isNavigating = false
        }, NAVIGATION_DELAY)
    }

    private companion object {
        const val NAVIGATION_DELAY = 300L
    }
}