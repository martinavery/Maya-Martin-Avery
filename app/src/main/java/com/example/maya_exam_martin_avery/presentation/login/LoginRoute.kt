package com.example.maya_exam_martin_avery.presentation.login

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginRoute(onLoginSuccess: () -> Unit, viewModel: LoginViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    LoginScreen(uiState = state.value)
}