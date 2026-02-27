package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveLocalTransactionsUseCaseTest {
    private val transactionRepository: TransactionRepository = mockk()
    private val useCase = ObserveLocalTransactionsUseCase(transactionRepository)

    @Test
    fun `invoke returns repository flow`() = runTest {
        val userId = 123L
        val expected = listOf(
            TransactionDomain.LocalSent(
                id = "local-1",
                userId = userId,
                amount = 1.23,
                description = "Sent ₱1.23",
                createdAtEpochMs = 1_700_000_000_000L,
            ),
        )
        val flow: Flow<List<TransactionDomain.LocalSent>> = flowOf(expected)
        every { transactionRepository.observeLocalTransactions(userId) } returns flow

        val result = useCase(userId).first()

        assertEquals(expected, result)
    }
}

