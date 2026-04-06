package com.iberdrola.practicas2026.davidsc.ui.invoices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.davidsc.R


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
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_large),
                    vertical = dimensionResource(R.dimen.margin_medium)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.rating_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))
            Text(
                text = stringResource(R.string.rating_question),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                val icons = listOf(
                    Icons.Outlined.SentimentVeryDissatisfied,
                    Icons.Outlined.SentimentDissatisfied,
                    Icons.Outlined.SentimentNeutral,
                    Icons.Outlined.SentimentSatisfied,
                    Icons.Outlined.SentimentVerySatisfied
                )

                // Colors map 1-to-1 with the sentiment scale: very negative to very positive.
                val colors = listOf(
                    colorResource(R.color.rating_very_negative),
                    colorResource(R.color.rating_negative),
                    colorResource(R.color.rating_neutral),
                    colorResource(R.color.rating_positive),
                    colorResource(R.color.iberdrola_green)
                )

                icons.forEachIndexed { index, icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = "Rating ${index + 1} of 5",
                        tint = colors[index],
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.icon_size_large))
                            .clickable { onRated(index + 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))
            TextButton(onClick = onLater) {
                Text(
                    text = stringResource(R.string.rating_later),
                    color =  colorResource(R.color.iberdrola_green),
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))
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