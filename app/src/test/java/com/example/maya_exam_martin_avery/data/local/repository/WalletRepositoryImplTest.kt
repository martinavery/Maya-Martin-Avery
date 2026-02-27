package com.example.maya_exam_martin_avery.data.local.repository

import com.example.maya_exam_martin_avery.data.local.dao.WalletDao
import com.example.maya_exam_martin_avery.data.local.entities.WalletEntity
import com.example.maya_exam_martin_avery.data.local.mappers.WalletMapper
import com.example.maya_exam_martin_avery.domain.error.WalletAppException
import com.example.maya_exam_martin_avery.domain.error.WalletNotFoundException
import com.example.maya_exam_martin_avery.domain.model.WalletDomain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletRepositoryImplTest {
    private val walletDao: WalletDao = mockk()
    private val walletMapper: WalletMapper = mockk()

    private val repository = WalletRepositoryImpl(
        walletDao = walletDao,
        walletMapper = walletMapper,
    )

    @Test
    fun `getWalletByUserId returns mapped domain when dao returns entity`() = runTest {
        val userId = 7L
        val entity = WalletEntity(userId = userId, balance = 42.5)
        val expected = WalletDomain(userId = userId, balance = 42.5)

        coEvery { walletDao.getWalletByUserId(userId) } returns entity
        every { walletMapper.toDomain(entity) } returns expected

        val result = repository.getWalletByUserId(userId)

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
        coVerify(exactly = 1) { walletDao.getWalletByUserId(userId) }
        verify(exactly = 1) { walletMapper.toDomain(entity) }
        confirmVerified(walletDao, walletMapper)
    }

    @Test
    fun `getWalletByUserId returns not found failure when dao returns null`() = runTest {
        val userId = 99L
        coEvery { walletDao.getWalletByUserId(userId) } returns null

        val result = repository.getWalletByUserId(userId)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is WalletNotFoundException)
        assertEquals(userId, (error as WalletNotFoundException).userId)
        coVerify(exactly = 1) { walletDao.getWalletByUserId(userId) }
        verify(exactly = 0) { walletMapper.toDomain(any()) }
        confirmVerified(walletDao, walletMapper)
    }

    @Test
    fun `getWalletByUserId returns app error when dao throws`() = runTest {
        val userId = 1L
        val cause = IllegalStateException("db exploded")
        coEvery { walletDao.getWalletByUserId(userId) } throws cause

        val result = repository.getWalletByUserId(userId)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is WalletAppException)
        assertSame(cause, error?.cause)
        coVerify(exactly = 1) { walletDao.getWalletByUserId(userId) }
        verify(exactly = 0) { walletMapper.toDomain(any()) }
        confirmVerified(walletDao, walletMapper)
    }
}

