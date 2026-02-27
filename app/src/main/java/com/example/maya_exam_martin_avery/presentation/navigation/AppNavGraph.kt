package com.example.maya_exam_martin_avery.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maya_exam_martin_avery.presentation.login.LoginRoute
import com.example.maya_exam_martin_avery.presentation.my_wallet.WalletRoute
import com.example.maya_exam_martin_avery.presentation.send_money.SendMoneyRoute
import com.example.maya_exam_martin_avery.presentation.start.StartRoute

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // Apply Scaffold insets (status/navigation bars) so screens don't render under system UI.
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                WalletRoute(onSendMoneyClicked = {
                    navController.navigate(SendMoney) {
                        launchSingleTop = true
                    }
                })
            }
            composable<SendMoney> {
                SendMoneyRoute(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.popBackStack() },
                )
            }
        }
    }
}