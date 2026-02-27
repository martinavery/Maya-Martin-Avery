package com.example.maya_exam_martin_avery.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maya_exam_martin_avery.presentation.login.LoginRoute
import com.example.maya_exam_martin_avery.presentation.my_wallet.WalletRoute
import com.example.maya_exam_martin_avery.presentation.send_money.SendMoneyRoute
import com.example.maya_exam_martin_avery.presentation.start.StartRoute
import com.example.maya_exam_martin_avery.presentation.transactions.TransactionsRoute

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    // Each destination is responsible for applying its own insets (edge-to-edge is enabled).
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        // Route through a tiny startup gate to decide if we have a saved user.
        startDestination = Start
    ) {
        composable<Start> {
            StartRoute(
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Start) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToWallet = {
                    navController.navigate(Wallet) {
                        popUpTo(Start) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable<Login> {
            LoginRoute(onLoginSuccess = {
                navController.navigate(Wallet) {
                    // Remove Login from the back stack after successful login.
                    popUpTo(Login) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable<Wallet> {
            WalletRoute(
                onSendMoneyClicked = {
                    navController.navigate(SendMoney) {
                        launchSingleTop = true
                    }
                },
                onViewTransactionsClicked = {
                    navController.navigate(Transactions) {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    // Logout: return to Login and clear Wallet from the back stack.
                    navController.navigate(Login) {
                        popUpTo(Wallet) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable<SendMoney> {
            SendMoneyRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.popBackStack() },
            )
        }
        composable<Transactions> {
            TransactionsRoute(onNavigateUp = { navController.popBackStack() })
        }
    }
}