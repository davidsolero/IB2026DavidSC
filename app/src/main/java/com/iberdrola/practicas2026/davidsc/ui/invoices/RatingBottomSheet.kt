package com.iberdrola.practicas2026.davidsc.ui.invoices


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.ui.theme.IberdrolaGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingBottomSheet(
    onRated: (Int) -> Unit,
    onLater: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.rating_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.rating_question),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                val icons = listOf(
                    Icons.Outlined.SentimentVeryDissatisfied,
                    Icons.Outlined.SentimentDissatisfied,
                    Icons.Outlined.SentimentNeutral,
                    Icons.Outlined.SentimentSatisfied,
                    Icons.Outlined.SentimentVerySatisfied
                )

                val colors = listOf(
                    Color.Red,          // Muy enojado
                    Color(0xFFAC6A2F),       // Triste
                    Color.Gray,         // Neutral
                    Color(0xFF226999),         // Satisfecho
                    IberdrolaGreen         // Muy satisfecho
                )

                icons.forEachIndexed { i, icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = "rating $i",
                        tint = colors[i],
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onRated(i + 1) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onLater) {
                Text(
                    text = stringResource(R.string.rating_later),
                    color= IberdrolaGreen,
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 480)
@Composable
fun PreviewRatingBottomSheet() {
    RatingBottomSheet(
        onRated = {},
        onLater = {},
        onDismiss = {}
    )
}