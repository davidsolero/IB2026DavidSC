package com.iberdrola.practicas2026.davidsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.iberdrola.practicas2026.davidsc.ui.invoices.InvoicesScreen
import com.iberdrola.practicas2026.davidsc.ui.theme.IB2026DavidSCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IB2026DavidSCTheme {
                InvoicesScreen()
            }
        }
    }
}