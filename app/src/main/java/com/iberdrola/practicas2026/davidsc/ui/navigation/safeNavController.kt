package com.iberdrola.practicas2026.davidsc.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import java.util.concurrent.atomic.AtomicBoolean
import android.os.Handler
import android.os.Looper

/**
 * Wraps [NavHostController] to prevent concurrent navigation calls and back-spam.
 *
 * The lock is released only after the destination changes AND the transition
 * animation has had time to complete. This prevents the user from triggering
 * a second navigation before the first one is visually finished.
 */
class SafeNavController(
    private val navController: NavHostController
) {

    private val isNavigating = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, _, _ ->
            // Destination changed, but the transition animation is still running.
            // Wait for it to finish before accepting the next navigation.
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ isNavigating.set(false) }, TRANSITION_DELAY_MS)
        }

    init {
        navController.addOnDestinationChangedListener(destinationListener)
    }

    fun navigate(route: String) {
        if (!isNavigating.compareAndSet(false, true)) return
        navController.navigate(route)
    }

    fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit) {
        if (!isNavigating.compareAndSet(false, true)) return
        navController.navigate(route, builder)
    }

    fun popBackStack(): Boolean {
        if (!isNavigating.compareAndSet(false, true)) return false
        val result = navController.popBackStack()
        // If there was nothing to pop, the listener will never fire.
        if (!result) isNavigating.set(false)
        return result
    }

    fun popTo(route: String, inclusive: Boolean = false) {
        if (!isNavigating.compareAndSet(false, true)) return
        navController.popBackStack(route, inclusive)
    }

    fun currentRoute(): String? = navController.currentDestination?.route

    fun canNavigate(): Boolean = !isNavigating.get()

    companion object {
        // Compose Navigation's default transition is ~300ms.
        // A small buffer on top ensures the animation is fully complete.
        private const val TRANSITION_DELAY_MS = 400L
    }
}