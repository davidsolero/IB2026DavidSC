package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.OtpFlow
import com.iberdrola.practicas2026.davidsc.core.utils.Screen
import com.iberdrola.practicas2026.davidsc.ui.util.maskEmail

@Composable
fun ConfirmationScreen(
    flow: String,
    email: String,
    navController: NavController
) {
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)
    val isActivation = flow == OtpFlow.ACTIVATE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(iberdrolaGreen)
    ) {
        IconButton(
            onClick = {
                navController.navigate(Screen.CONTRACT_SELECTION) {
                    // Pop the entire contract flow off the back stack so the user
                    // returns to contract selection with a clean state.
                    popUpTo(Screen.CONTRACT_SELECTION) { inclusive = false }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimensionResource(R.dimen.margin_medium))
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.cancel),
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.margin_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            Text(
                text = if (isActivation) {
                    stringResource(R.string.confirmation_activated_title)
                } else {
                    stringResource(R.string.confirmation_modified_title)
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            Text(
                text = stringResource(R.string.confirmation_body, maskEmail(email)),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            Button(
                onClick = {
                    navController.navigate(Screen.CONTRACT_SELECTION) {
                        popUpTo(Screen.CONTRACT_SELECTION) { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = iberdrolaGreen
                )
            ) {
                Text(
                    text = stringResource(R.string.accept),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}