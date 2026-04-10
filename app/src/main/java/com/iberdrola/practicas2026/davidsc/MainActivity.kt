package com.iberdrola.practicas2026.davidsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iberdrola.practicas2026.davidsc.core.utils.Screen
import com.iberdrola.practicas2026.davidsc.ui.invoices.FilterScreen
import com.iberdrola.practicas2026.davidsc.ui.invoices.InvoicesScreen
import com.iberdrola.practicas2026.davidsc.ui.main.MainScreen
import com.iberdrola.practicas2026.davidsc.ui.theme.IB2026DavidSCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IB2026DavidSCTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.MAIN
                ) {
                    composable(Screen.MAIN) {
                        MainScreen(navController = navController)
                    }
                    composable(Screen.INVOICES) {
                        InvoicesScreen(navController = navController)
                    }
                    composable(Screen.FILTER) {
                        FilterScreen(navController = navController)
                    }
                }
            }
        }
    }
}