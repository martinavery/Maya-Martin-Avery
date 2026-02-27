package com.example.maya_exam_martin_avery.presentation.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    state: TransactionsState,
    onNavigateUp: () -> Unit,
    onRefreshRemote: () -> Unit,
) {
    val formatPeso = remember { { value: Double -> String.format(Locale.US, "₱%,.2f", value) } }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.US) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRefreshRemote, enabled = !state.isRemoteLoading) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.screenErrorMessage.isNotBlank()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    text = state.screenErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    SectionHeader(title = "Your transactions")
                }
                if (state.localTransactions.isEmpty()) {
                    item {
                        EmptyRow("No local transactions yet.")
                    }
                } else {
                    items(state.localTransactions, key = { it.id }) { txn ->
                        LocalTransactionRow(
                            txn = txn,
                            formatPeso = formatPeso,
                            dateFormat = dateFormat,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun EmptyRow(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun LocalTransactionRow(
    txn: TransactionDomain.LocalSent,
    formatPeso: (Double) -> String,
    dateFormat: SimpleDateFormat,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Sent",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatPeso(txn.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = txn.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = dateFormat.format(Date(txn.createdAtEpochMs)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RemoteTransactionRow(txn: TransactionDomain.RemoteSample) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = txn.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = txn.body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
        )
    }
}

