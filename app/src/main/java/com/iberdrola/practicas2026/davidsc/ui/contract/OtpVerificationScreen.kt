package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.Screen

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
    val isResending by viewModel.isResending.collectAsState()
    BackHandler(enabled = isResending) {
        // no hacemos nada -> bloquea back
    }
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                FlowHeader(
                    title = stringResource(R.string.activate_contract_title),
                    step = 2,
                    totalSteps = 3
                )

                // =======================
                // CONTENT (con padding)
                // =======================
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.margin_medium))
                ) {

                    Text(
                        text = stringResource(R.string.otp_introduce_code),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

                    Text(
                        text = stringResource(R.string.otp_sent_to, MASKED_PHONE),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

                    ContractEmailField(
                        value = code,
                        onValueChange = { viewModel.onCodeChange(it) },
                        placeholder = stringResource(R.string.otp_field_label)
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.icon_size_large)))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = dimensionResource(R.dimen.margin_large),
                            bottomStart = dimensionResource(R.dimen.margin_large),
                            bottomEnd = dimensionResource(R.dimen.margin_large)
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.otp_box)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        ResendCodeBlock(
                            remainingResends = remainingResends,
                            onResend = { viewModel.resendCode() },
                            modifier = Modifier.padding(dimensionResource(R.dimen.margin_medium))
                        )
                    }
                }

                // =======================
                // FOOTER (FULL WIDTH)
                // =======================
                Column(modifier = Modifier.fillMaxWidth()) {

                    if (resendConfirmationVisible) {
                        ResendConfirmationBanner(
                            onClose = { viewModel.hideResendConfirmation() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp) // separación con botones
                        )
                    }

                    ContractNavigationButtons(
                        onPrevious = { navController.popBackStack() },
                        onNext = {
                            navController.navigate(Screen.confirmation(flow, email)) {
                                popUpTo(Screen.OTP_VERIFICATION) { inclusive = true }
                            }
                        },
                        nextEnabled = canContinue,
                        nextText = stringResource(R.string.nav_next),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(R.dimen.margin_medium))
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
                }
            }
        }

        // =======================
        // LOADING OVERLAY
        // =======================
        if (isResending) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        enabled = true,
                        indication = null,
                        interactionSource = remember {
                            androidx.compose.foundation.interaction.MutableInteractionSource()
                        }
                    ) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(R.color.status_pagado_texto),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}

@Composable
private fun ResendCodeBlock(
    remainingResends: Int,
    onResend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canResend = remainingResends > 0

    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_small)))
        Column {
            Text(
                text = stringResource(R.string.otp_not_received),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))
            Text(
                text = stringResource(R.string.otp_resend_sms),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
            if (canResend) {
                Text(
                    text = stringResource(R.string.otp_resends_remaining, remainingResends),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            } else {
                Text(
                    text = stringResource(R.string.otp_no_resends_left),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))
            Text(
                text = stringResource(R.string.otp_resend_link),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    color = if (canResend) Color.Black else Color.Gray
                ),
                modifier = Modifier.clickable(
                    enabled = canResend
                ) {
                    onResend()
                }
            )
        }
    }
}


@Composable
fun ResendConfirmationBanner(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    Row(
        modifier = modifier
            .background(colorResource(R.color.status_pagado_fondo))
            .padding(
                horizontal = dimensionResource(R.dimen.margin_medium),
                vertical = dimensionResource(R.dimen.margin_small)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = iberdrolaGreen,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_small)))

        Text(
            text = stringResource(R.string.otp_resend_confirmation),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = iberdrolaGreen
            )
        }
    }
}