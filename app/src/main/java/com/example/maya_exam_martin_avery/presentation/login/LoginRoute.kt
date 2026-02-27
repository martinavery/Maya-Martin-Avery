package com.example.maya_exam_martin_avery.presentation.login

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginRoute(onLoginSuccess: () -> Unit, viewModel: LoginViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        // One-off navigation on successful login (avoids re-triggering on recomposition).
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is LoginEffect.NavigateToNext -> {
                    onLoginSuccess()
                }
            }
        }
    }

    LoginScreen(
        // Edge-to-edge is enabled; keep content out of system bars.
        modifier = Modifier.safeDrawingPadding(),
        uiState = state.value,
        onUserNameChange = { userName -> viewModel.setUsername(userName) },
        onPasswordChange = { password -> viewModel.setPassword(password) },
        onLoginClick = { viewModel.login() }
    )
}