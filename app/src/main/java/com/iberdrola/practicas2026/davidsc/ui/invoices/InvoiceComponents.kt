package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import com.iberdrola.practicas2026.davidsc.ui.theme.IberdrolaGreen
import com.iberdrola.practicas2026.davidsc.ui.theme.StatusPagadofondo
import com.iberdrola.practicas2026.davidsc.ui.theme.StatusPagadotexto
import com.iberdrola.practicas2026.davidsc.ui.theme.StatusPendientepagofondo
import com.iberdrola.practicas2026.davidsc.ui.theme.StatusPendientepagotexto
import java.text.NumberFormat
import java.util.Locale

val nf = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

@Composable
fun LastInvoiceCard(invoice: Invoice, onClick: () -> Unit, modifier: Modifier = Modifier) { // 🔹 agregar onClick
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }, // 🔹
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent

        ),
        border = BorderStroke(1.3.dp, IberdrolaGreen),
        shape = CardDefaults.outlinedShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Columna con textos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.last_invoice),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = invoice.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 2.dp, bottom = 15.dp)
                )
                Text(
                    text = nf.format(invoice.amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = invoice.startDate,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                StatusBadge(
                    status = invoice.status,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            val icon = when (invoice.type) {
                InvoiceType.LUZ -> Icons.Outlined.Lightbulb
                InvoiceType.GAS -> Icons.Outlined.Whatshot
            }

            Icon(
                imageVector = icon,
                contentDescription = "icon",
                tint = IberdrolaGreen,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun InvoiceList(invoices: List<Invoice>, onClick: (Invoice) -> Unit) {
    LazyColumn {
        items(invoices) { invoice ->
            InvoiceItem(invoice = invoice, onClick = { onClick(invoice) })
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
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = invoice.startDate,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = invoice.description,
                style = MaterialTheme.typography.bodySmall,

                )
            StatusBadge(
                status = invoice.status,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text(
            text = "%.2f€".format(invoice.amount),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
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
    // Solo dos estados: Pagada y Pendiente de Pago
    val (bgColor, textColor) = when (status) {
        "Pagada" -> StatusPagadofondo to StatusPagadotexto
        "Pendiente de Pago" -> StatusPendientepagofondo to StatusPendientepagotexto
        else -> StatusPendientepagofondo to StatusPendientepagotexto // default al rojo
    }

    Text(
        text = status,
        color = textColor,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(color = bgColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun SkeletonList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp) //
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
                .width(30.dp)
                .height(30.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun InvoicesHeader(
    onBackClick: () -> Unit,
    useMock: Boolean,
    onToggleMock: () -> Unit,
    selectedStreet: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top =  WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                start = 16.dp,
                end = 16.dp,
                bottom = 12.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Atrás",
                    tint = IberdrolaGreen,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer { scaleX = -1f }
                )
            }

            Text(
                text = "Atrás",
                style = MaterialTheme.typography.bodyMedium,
                color = IberdrolaGreen,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onToggleMock,
                border = BorderStroke(2.dp, IberdrolaGreen),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = IberdrolaGreen
                )
            ) {
                Text(if (useMock) "Mock ON" else "Mock OFF")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.invoices_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = selectedStreet ?:  stringResource(R.string.invoices_subtitle),
            style = MaterialTheme.typography.titleMedium,
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
            color = if (selected) Color.Black else Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(40.dp) // ancho fijo, no fillMaxWidth
                .background(
                    if (selected) IberdrolaGreen else Color.Transparent
                )
        )
    }
}

@Composable
fun InvoiceListGroupedByYear(invoices: List<Invoice>, onClick: (Invoice) -> Unit) {
    val invoicesByYear = invoices.groupBy { it.startDate.take(4) } // Agrupar por año

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
fun SkeletonLastInvoiceCard( modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        border = BorderStroke(1.3.dp, Color.Gray),
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Simula icono
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFBDBDBD), RoundedCornerShape(20.dp))
                    .align(Alignment.End)
            )
            // Simula el título
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)  // mitad de ancho como ejemplo
                    .height(24.dp)
                    .background(Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Simula descripción
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(20.dp)
                    .background(Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Simula cantidad
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(24.dp)
                    .background(Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))


        }
    }
}

// Dummy invoices para preview
private val previewInvoices = listOf(
    Invoice(1, "2026-03-01", "2026-03-01","Factura Luz", 52.3, "Pagada", InvoiceType.LUZ, "C/larios"),
    Invoice(2, "2026-02-18", "2026-03-01","Factura Gas", 28.4, "Pendiente de Pago", InvoiceType.GAS,"C/larios"),
    Invoice(3, "2026-03-02", "2026-03-01","Factura Luz", 32.5, "Pagada", InvoiceType.LUZ, "C/larios")
)

@Preview(showBackground = true)
@Composable
fun PreviewLastInvoiceCard() {
    LastInvoiceCard(invoice = previewInvoices[0], onClick = {})
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
        useMock = true,       // o false, depende de cómo quieras mostrarlo
        onToggleMock = {}  ,   // dummy lambda para preview
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