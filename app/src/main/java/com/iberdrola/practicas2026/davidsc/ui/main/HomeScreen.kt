package com.iberdrola.practicas2026.davidsc.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R

@Composable
fun HomeScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.margin_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido a IB2026 DavidSC",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = { navController.navigate("invoices") }, // navega a pantalla de facturas
            modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_large))
        ) {
            Text(text = "Ver facturas")
        }
    }
}