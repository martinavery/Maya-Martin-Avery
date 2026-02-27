package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import javax.inject.Inject

class GetRemoteTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(): Result<List<TransactionDomain.RemoteSample>> {
        return transactionRepository.getRemoteSampleTransactions()
    }
}

