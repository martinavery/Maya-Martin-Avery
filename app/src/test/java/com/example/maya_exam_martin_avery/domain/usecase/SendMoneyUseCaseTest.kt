package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.model.WalletDomain
import com.example.maya_exam_martin_avery.domain.repository.WalletRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SendMoneyUseCaseTest {
    private val walletRepository: WalletRepository = mockk()
    private val useCase = SendMoneyUseCase(walletRepository)

    @Test
    fun `invoke returns failure when amount is zero or negative`() = runTest {
        val resultZero = useCase(userId = 1L, amount = 0.0)
        val resultNegative = useCase(userId = 1L, amount = -1.0)

        assertTrue(resultZero.isFailure)
        assertTrue(resultZero.exceptionOrNull() is IllegalArgumentException)

        assertTrue(resultNegative.isFailure)
        assertTrue(resultNegative.exceptionOrNull() is IllegalArgumentException)

        // Validation should short-circuit without touching the repository.
        coVerify(exactly = 0) { walletRepository.getWalletByUserId(any()) }
        coVerify(exactly = 0) { walletRepository.updateWalletBalance(any(), any()) }
        confirmVerified(walletRepository)
    }

    @Test
    fun `invoke propagates wallet fetch failure`() = runTest {
        val userId = 7L
        val amount = 1.0
        val expectedError = IllegalStateException("db down")

        coEvery { walletRepository.getWalletByUserId(userId) } returns Result.failure(expectedError)

        val result = useCase(userId = userId, amount = amount)

        assertTrue(result.isFailure)
        assertSame(expectedError, result.exceptionOrNull())
        coVerify(exactly = 1) { walletRepository.getWalletByUserId(userId) }
        coVerify(exactly = 0) { walletRepository.updateWalletBalance(any(), any()) }
        confirmVerified(walletRepository)
    }

    @Test
    fun `invoke returns failure when amount exceeds balance`() = runTest {
        val userId = 7L
        val amount = 10.01
        coEvery { walletRepository.getWalletByUserId(userId) } returns Result.success(
            WalletDomain(userId = userId, balance = 10.0),
        )

        val result = useCase(userId = userId, amount = amount)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 1) { walletRepository.getWalletByUserId(userId) }
        coVerify(exactly = 0) { walletRepository.updateWalletBalance(any(), any()) }
        confirmVerified(walletRepository)
    }

    @Test
    fun `invoke propagates update balance failure`() = runTest {
        val userId = 7L
        val amount = 1.0
        val expectedError = IllegalStateException("update failed")

        coEvery { walletRepository.getWalletByUserId(userId) } returns Result.success(
            WalletDomain(userId = userId, balance = 10.0),
        )
        coEvery { walletRepository.updateWalletBalance(userId = userId, newBalance = 9.0) } returns Result.failure(expectedError)

        val result = useCase(userId = userId, amount = amount)

        assertTrue(result.isFailure)
        assertSame(expectedError, result.exceptionOrNull())
        coVerify(exactly = 1) { walletRepository.getWalletByUserId(userId) }
        coVerify(exactly = 1) { walletRepository.updateWalletBalance(userId = userId, newBalance = 9.0) }
        confirmVerified(walletRepository)
    }

    @Test
    fun `invoke rounds balance to cents to avoid floating artifacts`() = runTest {
        val userId = 7L
        val amount = 0.2

        // 0.3 - 0.2 is a common floating point artifact; ensure we persist 0.10 exactly.
        coEvery { walletRepository.getWalletByUserId(userId) } returns Result.success(
            WalletDomain(userId = userId, balance = 0.3),
        )
        coEvery { walletRepository.updateWalletBalance(userId = userId, newBalance = 0.1) } returns Result.success(Unit)

        val result = useCase(userId = userId, amount = amount)

        assertTrue(result.isSuccess)
        assertEquals(0.1, result.getOrThrow(), 0.0)
        coVerify(exactly = 1) { walletRepository.getWalletByUserId(userId) }
        coVerify(exactly = 1) { walletRepository.updateWalletBalance(userId = userId, newBalance = 0.1) }
        confirmVerified(walletRepository)
    }
}

