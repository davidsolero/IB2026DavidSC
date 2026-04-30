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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.ui.invoices.BackButton
import com.iberdrola.practicas2026.davidsc.ui.navigation.SafeNavController
import com.iberdrola.practicas2026.davidsc.ui.navigation.Screen
import com.iberdrola.practicas2026.davidsc.ui.util.maskEmail

@Composable
fun ActiveContractScreen(
    contractId: String, safeNav: SafeNavController,
    viewModel: ContractDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(contractId) {
        viewModel.loadContract(contractId)
    }

    val contract by viewModel.contract.collectAsState()
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.margin_medium))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            BackButton(onClick = {safeNav.popBackStack() })

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            contract?.let { c ->
                Text(
                    text = stringResource(R.string.contract_luz),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

                Text(
                    text = c.address,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

                Text(
                    text = stringResource(R.string.active_contract_receives_here),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.icon_size_medium)))
                Text(
                    text = stringResource(R.string.here_in_this_mail),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

                Text(
                    text = c.email?.let { maskEmail(it) }
                        ?: stringResource(R.string.no_email_available),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
                HorizontalDivider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
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
                    Text(
                        text = stringResource(R.string.active_contract_disclaimer),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val email = c.email ?: ""
                        safeNav.navigate(Screen.modifyEmail(c.id, email))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = iberdrolaGreen)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_small)))
                    Text(
                        text = stringResource(R.string.active_contract_modify_email),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
            }
        }
    }
}