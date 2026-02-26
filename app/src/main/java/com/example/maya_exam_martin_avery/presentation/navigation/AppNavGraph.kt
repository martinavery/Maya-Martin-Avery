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
            startDestination = Login
        ) {
            composable<Login> {
                LoginRoute(onLoginSuccess = {
                    navController.navigate(Wallet) {
                        popUpTo(Login) { inclusive = true }
                    }
                })
            }
            composable<Wallet> {
                WalletRoute(onSendMoneyClicked = {})
            }
        }
    }
}