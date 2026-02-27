package com.example.maya_exam_martin_avery.presentation.my_wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.maya_exam_martin_avery.presentation.theme.MayaExamMartinAveryTheme

@Composable
fun WalletRoute(
    viewModel: WalletViewModel = hiltViewModel(),
    onSendMoneyClicked: () -> Unit,
    onViewTransactionsClicked: () -> Unit,
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        // When returning to Wallet via back stack, re-entering composition triggers a refresh.
        viewModel.refreshWallet()
    }

    WalletScreen(
        state = state.value,
        // Edge-to-edge is enabled; keep content out of system bars.
        modifier = Modifier.safeDrawingPadding(),
        onSendMoneyClicked = onSendMoneyClicked,
        onViewTransactionsClicked = onViewTransactionsClicked,
        onToggleBalanceVisibility = viewModel::onToggleBalanceVisibility
    )
}

@Composable
@Preview(showSystemUi = true)
fun WalletRoutePreview() {
    MayaExamMartinAveryTheme {
        WalletScreen(
            state = WalletState(),
            modifier = Modifier.safeDrawingPadding(),
            onSendMoneyClicked = {},
            onViewTransactionsClicked = {},
            onToggleBalanceVisibility = {}
        )
    }
}