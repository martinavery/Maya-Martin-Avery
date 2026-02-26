package com.example.maya_exam_martin_avery.presentation.my_wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maya_exam_martin_avery.presentation.theme.MayaExamMartinAveryTheme

@Composable
fun WalletScreen(
    state: WalletState,
    modifier: Modifier = Modifier,
    onSendMoneyClicked: () -> Unit
) {
    val buttonStyling = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(horizontal = 16.dp)

    Column(modifier = modifier.fillMaxSize()) {
        AvailableBalanceCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        SendMoneyButton(
            modifier = buttonStyling,
            onSendMoneyClicked = onSendMoneyClicked
        )
        Spacer(modifier = Modifier.height(16.dp))
        ViewTransactionsButton(modifier = buttonStyling, onViewTransacClicked = {})
    }
}

@Composable
fun AvailableBalanceCard(modifier: Modifier = Modifier, balance: Double = 0.0) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Available Balance")
                Spacer(modifier = Modifier.width(8.dp))
                Text(balance.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                modifier = Modifier
                    .width(16.dp)
                    .height(16.dp), onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.VisibilityOff,
                    contentDescription = "Toggle for balance"
                )
            }
        }
    }
}

@Composable
fun SendMoneyButton(modifier: Modifier = Modifier, onSendMoneyClicked: () -> Unit = {}) {
    Button(modifier = modifier, onClick = onSendMoneyClicked) {
        Text("Send Money")
    }
}

@Composable
fun ViewTransactionsButton(modifier: Modifier = Modifier, onViewTransacClicked: () -> Unit = {}) {
    OutlinedButton(modifier = modifier, onClick = onViewTransacClicked) {
        Text("View Transactions")
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun WalletPreview() {
    MayaExamMartinAveryTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // Use Scaffold padding in preview to avoid drawing under the status bar.
            WalletScreen(
                state = WalletState(),
                modifier = Modifier.padding(innerPadding),
                onSendMoneyClicked = {}
            )
        }
    }
}