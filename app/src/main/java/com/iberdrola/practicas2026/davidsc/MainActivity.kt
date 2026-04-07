package com.iberdrola.practicas2026.davidsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                    startDestination = "main_screen"
                ) {
                    composable("main_screen") {
                        MainScreen(navController = navController)
                    }
                    composable("invoices_screen") {
                        InvoicesScreen(navController = navController)
                    }
                }
            }
        }
    }
}