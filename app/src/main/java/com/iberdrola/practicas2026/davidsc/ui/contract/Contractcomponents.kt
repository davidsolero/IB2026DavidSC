package com.iberdrola.practicas2026.davidsc.ui.contracts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.davidsc.R

/**
 * Email or numeric input field styled to match the contract flow design.
 *
 * Uses an underline-only style (no full border) consistent with the
 * reference screens. [isNumeric] switches the keyboard to number pad,
 * used for the OTP field.
 */
@Composable
fun ContractEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isNumeric: Boolean = false
) {
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Email
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = iberdrolaGreen,
            focusedLabelColor = iberdrolaGreen,
            cursorColor = iberdrolaGreen,
            unfocusedBorderColor = Color.Gray
        )
    )
}

/**
 * Privacy information block shown on the activation screen.
 *
 * Displays the data protection notice required by the spec.
 * The "Más info" links are presentational only — no navigation action.
 */
@Composable
fun PrivacyInfoBlock(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.privacy_title),
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_xsmall)))

        Text(
            text = stringResource(R.string.privacy_responsible),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_xsmall)))

        Text(
            text = stringResource(R.string.privacy_purpose),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_xsmall)))

        Text(
            text = stringResource(R.string.privacy_rights),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
    }
}