package com.example.maya_exam_martin_avery.data.repository

import com.example.maya_exam_martin_avery.data.local.dao.TransactionDao
import com.example.maya_exam_martin_avery.data.local.entities.TransactionEntity
import com.example.maya_exam_martin_avery.data.remote.TransactionsApi
import com.example.maya_exam_martin_avery.data.remote.dto.PostRemoteTransactionBodyDto
import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionsApi: TransactionsApi,
) : TransactionRepository {

    override fun observeLocalTransactions(userId: Long): Flow<List<TransactionDomain.LocalSent>> {
        return transactionDao.observeByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveLocalSentTransaction(
        userId: Long,
        amount: Double,
        description: String,
        createdAtEpochMs: Long,
    ): Result<Unit> {
        return runCatching {
            transactionDao.insert(
                TransactionEntity(
                    userId = userId,
                    amount = amount,
                    description = description,
                    type = TYPE_SENT,
                    createdAtEpochMs = createdAtEpochMs,
                ),
            )
            Unit
        }
    }

    override suspend fun getRemoteSampleTransactions(): Result<List<TransactionDomain.RemoteSample>> {
        return runCatching {
            // JSONPlaceholder uses `/posts` as sample data; map into a \"remote sample\" transaction model.
            transactionsApi.getTransactions().map { dto ->
                TransactionDomain.RemoteSample(
                    id = "remote-${dto.id}",
                    userId = dto.userId,
                    title = dto.title,
                    body = dto.body,
                )
            }
        }
    }

    override suspend fun postRemoteSampleTransaction(
        userId: Long,
        amount: Double,
        description: String,
    ): Result<Unit> {
        return runCatching {
            // POST is best-effort: JSONPlaceholder won't retain writes, but this satisfies the API POST requirement.
            transactionsApi.postTransaction(
                PostRemoteTransactionBodyDto(
                    userId = userId,
                    title = "Sent ₱%.2f".format(amount),
                    body = description,
                ),
            )
            Unit
        }
    }

    private fun TransactionEntity.toDomain(): TransactionDomain.LocalSent {
        return TransactionDomain.LocalSent(
            id = "local-$id",
            userId = userId,
            amount = amount,
            description = description,
            createdAtEpochMs = createdAtEpochMs,
        )
    }

    private companion object {
        const val TYPE_SENT = "SENT"
    }
}

