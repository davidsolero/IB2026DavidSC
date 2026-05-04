package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.ui.navigation.Screen
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceFilter
import com.iberdrola.practicas2026.davidsc.ui.navigation.SafeNavController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    safeNav: SafeNavController,
    viewModel: InvoicesViewModel = hiltViewModel(
        navController.getBackStackEntry(Screen.INVOICES)
    )
) {
    val activeFilter by viewModel.activeFilter.collectAsState()
    val minAmount by viewModel.minAmount.collectAsState()
    val maxAmount by viewModel.maxAmount.collectAsState()

    var desde by remember { mutableStateOf(activeFilter.desde) }
    var hasta by remember { mutableStateOf(activeFilter.hasta) }
    var selectedEstados by remember { mutableStateOf(activeFilter.estados) }

    var showDesdePicker by remember { mutableStateOf(false) }
    var showHastaPicker by remember { mutableStateOf(false) }

    // Set to true the moment we decide to leave this screen.
    // Never reset to false — once exiting, all interaction is blocked.
    var isExiting by remember { mutableStateOf(false) }

    val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "ES"))
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    val allEstados = listOf(
        stringResource(R.string.status_paid),
        stringResource(R.string.status_pending),
        stringResource(R.string.status_in_progress),
        stringResource(R.string.status_cancelled),
        stringResource(R.string.status_fixed_fee)
    )

    var sliderRange by remember(minAmount, maxAmount) {
        val safeMin = minAmount.toFloat()
        val safeMax = maxOf(minAmount.toFloat(), maxAmount.toFloat())
        val currentMin =
            (activeFilter.importeMin ?: minAmount).toFloat().coerceIn(safeMin, safeMax)
        val currentMax =
            (activeFilter.importeMax ?: maxAmount).toFloat().coerceIn(safeMin, safeMax)
        mutableStateOf(currentMin..maxOf(currentMin, currentMax))
    }



    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.margin_medium))
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

                BackButton(
                    onClick = {
                        if (!isExiting) {
                            isExiting = true
                            safeNav.popBackStack()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

                Text(
                    text = stringResource(R.string.filter_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }, bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.invoice_amount_text),
                        vertical = dimensionResource(R.dimen.margin_small)
                    )
            ) {

                Button(
                    onClick = {
                        if (!isExiting) {
                            isExiting = true
                            viewModel.applyFilter(
                                InvoiceFilter(
                                    desde = desde,
                                    hasta = hasta,
                                    importeMin = sliderRange.start.toInt(),
                                    importeMax = sliderRange.endInclusive.toInt(),
                                    estados = selectedEstados
                                )
                            )
                            safeNav.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = iberdrolaGreen,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.filter_apply),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

                TextButton(
                    onClick = {
                        if (!isExiting) {

                            desde = null
                            hasta = null

                            selectedEstados = emptySet()

                            sliderRange = minAmount.toFloat()..maxAmount.toFloat()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(
                        text = stringResource(R.string.filter_clear),
                        fontWeight = FontWeight.SemiBold,
                        color = iberdrolaGreen,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.margin_medium))
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            // --- DATE ---
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

            // --- COST ---
            Text(
                text = stringResource(R.string.filter_by_amount),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            Box(
                modifier = Modifier
                    .background(
                        color = colorResource(R.color.status_pagado_fondo).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.margin_xsmall))
                    )
                    .padding(
                        horizontal = dimensionResource(R.dimen.margin_small),
                        vertical = dimensionResource(R.dimen.margin_xsmall)
                    )
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "${sliderRange.start.toInt()} € - ${sliderRange.endInclusive.toInt()} €",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            IberdrolaRangeSlider(
                value = sliderRange,
                onValueChange = { sliderRange = it },
                min = minAmount.toFloat(),
                max = maxAmount.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            // --- STATE ---
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
                        modifier = Modifier.scale(1.2f),
                        colors = CheckboxDefaults.colors(
                            checkedColor = iberdrolaGreen,
                            checkmarkColor = Color.White,
                            uncheckedColor = iberdrolaGreen
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
    Column(
        modifier = modifier.clickable { onClick() }
    ) {

        Row(
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
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
            )
        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 3.dp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IberdrolaDatePickerDialog(
    initialDate: LocalDate?,
    onConfirm: (LocalDate?) -> Unit,
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
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
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
            TextButton(
                onClick = {
                    onConfirm(null)
                    onDismiss()
                }
            ) {
                Text("Borrar", color = Color.Red)
            }
        }, shape = DatePickerDefaults.shape,
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        )
    ) {

        Surface(color = Color.White) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    selectedDayContainerColor = iberdrolaGreen,
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = iberdrolaGreen,
                    todayContentColor = iberdrolaGreen,
                    headlineContentColor = iberdrolaGreen,
                    subheadContentColor = iberdrolaGreen,
                    titleContentColor = iberdrolaGreen,
                    navigationContentColor = iberdrolaGreen
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IberdrolaRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    min: Float,
    max: Float,
    modifier: Modifier = Modifier
) {

    val range = max - min
    val startFraction = (value.start - min) / range
    val endFraction = (value.endInclusive - min) / range

    Column(modifier = modifier) {

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            contentAlignment = Alignment.Center
        ) {

            val widthPx = maxWidth

            val range = max - min
            val startFraction = (value.start - min) / range
            val endFraction = (value.endInclusive - min) / range
            val sliderColor = colorResource(R.color.slider_importe)
            // BASE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color.LightGray, CircleShape)
            )

            // ACTIVE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .drawWithContent {

                        val start = size.width * startFraction
                        val end = size.width * endFraction

                        // fondo gris
                        drawRect(Color.LightGray)

                        // rango verde
                        drawRect(
                            color = sliderColor,
                            topLeft = Offset(start, 0f),
                            size = Size(end - start, size.height)
                        )
                    }
            )
            // SLIDER (used instead of material3 for UI porpoises)
            RangeSlider(
                value = value,
                onValueChange = onValueChange,
                valueRange = min..max,
                colors = SliderDefaults.colors(
                    inactiveTrackColor = Color.Transparent,
                    activeTrackColor = Color.Transparent
                ),
                startThumb = {
                    Box(
                        Modifier
                            .size(20.dp)
                            .background(sliderColor, CircleShape)
                    )
                },
                endThumb = {
                    Box(
                        Modifier
                            .size(20.dp)
                            .background(sliderColor, CircleShape)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${floor(min).toInt()} €",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${floor(max).toInt()} €",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewIberdrolaRangeSlider() {
    var value by remember { mutableStateOf(20f..80f) }

    IberdrolaRangeSlider(
        value = value,
        onValueChange = { value = it },
        min = 0f,
        max = 100f,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}