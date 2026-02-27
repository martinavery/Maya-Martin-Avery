package com.example.maya_exam_martin_avery.domain.repository

import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeLocalTransactions(userId: Long): Flow<List<TransactionDomain.LocalSent>>

    suspend fun saveLocalSentTransaction(
        userId: Long,
        amount: Double,
        description: String,
        createdAtEpochMs: Long,
    ): Result<Unit>

    suspend fun getRemoteSampleTransactions(): Result<List<TransactionDomain.RemoteSample>>

    suspend fun postRemoteSampleTransaction(
        userId: Long,
        amount: Double,
        description: String,
    ): Result<Unit>
}

