package com.example.maya_exam_martin_avery.presentation.my_wallet

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.maya_exam_martin_avery.presentation.theme.MayaExamMartinAveryTheme

@Composable
fun WalletRoute(viewModel: WalletViewModel = hiltViewModel(), onSendMoneyClicked: () -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    WalletScreen(state.value, onSendMoneyClicked = {})
}

@Composable
@Preview(showSystemUi = true)
fun WalletRoutePreview() {
    MayaExamMartinAveryTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            WalletScreen(
                state = WalletState(),
                modifier = Modifier.padding(innerPadding),
                onSendMoneyClicked = {}
            )
        }
    }
}