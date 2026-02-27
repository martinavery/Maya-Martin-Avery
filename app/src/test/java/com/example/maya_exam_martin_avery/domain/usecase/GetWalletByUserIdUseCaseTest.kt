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

class GetWalletByUserIdUseCaseTest {
    private val walletRepository: WalletRepository = mockk()
    private val useCase = GetWalletByUserIdUseCase(walletRepository)

    @Test
    fun `invoke delegates to repository and returns success`() = runTest {
        val userId = 10L
        val expected = WalletDomain(userId = userId, balance = 5.0)
        coEvery { walletRepository.getWalletByUserId(userId) } returns Result.success(expected)

        val result = useCase(userId)

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
        coVerify(exactly = 1) { walletRepository.getWalletByUserId(userId) }
        confirmVerified(walletRepository)
    }

    @Test
    fun `invoke delegates to repository and returns failure`() = runTest {
        val userId = 10L
        val expectedError = IllegalStateException("nope")
        coEvery { walletRepository.getWalletByUserId(userId) } returns Result.failure(expectedError)

        val result = useCase(userId)

        assertTrue(result.isFailure)
        assertSame(expectedError, result.exceptionOrNull())
        coVerify(exactly = 1) { walletRepository.getWalletByUserId(userId) }
        confirmVerified(walletRepository)
    }
}

