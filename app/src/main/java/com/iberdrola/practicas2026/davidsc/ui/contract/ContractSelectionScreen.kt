package com.iberdrola.practicas2026.davidsc.ui.contract

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.ui.navigation.Screen
import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.model.ContractType
import com.iberdrola.practicas2026.davidsc.ui.invoices.BackButton
import com.iberdrola.practicas2026.davidsc.ui.navigation.SafeNavController

@Composable
fun ContractSelectionScreen(
    safeNav: SafeNavController,
    viewModel: ContractSelectionViewModel = hiltViewModel()
) {
    val contracts by viewModel.contracts.collectAsState()
    var isExiting by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.margin_medium))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            BackButton(onClick = {
                if (!isExiting) {
                    isExiting = true
                    safeNav.popBackStack()
                }
            })

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            Text(
                text = stringResource(R.string.contract_selection_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            LazyColumn {
                items(contracts) { contract ->
                    ContractItem(
                        contract = contract,
                        onClick = {
                            if (!isExiting) {
                                isExiting = true
                                val route = if (contract.isActive) {
                                    Screen.activeContract(contract.id)
                                } else {
                                    Screen.activateContract(contract.id)
                                }
                                safeNav.navigate(route)
                            }
                        }
                    )
                    HorizontalDivider(color = Color.LightGray)
                }
            }
        }
    }
}
@Composable
private fun ContractItem(
    contract: Contract,
    onClick: () -> Unit
) {
    val iberdrolaGreen = colorResource(R.color.iberdrola_green)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_medium),
                vertical = dimensionResource(R.dimen.margin_medium)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = if (contract.type == ContractType.LUZ) {
                Icons.Outlined.Lightbulb
            } else {
                Icons.Outlined.Whatshot
            },
            contentDescription = null,
            tint = iberdrolaGreen,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large))
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_medium)))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = if (contract.type == ContractType.LUZ) {
                    stringResource(R.string.contract_luz)
                } else {
                    stringResource(R.string.contract_gas)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            ContractStatusBadge(isActive = contract.isActive)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
        )
    }
}

@Composable
fun ContractStatusBadge(isActive: Boolean) {
    val bgColor = if (isActive) {
        colorResource(R.color.status_pagado_fondo)
    } else {
        colorResource(R.color.status_anulada_fondo)
    }

    val textColor = if (isActive) {
        colorResource(R.color.status_pagado_texto)
    } else {
        colorResource(R.color.status_anulada_texto)
    }

    Text(
        text = if (isActive) "Activo" else "Sin activar",
        color = textColor,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.status_badge_radius))
            )
            .padding(
                horizontal = dimensionResource(R.dimen.margin_small),
                vertical = dimensionResource(R.dimen.margin_xsmall)
            )
    )
}
