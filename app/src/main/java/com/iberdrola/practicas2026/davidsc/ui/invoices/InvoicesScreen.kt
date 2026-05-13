package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.ui.navigation.SafeNavController
import com.iberdrola.practicas2026.davidsc.ui.navigation.Screen
import com.iberdrola.practicas2026.davidsc.ui.util.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    navController: NavController,
    safeNav: SafeNavController,
    viewModel: InvoicesViewModel = hiltViewModel()
) {
    val invoices by viewModel.invoices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedStreet by viewModel.selectedStreet.collectAsState()
    val isFilterActive by viewModel.isFilterActive.collectAsState()
    val hasInvoices by viewModel.hasInvoicesForSelectedType.collectAsState()
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val allInvoices by viewModel.allInvoices.collectAsState()
    val filteredInvoices by viewModel.invoices.collectAsState()

    var showRatingSheet by remember { mutableStateOf(false) }
    var showInvoiceDialog by remember { mutableStateOf(false) }
    var isExiting by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val activity = LocalActivity.current
    val isGasTabVisible by viewModel.isGasTabVisible.collectAsState()
    val navigateBack: () -> Unit = {
        if (!isExiting) {
            isExiting = true
            val popped = safeNav.popBackStack()
            if (!popped) activity?.finish()
        }
    }

    val handleBack: () -> Unit = {
        if (!isExiting) {
            if (viewModel.onBackPressed()) {
                showRatingSheet = true
            } else {
                navigateBack()
            }
        }
    }

    val dateFormatter = remember { DateFormatter() }

    val rangeText = remember(allInvoices) {
        if (allInvoices.isEmpty()) ""
        else {
            val first = allInvoices.minOf { it.date }
            val last = allInvoices.maxOf { it.date }

            if (first == last) {
                dateFormatter.formatCompact(first)
            } else {
                dateFormatter.formatRange(first, last)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.amountFilterAdjusted.collect { event ->
            val message = when (event) {
                is InvoicesViewModel.AmountFilterEvent.Adjusted ->
                    "Hemos adaptado el filtro al rango disponible:\n (${event.newMin} € – ${event.newMax} €)"
                is InvoicesViewModel.AmountFilterEvent.Reset ->
                    "Filtro de importe eliminado: fuera del rango disponible"
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    BackHandler(enabled = !isExiting) { handleBack() }

    Scaffold(
        topBar = {
            InvoicesHeader(
                onBackClick = { handleBack() },
                selectedStreet = selectedStreet
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = colorResource(R.color.iberdrola_green),
                    contentColor = Color.White
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

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
                if (isGasTabVisible) {
                    TabItemUnderline(
                        text = stringResource(R.string.tab_gas),
                        selected = selectedType == InvoiceType.GAS,
                        onClick = { viewModel.selectType(InvoiceType.GAS) }
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray)

            if (isLoading) {
                if (isLandscape) SkeletonInvoicesLandscape()
                else {
                    SkeletonLastInvoiceCard()
                    SkeletonList()
                }
            } else {
                val lastInvoice = allInvoices.firstOrNull()

                val history = remember(filteredInvoices) {
                    filteredInvoices
                }

                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.margin_small)
                        )
                    ) {
                        lastInvoice?.let {
                            LastInvoiceCard(
                                invoice = it,
                                rangeText = rangeText,
                                onClick = { showInvoiceDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(horizontal = dimensionResource(R.dimen.margin_medium))
                            )
                        }
                        Column(modifier = Modifier.weight(2f)) {
                            InvoiceHistoryHeader(
                                onFilterClick = { safeNav.navigate(Screen.FILTER) },
                                isFilterActive = isFilterActive,
                                enabled = hasInvoices && !showRatingSheet && !isExiting
                            )
                            InvoiceHistoryContent(
                                history = history,
                                isFilterActive = isFilterActive,
                                onClick = { showInvoiceDialog = true },
                                onClearFilters = { viewModel.clearFilter() }
                            )
                        }
                    }
                } else {
                    lastInvoice?.let {
                        LastInvoiceCard(
                            invoice = it,
                            rangeText = rangeText,
                            onClick = { showInvoiceDialog = true }
                        )
                    }
                    InvoiceHistoryHeader(
                        onFilterClick = { safeNav.navigate(Screen.FILTER) },
                        isFilterActive = isFilterActive,
                        enabled = hasInvoices && !showRatingSheet && !isExiting
                    )
                    InvoiceHistoryContent(
                        history = history,
                        isFilterActive = isFilterActive,
                        onClick = { showInvoiceDialog = true },
                        onClearFilters = { viewModel.clearFilter() }
                    )
                }
            }
        }

        if (showRatingSheet) {
            RatingBottomSheet(
                onRated = { viewModel.onRated() },
                onRatedDismiss = {
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
                containerColor = Color.White,
                text = { Text(stringResource(R.string.invoice_not_available)) },
                confirmButton = {
                    TextButton(
                        onClick = { showInvoiceDialog = false },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = colorResource(R.color.iberdrola_green)
                        )
                    ) {
                        Text(stringResource(R.string.accept))
                    }
                }
            )
        }
    }
}


@Composable
private fun InvoiceHistoryContent(
    history: List<Invoice>,
    isFilterActive: Boolean,
    onClick: () -> Unit,
    onClearFilters: () -> Unit
) {
    if (history.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            InvoiceEmptyState(
                isFilterActive = isFilterActive,
                onClearFilters = onClearFilters
            )
        }
    } else {
        InvoiceListGroupedByYear(
            invoices = history,
            onClick =  {invoice -> onClick()}
        )
    }
}

@Composable
private fun InvoiceEmptyState(
    isFilterActive: Boolean,
    onClearFilters: () -> Unit
) {
    val primaryColor = colorResource(R.color.iberdrola_green)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.margin_large)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isFilterActive) {
                stringResource(R.string.no_older_invoices_with_filter)
            } else {
                stringResource(R.string.no_older_invoices)
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isFilterActive) {
                "No encontramos facturas con estos filtros.\nPrueba a ampliarlos para ver resultados."
            } else {
                "Aquí aparecerán tus facturas cuando estén disponibles."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        if (isFilterActive) {
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onClearFilters,
                border = BorderStroke(2.dp, primaryColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryColor
                )
            ) {
                Text("Limpiar filtros")
            }
        }
    }
}
@Composable
private fun InvoiceHistoryHeader(
    onFilterClick: () -> Unit,
    isFilterActive: Boolean,
    enabled: Boolean = true
) {
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
        OutlinedButton(
            onClick = onFilterClick,
            enabled = enabled,
            border = BorderStroke(
                2.dp,
                if (enabled) {
                    colorResource(R.color.iberdrola_green)
                } else {
                    Color.Gray
                }
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isFilterActive) colorResource(R.color.iberdrola_green) else Color.Transparent,
                contentColor = if (isFilterActive) Color.White else colorResource(R.color.iberdrola_green),
                disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
                disabledContentColor = Color.Gray
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = stringResource(R.string.invoices_filter),
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_xsmall)))
            Text(stringResource(R.string.invoices_filter))
        }
    }
}