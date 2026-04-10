package com.iberdrola.practicas2026.davidsc.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.navigation.NavController
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.core.utils.Screen

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val streets by viewModel.streets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val navigateToInvoices = { street: String? ->
        AppConfig.mockStreet = street
        navController.navigate(Screen.INVOICES)
    }

    LaunchedEffect(Unit) {
        viewModel.loadStreets()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.margin_medium))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.iberdrola_green)
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

            Text(
                text = stringResource(R.string.main_select_street),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            Button(
                onClick = { navigateToInvoices(null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.iberdrola_green)
                )
            ) {
                Text(
                    text = stringResource(R.string.main_all_streets),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))

            if (isLoading) {
                StreetsSkeleton()
            } else if (streets.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_streets_found),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.main_my_streets),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

                Card(
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = CardDefaults.outlinedShape
                ) {
                    LazyColumn {
                        items(streets) { street ->
                            StreetItem(
                                street = street,
                                onClick = { navigateToInvoices(street) }
                            )
                            if (street != streets.last()) {
                                HorizontalDivider(color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreetsSkeleton() {
    Column {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        // Fake title
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(14.dp)
                .background(
                    color = colorResource(R.color.skeleton_gray),
                    shape = RoundedCornerShape(4.dp)
                )
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        Card(
            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, Color.LightGray),
            shape = CardDefaults.outlinedShape
        ) {
            LazyColumn {
                items(5) {
                    StreetSkeletonItem()
                    HorizontalDivider(color = Color.LightGray)
                }
            }
        }
    }
}


@Composable
fun StreetSkeletonItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_medium),
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_small))
        ) {

            // Icon skeleton
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.icon_size_medium))
                    .background(
                        color = colorResource(R.color.skeleton_gray),
                        shape = RoundedCornerShape(6.dp)
                    )
            )

            // Street text skeleton
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(14.dp)
                    .background(
                        color = colorResource(R.color.skeleton_gray),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }

        // Arrow skeleton
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.icon_size_small))
                .background(
                    color = colorResource(R.color.skeleton_gray),
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}


@Composable
private fun StreetItem(street: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_medium),
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_small))
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = colorResource(R.color.iberdrola_green),
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
            )
            Text(
                text = street,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
        )
    }
}