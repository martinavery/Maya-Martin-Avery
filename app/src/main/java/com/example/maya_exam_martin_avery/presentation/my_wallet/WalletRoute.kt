package com.example.maya_exam_martin_avery.presentation.my_wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.maya_exam_martin_avery.presentation.theme.MayaExamMartinAveryTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WalletRoute(
    viewModel: WalletViewModel = hiltViewModel(),
    onSendMoneyClicked: () -> Unit,
    onViewTransactionsClicked: () -> Unit,
    onLogout: () -> Unit,
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        // When returning to Wallet via back stack, re-entering composition triggers a refresh.
        viewModel.refreshWallet()
    }

    LaunchedEffect(viewModel) {
        // One-off navigation events coming from user actions (e.g., logout).
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                WalletEffect.NavigateToLogin -> onLogout()
            }
        }
    }

    WalletScreen(
        state = state.value,
        onSendMoneyClicked = onSendMoneyClicked,
        onViewTransactionsClicked = onViewTransactionsClicked,
        onToggleBalanceVisibility = viewModel::onToggleBalanceVisibility,
        onLogoutClicked = viewModel::onLogout,
    )
}

@Composable
@Preview(showSystemUi = true)
fun WalletRoutePreview() {
    MayaExamMartinAveryTheme {
        WalletScreen(
            state = WalletState(),
            onSendMoneyClicked = {},
            onViewTransactionsClicked = {},
            onToggleBalanceVisibility = {},
            onLogoutClicked = {},
        )
    }
}