package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import javax.inject.Inject

class PostTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(
        userId: Long,
        amount: Double,
        description: String,
    ): Result<Unit> {
        return transactionRepository.postRemoteSampleTransaction(
            userId = userId,
            amount = amount,
            description = description,
        )
    }
}

