package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.OtpFlow
import com.iberdrola.practicas2026.davidsc.core.utils.Screen
import com.iberdrola.practicas2026.davidsc.ui.contracts.ContractDetailViewModel
import com.iberdrola.practicas2026.davidsc.ui.contracts.ContractEmailField
import com.iberdrola.practicas2026.davidsc.ui.contracts.PrivacyInfoBlock
import com.iberdrola.practicas2026.davidsc.ui.invoices.BackButton
import com.iberdrola.practicas2026.davidsc.ui.util.maskEmail

@Composable
fun ActivateContractScreen(
    contractId: String,
    navController: NavController,
    viewModel: ContractDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(contractId) {
        viewModel.loadContract(contractId)
    }

    val contract by viewModel.contract.collectAsState()
    val email by viewModel.email.collectAsState()
    val legalChecked by viewModel.legalChecked.collectAsState()
    val canContinue by viewModel.canContinue.collectAsState()
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.margin_medium))
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            BackButton(onClick = { navController.popBackStack() })

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            Text(
                text = stringResource(R.string.activate_contract_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            contract?.let { c ->
                if (!c.email.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.activate_contract_linked_email),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = maskEmail(c.email),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))
                }
            }

            Text(
                text = stringResource(R.string.activate_contract_email_question),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            ContractEmailField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = stringResource(R.string.field_email)
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            PrivacyInfoBlock()

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = legalChecked,
                    onCheckedChange = { viewModel.onLegalCheckedChange(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = iberdrolaGreen,
                        checkmarkColor = Color.White,
                        uncheckedColor = iberdrolaGreen
                    )
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_xsmall)))
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.activate_contract_legal_prefix))
                        append(" ")
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append(stringResource(R.string.activate_contract_legal_conditions))
                        }
                        append(" ")
                        append(stringResource(R.string.activate_contract_legal_suffix))
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

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
                            Screen.otpVerification(email, OtpFlow.ACTIVATE)
                        )
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