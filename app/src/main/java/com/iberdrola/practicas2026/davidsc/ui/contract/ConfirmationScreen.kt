package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.OtpFlow
import com.iberdrola.practicas2026.davidsc.ui.navigation.SafeNavController
import com.iberdrola.practicas2026.davidsc.ui.navigation.Screen
import com.iberdrola.practicas2026.davidsc.ui.util.maskEmail

@Composable
fun ConfirmationScreen(
    flow: String,
    email: String,
    safeNav: SafeNavController,
    navController: NavController,
    viewModel: ContractDetailViewModel = hiltViewModel(
        navController.getBackStackEntry(Screen.ACTIVE_CONTRACT)
    )
) {

    LaunchedEffect(Unit) {
        if (flow == OtpFlow.MODIFY) {
            viewModel.commitEmail(email)
        }
    }

    var isExiting by remember { mutableStateOf(false) }

    val navigateToSelection: () -> Unit = {
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

    ConfirmationScreenContent(
        flow = flow,
        email = email,
        onClose = { navigateToSelection() },
        onAccept = { navigateToSelection() }
    )
}

@Composable
fun ConfirmationScreenContent(
    flow: String,
    email: String,
    onClose: () -> Unit,
    onAccept: () -> Unit
) {
    val green = colorResource(R.color.modified_email)
    val isActivation = flow == OtpFlow.ACTIVATE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(green)
    ) {

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimensionResource(R.dimen.margin_medium))
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.margin_large)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.iberdrolathumbs),
                contentDescription = null,
                modifier = Modifier.size(220.dp)
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            Text(
                text = if (isActivation) {
                    stringResource(R.string.confirmation_activated_title)
                } else {
                    stringResource(R.string.confirmation_modified_title)
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
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
        }

        Button(
            onClick = onAccept,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.icon_size_large),
                    end = dimensionResource(R.dimen.icon_size_large),
                    bottom = 70.dp
                )
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = green
            )
        ) {
            Text(
                text = stringResource(R.string.accept),
                color = colorResource(R.color.iberdrola_green),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmationScreenPreview() {
    ConfirmationScreenContent(
        flow = OtpFlow.ACTIVATE,
        email = "usuario@correo.com",
        onClose = {},
        onAccept = {}
    )
}