package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    navController: NavController,                     // ← Recibimos NavController
    viewModel: InvoicesViewModel = hiltViewModel()
) {
    val invoices by viewModel.invoices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showRatingSheet by remember { mutableStateOf(false) }
    var shouldNavigateBack by remember { mutableStateOf(false) } // controla navegación después del sheet

    LaunchedEffect(Unit) {
        viewModel.loadInvoices()
    }

    // BottomSheet controlado por estado
    if (showRatingSheet) {
        RatingBottomSheet(
            onRated = {
                viewModel.onRated()
                showRatingSheet = false
                shouldNavigateBack = true
            },
            onLater = {
                viewModel.onRespondLater()
                showRatingSheet = false
                shouldNavigateBack = true
            },
            onDismiss = {
                viewModel.onSheetDismissed()
                showRatingSheet = false
                shouldNavigateBack = true
            }
        )
    }

    // Ejecutar la navegación después de cerrar el sheet
    LaunchedEffect(shouldNavigateBack) {
        if (shouldNavigateBack) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            InvoicesHeader(
                onBackClick = {
                    val shouldShow = viewModel.onBackPressed()
                    if (shouldShow) showRatingSheet = true
                    else navController.popBackStack()   // Navegación limpia
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Tabs Luz / Gas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                TabItemUnderline(
                    text = stringResource(R.string.tab_luz),
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                TabItemUnderline(
                    text = stringResource(R.string.tab_gas),
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
            HorizontalDivider(color = Color.LightGray)

            // Contenido: últimas facturas / histórico
            if (isLoading) {
                SkeletonList()
            } else {
                // Última factura
                invoices.firstOrNull()?.let { latest ->
                    LastInvoiceCard(invoice = latest)
                }

                // Histórico header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.invoices_history),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(onClick = {}, enabled = false) {
                        Text(stringResource(R.string.invoices_filter))
                    }
                }

                InvoiceListGroupedByYear(invoices = invoices) { invoice ->
                }
            }
        }
    }
}
