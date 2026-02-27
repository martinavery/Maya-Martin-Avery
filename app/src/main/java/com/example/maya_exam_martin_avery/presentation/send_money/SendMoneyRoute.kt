package com.example.maya_exam_martin_avery.presentation.send_money

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SendMoneyRoute(
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SendMoneyViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        // One-off navigation after a successful send (avoids re-triggering on recomposition).
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is SendMoneyEffect.NavigateBackToWallet -> onNavigateBack()
            }
        }
    }

    SendMoneyScreen(
        state = state.value,
        onNavigateUp = onNavigateUp,
        onAmountChanged = viewModel::onAmountChanged,
        onSubmit = viewModel::onSubmit,
        onDismissSheet = viewModel::onDismissSheet,
        onDoneFromSheet = viewModel::onDoneFromSheet,
    )
}

