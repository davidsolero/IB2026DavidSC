package com.iberdrola.practicas2026.davidsc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iberdrola.practicas2026.davidsc.ui.contract.ActivateContractScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.ActiveContractScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.ConfirmationScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.ContractSelectionScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.ModifyEmailScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.OtpVerificationScreen
import com.iberdrola.practicas2026.davidsc.ui.invoices.FilterScreen
import com.iberdrola.practicas2026.davidsc.ui.invoices.InvoicesScreen
import com.iberdrola.practicas2026.davidsc.ui.main.MainScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    safeNav: SafeNavController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MAIN
    ) {
        composable(Screen.MAIN) {
            MainScreen(safeNav)
        }

        composable(Screen.INVOICES) {
            InvoicesScreen(safeNav)
        }

        composable(Screen.FILTER) {
            FilterScreen(navController, safeNav)
        }

        composable(Screen.CONTRACT_SELECTION) {
            ContractSelectionScreen(safeNav)
        }

        composable(
            route = Screen.ACTIVE_CONTRACT,
            arguments = listOf(navArgument("contractId") { type = NavType.StringType })
        ) { backStack ->
            ActiveContractScreen(
                contractId = backStack.arguments?.getString("contractId").orEmpty(),
                safeNav = safeNav
            )
        }

        composable(
            route = Screen.ACTIVATE_CONTRACT,
            arguments = listOf(navArgument("contractId") { type = NavType.StringType })
        ) { backStack ->
            ActivateContractScreen(
                contractId = backStack.arguments?.getString("contractId").orEmpty(),
                safeNav = safeNav
            )
        }

        composable(
            route = Screen.MODIFY_EMAIL,
            arguments = listOf(
                navArgument("contractId") { type = NavType.StringType },
                navArgument("currentEmail") { type = NavType.StringType }
            )
        ) { backStack ->
            val contractId = backStack.arguments?.getString("contractId").orEmpty()
            val currentEmail = java.net.URLDecoder.decode(
                backStack.arguments?.getString("currentEmail").orEmpty(), "UTF-8"
            )
            ModifyEmailScreen(
                contractId = contractId,
                currentEmail = currentEmail,
                safeNav = safeNav,
            )
        }

        composable(
            route = Screen.OTP_VERIFICATION,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("flow") { type = NavType.StringType }
            )
        ) { backStack ->
            val email = java.net.URLDecoder.decode(
                backStack.arguments?.getString("email").orEmpty(), "UTF-8"
            )
            val flow = backStack.arguments?.getString("flow").orEmpty()
            OtpVerificationScreen(
                email = email,
                flow = flow,
                safeNav = safeNav
            )
        }

        composable(
            route = Screen.CONFIRMATION,
            arguments = listOf(
                navArgument("flow") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStack ->
            val flow = backStack.arguments?.getString("flow").orEmpty()
            val email = java.net.URLDecoder.decode(
                backStack.arguments?.getString("email").orEmpty(), "UTF-8"
            )
            ConfirmationScreen(
                flow = flow,
                email = email,
                safeNav = safeNav,
                navController = navController
            )
        }
    }
}