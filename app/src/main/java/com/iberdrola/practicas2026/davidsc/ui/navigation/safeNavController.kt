package com.iberdrola.practicas2026.davidsc.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import java.util.concurrent.atomic.AtomicBoolean
class SafeNavController(
    private val navController: NavHostController
) {

    private val isNavigating = AtomicBoolean(false)

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, _, _ ->
            isNavigating.set(false)
        }

    init {
        navController.addOnDestinationChangedListener(destinationListener)
    }

    fun navigate(route: String) {
        if (!isNavigating.compareAndSet(false, true)) return
        navController.navigate(route)
    }

    fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit = {}) {
        if (!isNavigating.compareAndSet(false, true)) return
        navController.navigate(route, builder)
    }

    fun popBackStack(): Boolean {
        if (!isNavigating.compareAndSet(false, true)) return false
        val result = navController.popBackStack()
        if (!result) {
            isNavigating.set(false)
        }

        return result
    }

    fun popTo(route: String, inclusive: Boolean = false) {
        if (!isNavigating.compareAndSet(false, true)) return

        val result = navController.popBackStack(route, inclusive)
        if (!result) {
            isNavigating.set(false)
        }
    }

    fun currentRoute(): String? = navController.currentDestination?.route

    fun canNavigate(): Boolean = !isNavigating.get()
}