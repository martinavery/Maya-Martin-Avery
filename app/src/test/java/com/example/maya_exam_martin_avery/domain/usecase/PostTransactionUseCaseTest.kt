package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class PostTransactionUseCaseTest {
    private val transactionRepository: TransactionRepository = mockk()
    private val useCase = PostTransactionUseCase(transactionRepository)

    @Test
    fun `invoke delegates to repository`() = runTest {
        val userId = 1L
        val amount = 44.0
        val description = "Sent ₱44.00"
        val expected = Result.success(Unit)
        coEvery {
            transactionRepository.postRemoteSampleTransaction(userId = userId, amount = amount, description = description)
        } returns expected

        val result = useCase(userId = userId, amount = amount, description = description)

        assertTrue(result.isSuccess)
        assertSame(expected.getOrThrow(), result.getOrThrow())
        coVerify(exactly = 1) {
            transactionRepository.postRemoteSampleTransaction(userId = userId, amount = amount, description = description)
        }
        confirmVerified(transactionRepository)
    }
}

