package com.example.maya_exam_martin_avery.presentation.start

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun StartRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToWallet: () -> Unit,
    viewModel: StartViewModel = hiltViewModel(),
) {
    // One-off routing decision on app start. The screen shows a minimal loading UI
    // while navigation is performed.
    LaunchedEffect(viewModel.destination) {
        when (viewModel.destination) {
            StartDestination.Login -> onNavigateToLogin()
            StartDestination.Wallet -> onNavigateToWallet()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

