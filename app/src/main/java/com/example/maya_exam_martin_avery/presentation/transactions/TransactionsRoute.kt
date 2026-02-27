package com.example.maya_exam_martin_avery.presentation.transactions

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransactionsRoute(
    onNavigateUp: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    TransactionsScreen(
        state = state.value,
        onNavigateUp = onNavigateUp,
        onRefreshRemote = viewModel::refreshRemote,
    )
}

