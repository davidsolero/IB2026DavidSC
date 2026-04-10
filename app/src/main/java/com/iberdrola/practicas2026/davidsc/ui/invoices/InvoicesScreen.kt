package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.core.utils.Screen

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
    val selectedStreet by viewModel.selectedStreet.collectAsState()

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showRatingSheet by remember { mutableStateOf(false) }
    var showInvoiceDialog by remember { mutableStateOf(false) }


    val activity = LocalActivity.current

    val navigateBack = {
        val popped = navController.popBackStack()
        if (!popped) {
            activity?.finish()
        }
    }

    val handleBack = {
        if (viewModel.onBackPressed()) {
            showRatingSheet = true
        } else {
            navigateBack()
        }
    }

    BackHandler { handleBack() }

    Scaffold(
        topBar = {
            InvoicesHeader(
                onBackClick = {
                    handleBack()
                },
                useMock = useMock,
                onToggleMock = { viewModel.toggleMock() },
                selectedStreet = selectedStreet
            )
        }
    ) { innerPadding ->

        val filteredInvoices = invoices.sortedByDescending { it.startDate }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.margin_medium))
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
                if (isLandscape) {
                    SkeletonInvoicesLandscape()
                } else {
                    SkeletonLastInvoiceCard()
                    SkeletonList()
                }
            } else {
                if (filteredInvoices.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.margin_medium)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_invoices_found),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    if (isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_small))
                        ) {
                            filteredInvoices.firstOrNull()?.let { latest ->
                                LastInvoiceCard(
                                    invoice = latest,
                                    onClick = { showInvoiceDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(horizontal = dimensionResource(R.dimen.margin_medium))
                                )
                            }
                            Column(modifier = Modifier.weight(2f)) {
                                if (filteredInvoices.size > 1) {
                                    InvoiceHistoryHeader(onFilterClick = { navController.navigate(Screen.FILTER) })
                                    InvoiceListGroupedByYear(
                                        invoices = filteredInvoices.drop(1),
                                        onClick = { showInvoiceDialog = true }
                                    )
                                }
                            }
                        }
                    } else {
                        filteredInvoices.firstOrNull()?.let { latest ->
                            LastInvoiceCard(
                                invoice = latest,
                                onClick = { showInvoiceDialog = true }
                            )
                        }
                        if (filteredInvoices.size > 1) {
                            InvoiceHistoryHeader(onFilterClick = { navController.navigate(Screen.FILTER) })
                            InvoiceListGroupedByYear(
                                invoices = filteredInvoices.drop(1),
                                onClick = { showInvoiceDialog = true }
                            )
                        }
                    }
                }
            }
        }

        if (showRatingSheet) {
            RatingBottomSheet(
                onRated = {
                    viewModel.onRated()
                    showRatingSheet = false
                    navigateBack()
                },
                onLater = {
                    viewModel.onRespondLater()
                    showRatingSheet = false
                    navigateBack()
                },
                onDismiss = {
                    viewModel.onSheetDismissed()
                    showRatingSheet = false
                    navigateBack()
                }
            )
        }

        if (showInvoiceDialog) {
            AlertDialog(
                onDismissRequest = { showInvoiceDialog = false },
                text = { Text(stringResource(R.string.invoice_not_available)) },
                confirmButton = {
                    TextButton(onClick = { showInvoiceDialog = false }) {
                        Text(stringResource(R.string.accept))
                    }
                }
            )
        }
    }
}

// Extracted to avoid duplicating the history header block in portrait and landscape layouts.
@Composable
private fun InvoiceHistoryHeader(onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_medium),
                vertical = dimensionResource(R.dimen.margin_small)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.invoices_history),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        OutlinedButton(onClick = onFilterClick) {
            Text(stringResource(R.string.invoices_filter))
        }
    }
}