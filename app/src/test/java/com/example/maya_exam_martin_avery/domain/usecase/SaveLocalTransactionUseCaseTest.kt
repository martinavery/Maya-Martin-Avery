package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveLocalTransactionUseCaseTest {
    private val transactionRepository: TransactionRepository = mockk()
    private val useCase = SaveLocalTransactionUseCase(transactionRepository)

    @Test
    fun `invoke delegates to repository`() = runTest {
        val userId = 1L
        val amount = 12.34
        val description = "Sent ₱12.34"
        val now = 1_700_000_000_000L
        coEvery {
            transactionRepository.saveLocalSentTransaction(
                userId = userId,
                amount = amount,
                description = description,
                createdAtEpochMs = now,
            )
        } returns Result.success(Unit)

        val result = useCase(userId = userId, amount = amount, description = description, createdAtEpochMs = now)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            transactionRepository.saveLocalSentTransaction(
                userId = userId,
                amount = amount,
                description = description,
                createdAtEpochMs = now,
            )
        }
        confirmVerified(transactionRepository)
    }
}

