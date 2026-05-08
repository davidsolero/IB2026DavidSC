package com.iberdrola.practicas2026.davidsc.ui.contract

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.davidsc.R
import androidx.core.net.toUri

@Composable
fun ContractEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    Column(modifier = modifier) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            cursorBrush = SolidColor(iberdrolaGreen),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            decorationBox = { innerTextField ->

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Asterisco SIEMPRE visible
                    Text(
                        text = "* ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )

        HorizontalDivider(
            color = if (value.isNotEmpty()) iberdrolaGreen else Color.Gray,
            thickness = 2.dp
        )
    }
}

@Composable
fun PrivacyInfoBlock(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.privacy_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        val context = LocalContext.current
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        TextWithMoreInfo(stringResource(R.string.privacy_responsible), onMoreInfoClick = {
            val intent = Intent(Intent.ACTION_VIEW, "https://www.iberdrola.es".toUri())
            context.startActivity(intent)
        })

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        TextWithMoreInfo(stringResource(R.string.privacy_purpose),onMoreInfoClick = {
            val intent = Intent(Intent.ACTION_VIEW, "https://www.iberdrola.es".toUri())
            context.startActivity(intent)
        })

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        TextWithMoreInfo(stringResource(R.string.privacy_rights),onMoreInfoClick = {
            val intent = Intent(Intent.ACTION_VIEW, "https://www.iberdrola.es".toUri())
            context.startActivity(intent)
        })
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
    }
}


@Composable
fun ContractNavigationButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean,
    nextText: String,
    modifier: Modifier = Modifier
) {
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    Row(modifier = modifier.fillMaxWidth()) {

        OutlinedButton(
            onClick = onPrevious,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = iberdrolaGreen
            ),
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                iberdrolaGreen
            )
        ) {
            Text(
                text = stringResource(R.string.nav_previous),
                color = iberdrolaGreen
            )
        }


        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_medium)))

        Button(
            onClick = onNext,
            enabled = nextEnabled,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = iberdrolaGreen)
        ) {
            Text(nextText)
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}


@Composable
fun TextWithMoreInfo(
    text: String,
    onMoreInfoClick: () -> Unit = {}
) {
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    FlowRow {
        Text(
            text = "$text ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )

        Text(
            text = "Más info",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                color = iberdrolaGreen
            ),
            modifier = Modifier.clickable { onMoreInfoClick() }
        )
    }
}