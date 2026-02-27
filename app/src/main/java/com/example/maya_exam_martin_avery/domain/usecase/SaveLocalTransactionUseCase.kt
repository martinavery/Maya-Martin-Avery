package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import javax.inject.Inject

class SaveLocalTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(
        userId: Long,
        amount: Double,
        description: String,
        createdAtEpochMs: Long,
    ): Result<Unit> {
        return transactionRepository.saveLocalSentTransaction(
            userId = userId,
            amount = amount,
            description = description,
            createdAtEpochMs = createdAtEpochMs,
        )
    }
}

