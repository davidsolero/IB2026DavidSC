package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.Screen
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceFilter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    viewModel: InvoicesViewModel = hiltViewModel(
        navController.getBackStackEntry(Screen.INVOICES)
    )
) {
    val activeFilter by viewModel.activeFilter.collectAsState()
    val minAmount by viewModel.minAmount.collectAsState()
    val maxAmount by viewModel.maxAmount.collectAsState()

    // Local UI state — only committed to the ViewModel when the user taps "Aplicar filtros".
    var desde by remember { mutableStateOf(activeFilter.desde) }
    var hasta by remember { mutableStateOf(activeFilter.hasta) }
    var selectedEstados by remember { mutableStateOf(activeFilter.estados) }

    var showDesdePicker by remember { mutableStateOf(false) }
    var showHastaPicker by remember { mutableStateOf(false) }

    val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "ES"))
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    val allEstados = listOf(
        stringResource(R.string.status_paid),
        stringResource(R.string.status_pending),
        stringResource(R.string.status_in_progress),
        stringResource(R.string.status_cancelled),
        stringResource(R.string.status_fixed_fee)
    )



    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.margin_medium))
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = stringResource(R.string.back),
                        tint = colorResource(R.color.iberdrola_green),
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.icon_size_medium))
                            .graphicsLayer { scaleX = -1f }
                    )
                }
                Text(
                    text = stringResource(R.string.back),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.iberdrola_green),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            Text(
                text = stringResource(R.string.filter_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            // --- Por fecha ---
            Text(
                text = stringResource(R.string.filter_by_date),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateField(
                    label = stringResource(R.string.filter_date_from),
                    date = desde,
                    formatter = displayFormatter,
                    onClick = { showDesdePicker = true },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_medium)))
                DateField(
                    label = stringResource(R.string.filter_date_to),
                    date = hasta,
                    formatter = displayFormatter,
                    onClick = { showHastaPicker = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            // --- Por importe ---
            Text(
                text = stringResource(R.string.filter_by_amount),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            var sliderRange by remember(minAmount, maxAmount) {
                val safeMin = minAmount.toFloat()
                val safeMax = maxOf(minAmount.toFloat(), maxAmount.toFloat())
                val currentMin = (activeFilter.importeMin ?: minAmount).toFloat().coerceIn(safeMin, safeMax)
                val currentMax = (activeFilter.importeMax ?: maxAmount).toFloat().coerceIn(safeMin, safeMax)
                mutableStateOf(currentMin..maxOf(currentMin, currentMax))
            }

            Text(
                text = "${sliderRange.start.toInt()} € - ${sliderRange.endInclusive.toInt()} €",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            RangeSlider(
                value = sliderRange.start.coerceIn(minAmount.toFloat(), maxAmount.toFloat())
                        ..sliderRange.endInclusive.coerceIn(minAmount.toFloat(), maxAmount.toFloat()),
                onValueChange = { newRange ->
                    if (newRange.start <= newRange.endInclusive) {
                        sliderRange = newRange
                    }
                },
                valueRange = minAmount.toFloat()..maxOf(minAmount.toFloat(), maxAmount.toFloat()),
                colors = SliderDefaults.colors(
                    thumbColor = iberdrolaGreen,
                    activeTrackColor = iberdrolaGreen
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$minAmount €",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$maxAmount €",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            // --- Por estado ---
            Text(
                text = stringResource(R.string.filter_by_status),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            allEstados.forEach { estado ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedEstados = if (estado in selectedEstados) {
                                selectedEstados - estado
                            } else {
                                selectedEstados + estado
                            }
                        }
                        .padding(vertical = dimensionResource(R.dimen.margin_xsmall))
                ) {
                    Checkbox(
                        checked = estado in selectedEstados,
                        onCheckedChange = { checked ->
                            selectedEstados = if (checked) {
                                selectedEstados + estado
                            } else {
                                selectedEstados - estado
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(R.color.iberdrola_green)
                        )
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_small)))
                    Text(
                        text = estado,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            // --- Botones ---
            Button(
                onClick = {
                    viewModel.applyFilter(
                        InvoiceFilter(
                            desde = desde,
                            hasta = hasta,
                            importeMin = sliderRange.start.toInt(),
                            importeMax = sliderRange.endInclusive.toInt(),
                            estados = selectedEstados
                        )
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.iberdrola_green),
                )
            ) {
                Text(stringResource(R.string.filter_apply))
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            TextButton(
                onClick = {
                    viewModel.clearFilter()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.filter_clear),
                    color = colorResource(R.color.iberdrola_green),
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
        }
    }

    // --- Date pickers ---
    if (showDesdePicker) {
        IberdrolaDatePickerDialog(
            initialDate = desde,
            onConfirm = { date ->
                desde = date
                showDesdePicker = false
            },
            onDismiss = { showDesdePicker = false },
            maxDateMillis = hasta
                ?.atStartOfDay(ZoneId.of("UTC"))
                ?.toInstant()
                ?.toEpochMilli()
        )
    }

    if (showHastaPicker) {
        IberdrolaDatePickerDialog(
            initialDate = hasta,
            onConfirm = { date ->
                hasta = date
                showHastaPicker = false
            },
            onDismiss = { showHastaPicker = false },
            minDateMillis = desde
                ?.atStartOfDay(ZoneId.of("UTC"))
                ?.toInstant()
                ?.toEpochMilli()
        )
    }
}

@Composable
private fun DateField(
    label: String,
    date: LocalDate?,
    formatter: DateTimeFormatter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = date?.format(formatter) ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider()
        }
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IberdrolaDatePickerDialog(
    initialDate: LocalDate?,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    minDateMillis: Long? = null,
    maxDateMillis: Long? = null
) {
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    val initialMillis = initialDate
        ?.atStartOfDay(ZoneId.of("UTC"))
        ?.toInstant()
        ?.toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val afterMin = minDateMillis == null || utcTimeMillis >= minDateMillis
                val beforeMax = maxDateMillis == null || utcTimeMillis <= maxDateMillis
                return afterMin && beforeMax
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        onConfirm(date)
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.accept),
                    color = iberdrolaGreen
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = Color.Gray
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                // día seleccionado
                selectedDayContainerColor = iberdrolaGreen,
                selectedDayContentColor = Color.White,

                // día de hoy
                todayDateBorderColor = iberdrolaGreen,
                todayContentColor = iberdrolaGreen,

                // mes / cabecera
                headlineContentColor = iberdrolaGreen,
                subheadContentColor = iberdrolaGreen,
                titleContentColor = iberdrolaGreen,

                // flechas navegación
                navigationContentColor = iberdrolaGreen
            )
        )
    }
}