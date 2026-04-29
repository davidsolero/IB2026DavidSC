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
        ) {

            FlowHeader(
                title = stringResource(R.string.modify_email_title),
                step = 1,
                totalSteps = 2
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
                    placeholder = "Nuevo email"
                )

                Spacer(modifier = Modifier.weight(1f))

                ContractNavigationButtons(
                    onPrevious = { navController.popBackStack() },
                    onNext = {
                        navController.navigate(Screen.otpVerification(email, OtpFlow.MODIFY))
                    },
                    nextEnabled = isValidEmail(email),
                    nextText = stringResource(R.string.nav_next)
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
            }
        }
    }
}