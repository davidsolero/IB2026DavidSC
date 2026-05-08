package com.iberdrola.practicas2026.davidsc

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.iberdrola.practicas2026.davidsc.ui.navigation.AppNavHost
import com.iberdrola.practicas2026.davidsc.ui.navigation.SafeNavController
import com.iberdrola.practicas2026.davidsc.ui.theme.IB2026DavidSCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IB2026DavidSCTheme {
                val window = (LocalView.current.context as Activity).window

                SideEffect {
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = true
                    }
                }

                val navController = rememberNavController()
                val safeNav = remember(navController) {
                    SafeNavController(navController)
                }
                AppNavHost(
                    navController = navController,
                    safeNav = safeNav
                )
            }
        }
    }
}