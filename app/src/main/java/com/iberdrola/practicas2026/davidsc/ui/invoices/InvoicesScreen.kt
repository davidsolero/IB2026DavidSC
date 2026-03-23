package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.davidsc.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    viewModel: InvoicesViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val invoices by viewModel.invoices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showRatingSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadInvoices()
    }

    if (showRatingSheet) {
        RatingBottomSheet(
            onRated = { viewModel.onRated(); showRatingSheet = false; onBackClick() },
            onLater = { viewModel.onRespondLater(); showRatingSheet = false; onBackClick() },
            onDismiss = { viewModel.onSheetDismissed(); showRatingSheet = false; onBackClick() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.invoices_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.invoices_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val shouldShow = viewModel.onBackPressed()
                        if (shouldShow) showRatingSheet = true else onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
        }

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
            // Lista
            InvoiceList(invoices = invoices)
        }
    }
}


@Composable
fun TabItemUnderline(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(end = 24.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(if (selected) 40.dp else 0.dp)  // ancho fijo, no fillMaxWidth
                .background(
                    if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                )
        )
    }
}