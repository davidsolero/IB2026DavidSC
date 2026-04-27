package com.iberdrola.practicas2026.davidsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iberdrola.practicas2026.davidsc.core.utils.Screen
import com.iberdrola.practicas2026.davidsc.ui.contract.ActivateContractScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.ActiveContractScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.ConfirmationScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.ModifyEmailScreen
import com.iberdrola.practicas2026.davidsc.ui.contract.OtpVerificationScreen
import com.iberdrola.practicas2026.davidsc.ui.contracts.ContractSelectionScreen
import com.iberdrola.practicas2026.davidsc.ui.invoices.FilterScreen
import com.iberdrola.practicas2026.davidsc.ui.invoices.InvoicesScreen
import com.iberdrola.practicas2026.davidsc.ui.main.MainScreen
import com.iberdrola.practicas2026.davidsc.ui.theme.IB2026DavidSCTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IB2026DavidSCTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.MAIN
                ) {
                    composable(Screen.MAIN) {
                        MainScreen(navController = navController)
                    }
                    composable(Screen.INVOICES) {
                        InvoicesScreen(navController = navController)
                    }
                    composable(Screen.FILTER) {
                        FilterScreen(navController = navController)
                    }

                    // --- Electronic invoice flow ---

                    composable(Screen.CONTRACT_SELECTION) {
                        ContractSelectionScreen(navController = navController)
                    }
                    composable(
                        route = Screen.ACTIVE_CONTRACT,
                        arguments = listOf(navArgument("contractId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        ActiveContractScreen(
                            contractId = backStackEntry.arguments?.getString("contractId").orEmpty(),
                            navController = navController
                        )
                    }
                    composable(
                        route = Screen.ACTIVATE_CONTRACT,
                        arguments = listOf(navArgument("contractId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        ActivateContractScreen(
                            contractId = backStackEntry.arguments?.getString("contractId").orEmpty(),
                            navController = navController
                        )
                    }
                    composable(
                        route = Screen.MODIFY_EMAIL,
                        arguments = listOf(
                            navArgument("contractId") { type = NavType.StringType },
                            navArgument("currentEmail") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->

                        val contractId = backStackEntry.arguments?.getString("contractId").orEmpty()

                        val currentEmail = URLDecoder.decode(
                            backStackEntry.arguments?.getString("currentEmail").orEmpty(),
                            "UTF-8"
                        )

                        ModifyEmailScreen(
                            contractId = contractId,
                            currentEmail = currentEmail,
                            navController = navController
                        )
                    }
                    composable(
                        route = Screen.OTP_VERIFICATION,
                        arguments = listOf(
                            navArgument("email") { type = NavType.StringType },
                            navArgument("flow") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val email = URLDecoder.decode(
                            backStackEntry.arguments?.getString("email").orEmpty(), "UTF-8"
                        )
                        val flow = backStackEntry.arguments?.getString("flow").orEmpty()
                        OtpVerificationScreen(
                            email = email,
                            flow = flow,
                            navController = navController
                        )
                    }
                    composable(
                        route = Screen.CONFIRMATION,
                        arguments = listOf(
                            navArgument("flow") { type = NavType.StringType },
                            navArgument("email") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val flow = backStackEntry.arguments?.getString("flow").orEmpty()
                        val email = URLDecoder.decode(
                            backStackEntry.arguments?.getString("email").orEmpty(), "UTF-8"
                        )
                        ConfirmationScreen(
                            flow = flow,
                            email = email,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}