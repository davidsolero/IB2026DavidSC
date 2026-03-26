package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    navController: NavController,
    viewModel: InvoicesViewModel = hiltViewModel()
) {
    val invoices by viewModel.invoices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val useMock by viewModel.useMock.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()

    var showRatingSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            InvoicesHeader(
                onBackClick = {
                    if (viewModel.onBackPressed()) {
                        showRatingSheet = true
                    } else {
                        navController.popBackStack()
                    }
                },
                useMock = useMock,
                onToggleMock = { viewModel.toggleMock() }
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
                    selected = selectedType == InvoiceType.LUZ,
                    onClick = { viewModel.selectType(InvoiceType.LUZ) }
                )
                TabItemUnderline(
                    text = stringResource(R.string.tab_gas),
                    selected = selectedType == InvoiceType.GAS,
                    onClick = { viewModel.selectType(InvoiceType.GAS) }
                )
            }

            HorizontalDivider(color = Color.LightGray)

            if (isLoading) {
                SkeletonLastInvoiceCard()
                SkeletonList()
            } else {
                val filteredInvoices = invoices
                    .filter { it.type == selectedType }
                    .sortedByDescending { it.date }

                // Última factura
                filteredInvoices.firstOrNull()?.let { latest ->
                    LastInvoiceCard(invoice = latest) { showDialog = true }
                }

                // Histórico header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.invoices_history),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    OutlinedButton(onClick = {}, enabled = false) {
                        Text(stringResource(R.string.invoices_filter))
                    }
                }

                // Lista de facturas agrupadas
                InvoiceListGroupedByYear(
                    invoices = filteredInvoices.drop(1),
                    onClick = { showDialog = true }
                )
            }

            // Bottom Sheet y Dialog fuera del flujo principal
            if (showRatingSheet) {
                RatingBottomSheet(
                    onRated = {
                        viewModel.onRated()
                        showRatingSheet = false
                        navController.popBackStack()
                    },
                    onLater = {
                        viewModel.onRespondLater()
                        showRatingSheet = false
                        navController.popBackStack()
                    },
                    onDismiss = {
                        viewModel.onSheetDismissed()
                        showRatingSheet = false
                        navController.popBackStack()
                    }
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    text = { Text(stringResource(R.string.invoice_not_available)) },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.accept))
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewInvoicesFullScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var useMock by remember { mutableStateOf(true) }

    val fakeInvoices = listOf(
        Invoice(1, "2026-03-01", "Factura Luz", 52.3, "Pagada", InvoiceType.LUZ),
        Invoice(2, "2026-02-18", "Factura Gas", 28.4, "Pendiente de Pago", InvoiceType.GAS),
        Invoice(3, "2026-01-25", "Factura Luz", 32.5, "En trámite de cobro", InvoiceType.LUZ),
        Invoice(4, "2025-12-30", "Factura Gas", 45.0, "Cuota Fija", InvoiceType.GAS)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        InvoicesHeader(
            onBackClick = {},
            useMock = useMock,
            onToggleMock = { useMock = !useMock }
        )

        // Tabs Luz / Gas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TabItemUnderline(text = "Luz", selected = selectedTab == 0) { selectedTab = 0 }
            TabItemUnderline(text = "Gas", selected = selectedTab == 1) { selectedTab = 1 }
        }

        HorizontalDivider(color = Color.LightGray)

        // Última factura
        LastInvoiceCard(invoice = fakeInvoices.first(), onClick = {})

        // Histórico
        Text(
            text = "Histórico",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )

        // Lista de facturas agrupadas por año
        InvoiceListGroupedByYear(invoices = fakeInvoices.drop(1), onClick = {})
    }
}