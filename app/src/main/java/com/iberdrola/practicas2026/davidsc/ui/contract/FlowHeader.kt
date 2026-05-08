package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.ui.navigation.SafeNavController
import com.iberdrola.practicas2026.davidsc.ui.navigation.Screen

@Composable
fun FlowHeader(
    title: String,
    step: Int,
    totalSteps: Int,
    onClose: () -> Unit
) {
    val progress = step.toFloat() / totalSteps.coerceAtLeast(1)
    val green = colorResource(R.color.iberdrola_green)

    Column(modifier = Modifier.fillMaxWidth()) {

        Column(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(dimensionResource(R.dimen.margin_medium))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = green
                    )
                }
            }

            Text(
                text = title,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = green,
            trackColor = green.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))
    }
}