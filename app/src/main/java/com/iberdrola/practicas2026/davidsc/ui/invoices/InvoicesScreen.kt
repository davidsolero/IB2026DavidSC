package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.Screen
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.ui.util.DateFormatter

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
    val isFilterActive by viewModel.isFilterActive.collectAsState()

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showRatingSheet by remember { mutableStateOf(false) }
    var showInvoiceDialog by remember { mutableStateOf(false) }

    val activity = LocalActivity.current

    val navigateBack = {
        val popped = navController.popBackStack()
        if (!popped) activity?.finish()
    }

    val handleBack = {
        if (viewModel.onBackPressed()) {
            showRatingSheet = true
        } else {
            navigateBack()
        }
    }

    val sortedInvoices = invoices

    val dateFormatter = remember { DateFormatter() }

    val rangeText = remember(sortedInvoices) {
        if (sortedInvoices.isEmpty()) {
            ""
        } else {
            val first = sortedInvoices.minOf { it.date }
            val last = sortedInvoices.maxOf { it.date }

            if (first == last) {
                dateFormatter.formatCompact(first)
            } else {
                dateFormatter.formatRange(first, last)
            }
        }
    }

    BackHandler { handleBack() }

    Scaffold(
        topBar = {
            InvoicesHeader(
                onBackClick = { handleBack() },
                useMock = useMock,
                onToggleMock = { viewModel.toggleMock() },
                selectedStreet = selectedStreet
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

                val latest = sortedInvoices.firstOrNull()
                val history = if (sortedInvoices.size > 1) {
                    sortedInvoices.drop(1)
                } else {
                    emptyList()
                }

                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.margin_small)
                        )
                    ) {

                        latest?.let {
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
                                onFilterClick = { navController.navigate(Screen.FILTER) },
                                isFilterActive = isFilterActive
                            )

                            if (history.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.no_invoices_found),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(
                                        dimensionResource(R.dimen.margin_medium)
                                    )
                                )
                            } else {
                                InvoiceListGroupedByYear(
                                    invoices = history,
                                    onClick = { showInvoiceDialog = true }
                                )
                            }
                        }
                    }

                } else {

                    latest?.let {
                        LastInvoiceCard(
                            invoice = it,
                            rangeText = rangeText,
                            onClick = { showInvoiceDialog = true }
                        )
                    }

                    InvoiceHistoryHeader(
                        onFilterClick = { navController.navigate(Screen.FILTER) },
                        isFilterActive = isFilterActive
                    )

                    if (history.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_invoices_found),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(
                                dimensionResource(R.dimen.margin_medium)
                            )
                        )
                    } else {
                        InvoiceListGroupedByYear(
                            invoices = history,
                            onClick = { showInvoiceDialog = true }
                        )
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
                text = {
                    Text(stringResource(R.string.invoice_not_available))
                },
                confirmButton = {
                    TextButton(onClick = { showInvoiceDialog = false }) {
                        Text(
                            stringResource(R.string.accept),
                            color = colorResource(R.color.iberdrola_green)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun InvoiceHistoryHeader(
    onFilterClick: () -> Unit,
    isFilterActive: Boolean
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
            border = BorderStroke(2.dp, colorResource(R.color.iberdrola_green)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isFilterActive) colorResource(R.color.iberdrola_green) else Color.Transparent,
                contentColor = if (isFilterActive) Color.White else colorResource(R.color.iberdrola_green)
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