package com.iberdrola.practicas2026.davidsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iberdrola.practicas2026.davidsc.ui.invoices.InvoicesScreen
import com.iberdrola.practicas2026.davidsc.ui.theme.IB2026DavidSCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IB2026DavidSCTheme {
                val navController = rememberNavController()  // ← Aquí creamos el NavController

                NavHost(
                    navController = navController,
                    startDestination = "invoices_screen"
                ) {
                    composable("invoices_screen") {
                        InvoicesScreen(
                            navController = navController   // ← pasamos al screen
                            // viewModel lo obtiene por hiltViewModel(), no hace falta pasarlo
                        )
                    }
                }
            }
        }
    }
}