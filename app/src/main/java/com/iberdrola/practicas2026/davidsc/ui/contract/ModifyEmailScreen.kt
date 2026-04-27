package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.OtpFlow
import com.iberdrola.practicas2026.davidsc.core.utils.Screen
import com.iberdrola.practicas2026.davidsc.ui.contracts.ContractEmailField
import com.iberdrola.practicas2026.davidsc.ui.invoices.BackButton
import com.iberdrola.practicas2026.davidsc.ui.util.isValidEmail

/**
 * Allows the user to enter a new email address for electronic invoicing.
 *
 * This screen does not require a ViewModel because its only state is the
 * email input field, which is local UI state. Validation is stateless.
 */
@Composable
fun ModifyEmailScreen(
    contractId: String,
    currentEmail: String,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.margin_medium))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            BackButton(onClick = { navController.popBackStack() })

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            Text(
                text = stringResource(R.string.modify_email_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            Text(
                text = stringResource(R.string.activate_contract_email_question),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            ContractEmailField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.modify_email_field_label)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.nav_previous))
                }

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_medium)))

                Button(
                    onClick = {
                        navController.navigate(
                            Screen.otpVerification(email, OtpFlow.MODIFY)
                        )
                    },
                    enabled = isValidEmail(email),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = iberdrolaGreen)
                ) {
                    Text(stringResource(R.string.nav_next))
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
        }
    }
}