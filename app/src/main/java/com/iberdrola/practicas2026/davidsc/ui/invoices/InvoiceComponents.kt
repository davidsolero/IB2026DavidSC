package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.ui.util.CurrencyFormatter
import com.iberdrola.practicas2026.davidsc.ui.util.DateFormatter


@Composable
fun LastInvoiceCard(
    invoice: Invoice,
    rangeText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val currencyFormatter = remember { CurrencyFormatter() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.margin_medium))
            .clickable { onClick() },
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.3.dp, colorResource(R.color.iberdrola_green)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.margin_medium))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart) // ocupa el ancho, texto puede ir debajo del icono
                ) {
                    Text(
                        text = stringResource(R.string.last_invoice),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = invoice.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(
                            top = dimensionResource(R.dimen.margin_xsmall),
                            bottom = 15.dp
                        )
                    )
                    val amountText = currencyFormatter.format(invoice.amount)

                    val baseStyle = MaterialTheme.typography.titleLarge.copy(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.1f,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = buildAnnotatedString {
                            if (amountText.isNotEmpty()) {
                                append(amountText.dropLast(1))

                                withStyle(
                                    SpanStyle(
                                        fontSize = baseStyle.fontSize * 0.7f,
                                        fontWeight = FontWeight.Black
                                    )
                                ) {
                                    append(amountText.last().toString())
                                }
                            }
                        },
                        style = baseStyle,
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_xsmall))
                    )
                    Text(
                        text = rangeText,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))
                }

                val icon = when (invoice.type) {
                    InvoiceType.LUZ -> Icons.Outlined.Lightbulb
                    InvoiceType.GAS -> Icons.Outlined.Whatshot
                }

                Icon(
                    imageVector = icon,
                    contentDescription = invoice.type.name,
                    tint = colorResource(R.color.iberdrola_green),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.icon_size_large))
                        .align(Alignment.TopEnd) // icono fijo arriba a la derecha
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.margin_xsmall))
            )
            StatusBadge(
                status = invoice.status,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_small))
            )
        }
    }
}

@Composable
fun InvoiceList(invoices: List<Invoice>, onClick: (Invoice) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(invoices) { invoice ->
            InvoiceItem(
                invoice = invoice,
                onClick = { onClick(invoice) }
            )
        }
    }
}

@Composable
fun InvoiceItem(invoice: Invoice, onClick: () -> Unit) {
    val dateFormatter = remember { DateFormatter() }
    val currencyFormatter = remember { CurrencyFormatter() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_medium),
                vertical = 15.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dateFormatter.formatInvoiceDate(
                    date = invoice.date,
                    useCompactFormat = false
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = invoice.description,
                style = MaterialTheme.typography.bodySmall
            )
            StatusBadge(
                status = invoice.status,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_xsmall))
            )
        }
        Text(
            text = currencyFormatter.format(invoice.amount),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.margin_small))
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
        )
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        stringResource(R.string.status_paid) -> colorResource(R.color.status_pagado_fondo) to colorResource(
            R.color.status_pagado_texto
        )

        stringResource(R.string.status_pending) -> colorResource(R.color.status_pendiente_pago_fondo) to colorResource(
            R.color.status_pendiente_pago_texto
        )

        stringResource(R.string.status_in_progress) -> colorResource(R.color.status_tramite_fondo) to colorResource(
            R.color.status_tramite_texto
        )

        stringResource(R.string.status_cancelled) -> colorResource(R.color.status_anulada_fondo) to colorResource(
            R.color.status_anulada_texto
        )

        stringResource(R.string.status_fixed_fee) -> colorResource(R.color.status_cuota_fija_fondo) to colorResource(
            R.color.status_cuota_fija_texto
        )

        else -> colorResource(R.color.status_pendiente_pago_fondo) to colorResource(R.color.status_pendiente_pago_texto)
    }

    Text(
        text = status,
        color = textColor,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold
        ),
        modifier = modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.status_badge_radius))
            )
            .padding(
                horizontal = dimensionResource(R.dimen.margin_small),
                vertical = dimensionResource(R.dimen.margin_xsmall)
            )
    )
}

@Composable
fun InvoicesHeader(
    onBackClick: () -> Unit,
    selectedStreet: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                start = dimensionResource(R.dimen.margin_medium),
                end = dimensionResource(R.dimen.margin_medium),
                bottom = dimensionResource(R.dimen.margin_xlarge)
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton(onClick = onBackClick)
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        Text(
            text = stringResource(R.string.invoices_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.15f,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))
        Text(
            text = selectedStreet ?: stringResource(R.string.invoices_subtitle),
            style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = stringResource(R.string.back),
            tint = colorResource(R.color.iberdrola_green),
            modifier = Modifier
                .size(dimensionResource(R.dimen.icon_size_medium))
                .graphicsLayer { scaleX = -1f }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(R.string.back),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.iberdrola_green),
            textDecoration = TextDecoration.Underline
        )
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
            .padding(end = dimensionResource(R.dimen.margin_large))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Color.Black else Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(40.dp)
                .background(if (selected) colorResource(R.color.iberdrola_green) else Color.Transparent)
        )
    }
}

@Composable
fun InvoiceListGroupedByYear(invoices: List<Invoice>, onClick: (Invoice) -> Unit) {
    val invoicesByYear = invoices.groupBy { it.date.take(4) }

    LazyColumn {
        invoicesByYear.toSortedMap(compareByDescending { it }).forEach { (year, invoicesInYear) ->
            item { YearHeader(year = year) }
            items(invoicesInYear) { invoice ->
                InvoiceItem(invoice = invoice, onClick = { onClick(invoice) })
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun YearHeader(year: String) {
    Text(
        text = year,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.margin_medium),
            top = dimensionResource(R.dimen.margin_small),
            bottom = dimensionResource(R.dimen.margin_xsmall)
        )
    )
}

@Composable
fun SkeletonList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
    ) {
        items(7) {
            SkeletonItem()
            HorizontalDivider()
        }
    }
}

@Composable
fun SkeletonItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_medium),
                vertical = dimensionResource(R.dimen.margin_small)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(14.dp)
                    .background(
                        color = colorResource(R.color.skeleton_darkgray),
                        RoundedCornerShape(4.dp)
                    )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .background(
                        color = colorResource(R.color.skeleton_darkgray),
                        RoundedCornerShape(4.dp)
                    )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .background(
                        color = colorResource(R.color.skeleton_darkgray),
                        RoundedCornerShape(4.dp)
                    )
            )
        }
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .background(
                    color = colorResource(R.color.skeleton_darkgray), RoundedCornerShape(4.dp)
                )
        )
    }
}

@Composable
fun SkeletonLastInvoiceCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.margin_medium)),
        border = BorderStroke(1.3.dp, Color.Gray),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.margin_medium))
        ) {

            Box(modifier = Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 38.dp) // deja espacio para el icono
                ) {

                    // Título
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .height(22.dp)
                            .background(
                                color = colorResource(R.color.skeleton_gray),
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Descripción
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.35f)
                            .height(18.dp)
                            .background(
                                color = colorResource(R.color.skeleton_gray),
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Importe grande
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                            .height(30.dp)
                            .background(
                                color = colorResource(R.color.skeleton_gray),
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rango
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp)
                            .background(
                                color = colorResource(R.color.skeleton_gray),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }

                // Icono
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.icon_size_large))
                        .background(
                            color = colorResource(R.color.skeleton_gray),
                            RoundedCornerShape(4.dp)
                        )
                        .align(Alignment.TopEnd)
                )
            }



            // Divider (igual que el real)
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                thickness = 1.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )

            // Badge skeleton
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(22.dp)
                    .background(
                        color = colorResource(R.color.skeleton_gray),
                        RoundedCornerShape(20)
                    )
            )
        }
    }
}

@Composable
fun SkeletonInvoicesLandscape() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.margin_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_small))
    ) {
        SkeletonLastInvoiceCard(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.weight(2f)) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))
            repeat(5) {
                SkeletonItem()
                HorizontalDivider()
            }
        }
    }
}

// Preview data
private val previewInvoices = listOf(
    Invoice(
        1,
        "2026-03-01",
        "Factura Luz",
        52.3,
        "Pagada",
        InvoiceType.LUZ,
        "C/Larios"
    ),
    Invoice(
        2,
        "2026-02-18",
        "Factura Gas",
        28.4,
        "Pendiente de Pago",
        InvoiceType.GAS,
        "C/Larios"
    ),
    Invoice(
        3,
        "2026-03-02",
        "Factura Luz",
        32.5,
        "Pagada",
        InvoiceType.LUZ,
        "C/Larios"
    )
)

@Preview(showBackground = true)
@Composable
fun PreviewLastInvoiceCard() {
    LastInvoiceCard(
        invoice = previewInvoices[0],
        rangeText = "10 abr. 2026 - 20 mar. 2026",
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInvoiceList() {
    InvoiceList(invoices = previewInvoices, onClick = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewSkeletonList() {
    SkeletonList()
}

@Preview(showBackground = true)
@Composable
fun PreviewSkeletonLast() {
    SkeletonLastInvoiceCard()
}

@Preview(showBackground = true)
@Composable
fun PreviewInvoicesHeader() {
    InvoicesHeader(
        onBackClick = {},
        selectedStreet = "CALLE LARIOS"
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewStatusBadgePagada() {
    StatusBadge(status = "Pagada")
}

@Preview(showBackground = true)
@Composable
fun PreviewStatusBadgePendiente() {
    StatusBadge(status = "Pendiente de Pago")
}

@Preview(showBackground = true)
@Composable
fun PreviewTabItemLuzSelected() {
    TabItemUnderline(text = "Luz", selected = true, onClick = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewTabItemGasUnselected() {
    TabItemUnderline(text = "Gas", selected = false, onClick = {})
}