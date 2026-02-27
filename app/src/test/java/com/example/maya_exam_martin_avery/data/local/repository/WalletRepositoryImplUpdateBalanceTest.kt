package com.example.maya_exam_martin_avery.data.local.repository

import com.example.maya_exam_martin_avery.data.local.dao.WalletDao
import com.example.maya_exam_martin_avery.data.local.mappers.WalletMapper
import com.example.maya_exam_martin_avery.domain.error.WalletAppException
import com.example.maya_exam_martin_avery.domain.error.WalletNotFoundException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletRepositoryImplUpdateBalanceTest {
    private val walletDao: WalletDao = mockk()
    private val walletMapper: WalletMapper = mockk(relaxed = true)

    private val repository = WalletRepositoryImpl(walletDao = walletDao, walletMapper = walletMapper)

    @Test
    fun `updateWalletBalance returns success when dao updates a row`() = runTest {
        val userId = 1L
        val newBalance = 9.99
        coEvery { walletDao.updateWalletBalance(userId = userId, newBalance = newBalance) } returns 1

        val result = repository.updateWalletBalance(userId = userId, newBalance = newBalance)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { walletDao.updateWalletBalance(userId = userId, newBalance = newBalance) }
        // updateWalletBalance should not use the mapper.
        verify(exactly = 0) { walletMapper.toDomain(any()) }
        confirmVerified(walletDao, walletMapper)
    }

    @Test
    fun `updateWalletBalance returns not found when dao updates zero rows`() = runTest {
        val userId = 1L
        val newBalance = 9.99
        coEvery { walletDao.updateWalletBalance(userId = userId, newBalance = newBalance) } returns 0

        val result = repository.updateWalletBalance(userId = userId, newBalance = newBalance)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is WalletNotFoundException)
        coVerify(exactly = 1) { walletDao.updateWalletBalance(userId = userId, newBalance = newBalance) }
        verify(exactly = 0) { walletMapper.toDomain(any()) }
        confirmVerified(walletDao, walletMapper)
    }

    @Test
    fun `updateWalletBalance wraps dao exceptions in WalletAppException`() = runTest {
        val userId = 1L
        val newBalance = 9.99
        val cause = IllegalStateException("db exploded")
        coEvery { walletDao.updateWalletBalance(userId = userId, newBalance = newBalance) } throws cause

        val result = repository.updateWalletBalance(userId = userId, newBalance = newBalance)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is WalletAppException)
        assertSame(cause, error?.cause)
        coVerify(exactly = 1) { walletDao.updateWalletBalance(userId = userId, newBalance = newBalance) }
        verify(exactly = 0) { walletMapper.toDomain(any()) }
        confirmVerified(walletDao, walletMapper)
    }
}

