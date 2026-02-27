package com.example.maya_exam_martin_avery.presentation.transactions

import com.example.maya_exam_martin_avery.domain.model.TransactionDomain

data class TransactionsState(
    val isRemoteLoading: Boolean = false,
    val localTransactions: List<TransactionDomain.LocalSent> = emptyList(),
    val remoteTransactions: List<TransactionDomain.RemoteSample> = emptyList(),
    val screenErrorMessage: String = "",
)

