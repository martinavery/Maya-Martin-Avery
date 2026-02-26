package com.example.maya_exam_martin_avery.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maya_exam_martin_avery.presentation.login.LoginRoute

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
        NavHost(navController = navController, startDestination = Login) {
            composable<Login> {
                LoginRoute(onLoginSuccess = {

                })
            }
        }
    }
}