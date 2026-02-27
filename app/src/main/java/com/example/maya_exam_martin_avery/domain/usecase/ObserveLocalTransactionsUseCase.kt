package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLocalTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(userId: Long): Flow<List<TransactionDomain.LocalSent>> {
        return transactionRepository.observeLocalTransactions(userId)
    }
}

