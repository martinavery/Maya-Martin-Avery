package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRemoteTransactionsUseCaseTest {
    private val transactionRepository: TransactionRepository = mockk()
    private val useCase = GetRemoteTransactionsUseCase(transactionRepository)

    @Test
    fun `invoke returns repository result`() = runTest {
        val expectedList = listOf(
            TransactionDomain.RemoteSample(id = "remote-1", userId = 1, title = "t", body = "b"),
        )
        coEvery { transactionRepository.getRemoteSampleTransactions() } returns Result.success(expectedList)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(expectedList, result.getOrThrow())
        coVerify(exactly = 1) { transactionRepository.getRemoteSampleTransactions() }
        confirmVerified(transactionRepository)
    }

    @Test
    fun `invoke propagates repository failure`() = runTest {
        val expectedError = IllegalStateException("network down")
        coEvery { transactionRepository.getRemoteSampleTransactions() } returns Result.failure(expectedError)

        val result = useCase()

        assertTrue(result.isFailure)
        assertSame(expectedError, result.exceptionOrNull())
        coVerify(exactly = 1) { transactionRepository.getRemoteSampleTransactions() }
        confirmVerified(transactionRepository)
    }
}

