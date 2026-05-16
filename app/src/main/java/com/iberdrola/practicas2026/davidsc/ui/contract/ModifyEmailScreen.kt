package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.OtpFlow
import com.iberdrola.practicas2026.davidsc.ui.navigation.SafeNavController
import com.iberdrola.practicas2026.davidsc.ui.navigation.Screen
import com.iberdrola.practicas2026.davidsc.ui.util.isValidEmail
@Composable
fun ModifyEmailScreen(
    contractId: String,
    currentEmail: String,
    safeNav: SafeNavController
) {
    var email by remember { mutableStateOf("") }
    var isExiting by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FlowHeader(
                title = stringResource(R.string.modify_email_title),
                step = 1,
                totalSteps = 2,
                onClose = {
                    if (!isExiting) {
                        isExiting = true
                        safeNav.navigate(Screen.CONTRACT_SELECTION) {
                            popUpTo(Screen.MAIN) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.margin_medium))
            ) {
                Text(
                    text = stringResource(R.string.activate_contract_email_question),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))
                ContractEmailField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = stringResource(R.string.email_placeholder)
                )
                Spacer(modifier = Modifier.weight(1f))
                ContractNavigationButtons(
                    onPrevious = {
                        if (!isExiting) {
                            isExiting = true
                            safeNav.popBackStack()
                        }
                    },
                    onNext = {
                        if (!isExiting) {
                            isExiting = true
                            safeNav.navigate(Screen.otpVerification(email, OtpFlow.MODIFY))
                        }
                    },
                    nextEnabled = isValidEmail(email),
                    nextText = stringResource(R.string.nav_next)
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
            }
        }
    }
}