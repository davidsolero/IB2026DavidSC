package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.Screen
import com.iberdrola.practicas2026.davidsc.ui.contracts.ContractEmailField
import com.iberdrola.practicas2026.davidsc.ui.contracts.OtpViewModel
import com.iberdrola.practicas2026.davidsc.ui.invoices.BackButton

// Simulated masked phone number shown in the OTP instructions
private const val MASKED_PHONE = "*****146"

@Composable
fun OtpVerificationScreen(
    email: String,
    flow: String,
    navController: NavController,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val code by viewModel.code.collectAsState()
    val canContinue by viewModel.canContinue.collectAsState()
    val remainingResends by viewModel.remainingResends.collectAsState()
    val resendConfirmationVisible by viewModel.resendConfirmationVisible.collectAsState()
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
                text = stringResource(R.string.activate_contract_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            Text(
                text = stringResource(R.string.otp_introduce_code),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            Text(
                text = stringResource(R.string.otp_sent_to, MASKED_PHONE),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            ContractEmailField(
                value = code,
                onValueChange = { viewModel.onCodeChange(it) },
                label = stringResource(R.string.otp_field_label),
                isNumeric = true
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            ResendCodeBlock(
                remainingResends = remainingResends,
                onResend = { viewModel.resendCode() }
            )

            if (resendConfirmationVisible) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))
                ResendConfirmationBanner()
            }

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
                        navController.navigate(Screen.confirmation(flow, email)) {
                            // Clear the OTP screen from the back stack so the user
                            // cannot navigate back into the verification step after success.
                            popUpTo(Screen.OTP_VERIFICATION) { inclusive = true }
                        }
                    },
                    enabled = canContinue,
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

@Composable
private fun ResendCodeBlock(
    remainingResends: Int,
    onResend: () -> Unit
) {
    val canResend = remainingResends > 0

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_small)))
        Column {
            Text(
                text = stringResource(R.string.otp_not_received),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = stringResource(R.string.otp_resend_sms),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            if (!canResend) {
                Text(
                    text = stringResource(R.string.otp_no_resends_left),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = stringResource(R.string.otp_resends_remaining, remainingResends),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            TextButton(
                onClick = onResend,
                enabled = canResend
            ) {
                Text(
                    text = stringResource(R.string.otp_resend_link),
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = TextDecoration.Underline,
                    color = if (canResend) colorResource(R.color.iberdrola_green) else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ResendConfirmationBanner() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = colorResource(R.color.iberdrola_green),
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_small)))
        Text(
            text = stringResource(R.string.otp_resend_confirmation),
            style = MaterialTheme.typography.bodySmall,
            color = colorResource(R.color.iberdrola_green)
        )
    }
}