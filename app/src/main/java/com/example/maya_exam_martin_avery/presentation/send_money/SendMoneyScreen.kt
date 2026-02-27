package com.example.maya_exam_martin_avery.presentation.send_money

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen(
    state: SendMoneyState,
    onNavigateUp: () -> Unit,
    onAmountChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismissSheet: () -> Unit,
    onDoneFromSheet: () -> Unit,
) {
    val formatPeso = remember {
        { value: Double -> String.format(Locale.US, "₱%,.2f", value) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Money") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "Enter Amount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.amountInput,
                onValueChange = onAmountChanged,
                enabled = !state.isLoading,
                singleLine = true,
                leadingIcon = { Text("₱", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Available balance: ${formatPeso(state.balance)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (state.screenErrorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.screenErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = onSubmit,
                enabled = !state.isLoading,
            ) {
                Text("Submit")
            }
        }
    }

    // Bottom sheet is driven by ViewModel state, so it survives recomposition.
    val sheet = state.sheet
    if (sheet != null) {
        ModalBottomSheet(onDismissRequest = onDismissSheet) {
            SheetContent(
                sheet = sheet,
                formatPeso = formatPeso,
                onDone = onDoneFromSheet,
            )
        }
    }
}

@Composable
private fun SheetContent(
    sheet: SendMoneySheetState,
    formatPeso: (Double) -> String,
    onDone: () -> Unit,
) {
    val (icon, iconTint, title, message) = when (sheet) {
        is SendMoneySheetState.Success -> {
            Quad(
                Icons.Filled.CheckCircle,
                MaterialTheme.colorScheme.primary,
                "Transaction Successful",
                "You sent ${formatPeso(sheet.sentAmount)}",
            )
        }
        is SendMoneySheetState.Failure -> {
            Quad(
                Icons.Filled.Error,
                MaterialTheme.colorScheme.error,
                "Transaction Failed",
                sheet.message,
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(72.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = onDone,
        ) {
            Text("Done")
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

private data class Quad(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tint: androidx.compose.ui.graphics.Color,
    val title: String,
    val message: String,
)

