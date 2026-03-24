package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice

@Composable
fun LastInvoiceCard(invoice: Invoice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.last_invoice),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "%.2f€".format(invoice.amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = invoice.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            StatusBadge(
                status = invoice.status,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun InvoiceList(invoices: List<Invoice>) {
    var showDialog by remember { mutableStateOf(false) }

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

    LazyColumn {
        items(invoices) { invoice ->
            InvoiceItem(
                invoice = invoice,
                onClick = { showDialog = true }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun InvoiceItem(invoice: Invoice, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = invoice.date,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = invoice.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            StatusBadge(
                status = invoice.status,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text(
            text = "%.2f€".format(invoice.amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val color = when (status) {
        "Pagada" -> Color(0xFF4CAF50)
        "Pendiente de Pago" -> Color(0xFFFF5722)
        "En trámite de cobro" -> Color(0xFFFF9800)
        "Anulada" -> Color(0xFF9E9E9E)
        "Cuota Fija" -> Color(0xFF2196F3)
        else -> Color(0xFF9E9E9E)
    }
    Text(
        text = status,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(color = color, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun SkeletonList() {
    LazyColumn {
        items(4) {
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(14.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
            )
        }
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(14.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun InvoicesHeader(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // 🔹 Fila superior → flecha + "Atrás"
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás"
                )
            }

            Text(
                text = "Atrás",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Título
        Text(
            text = stringResource(R.string.invoices_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // 🔹 Subtítulo
        Text(
            text = stringResource(R.string.invoices_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
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

@Composable
fun InvoiceListGroupedByYear(invoices: List<Invoice>, onClick: (Invoice) -> Unit) {
    val invoicesByYear = invoices.groupBy { it.date.take(4) } // Agrupar por año

    LazyColumn {
        invoicesByYear.toSortedMap(compareByDescending { it }).forEach { (year, invoicesInYear) ->
            // Encabezado del año
            item {
                YearHeader(year = year)
            }
            // Facturas del año
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
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
    )
}


@Composable
fun SkeletonLastInvoiceCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(20.dp)
                .background(Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(24.dp)
                .background(Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(14.dp)
                .background(Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
        )
    }
}