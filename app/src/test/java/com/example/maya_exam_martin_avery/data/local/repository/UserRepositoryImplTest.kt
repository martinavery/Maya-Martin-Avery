package com.example.maya_exam_martin_avery.data.local.repository

import com.example.maya_exam_martin_avery.data.local.dao.UserDao
import com.example.maya_exam_martin_avery.data.local.dao.WalletDao
import com.example.maya_exam_martin_avery.data.local.entities.UserEntity
import com.example.maya_exam_martin_avery.data.local.mappers.UserMapper
import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferences
import com.example.maya_exam_martin_avery.domain.error.InvalidCredentialsException
import com.example.maya_exam_martin_avery.domain.error.LoginAppException
import com.example.maya_exam_martin_avery.domain.model.UserDomain
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

class UserRepositoryImplTest {
    private val userDao: UserDao = mockk()
    private val walletDao: WalletDao = mockk()
    private val userMapper: UserMapper = mockk()
    private val currentUserPreferences: CurrentUserPreferences = mockk()

    private val repository = UserRepositoryImpl(
        userDao = userDao,
        walletDao = walletDao,
        userMapper = userMapper,
        currentUserPreferences = currentUserPreferences,
    )

    @Test
    fun `fetchUser returns failure when dao returns null`() = runTest {
        // Treat "no matching row" as invalid credentials (expected login failure).
        val username = "maya toby"
        val password = "pw"

        coEvery { userDao.fetchUser(username, password) } returns null

        val result = repository.fetchUser(username, password)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidCredentialsException)

        coVerify(exactly = 1) { userDao.fetchUser(username, password) }
        verify(exactly = 0) { userMapper.toDomain(any()) }
        verify(exactly = 0) { currentUserPreferences.saveCurrentUserId(any()) }
        confirmVerified(userDao, userMapper, currentUserPreferences)
    }

    @Test
    fun `fetchUser returns app error when dao throws`() = runTest {
        // Unexpected data-layer issues should not look like invalid credentials.
        val username = "maya"
        val password = "pw"
        val cause = IllegalStateException("db exploded")

        coEvery { userDao.fetchUser(username, password) } throws cause

        val result = repository.fetchUser(username, password)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is LoginAppException)
        assertSame(cause, error?.cause)

        coVerify(exactly = 1) { userDao.fetchUser(username, password) }
        verify(exactly = 0) { userMapper.toDomain(any()) }
        verify(exactly = 0) { currentUserPreferences.saveCurrentUserId(any()) }
        confirmVerified(userDao, userMapper, currentUserPreferences)
    }

    @Test
    fun `fetchUser returns mapped domain when dao returns entity`() = runTest {
        // Repository should map local entities into a domain model.
        val username = "maya"
        val password = "pw"
        val entity = UserEntity(id = 123L, username = username, password = password)
        val expected = UserDomain(userName = username, userId = entity.id)

        coEvery { userDao.fetchUser(username, password) } returns entity
        every { currentUserPreferences.saveCurrentUserId(entity.id) } returns Unit
        // Mapper is synchronous (non-suspend).
        every { userMapper.toDomain(entity) } returns expected

        val result = repository.fetchUser(username, password)

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())

        coVerify(exactly = 1) { userDao.fetchUser(username, password) }
        verify(exactly = 1) { currentUserPreferences.saveCurrentUserId(entity.id) }
        verify(exactly = 1) { userMapper.toDomain(entity) }
        confirmVerified(userDao, userMapper, currentUserPreferences)
    }

    @Test
    fun `fetchUser returns app error when saving current user id fails`() = runTest {
        val username = "maya"
        val password = "pw"
        val entity = UserEntity(id = 123L, username = username, password = password)
        val cause = IllegalStateException("prefs failed")

        coEvery { userDao.fetchUser(username, password) } returns entity
        every { currentUserPreferences.saveCurrentUserId(entity.id) } throws cause

        val result = repository.fetchUser(username, password)

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is LoginAppException)
        assertSame(cause, error?.cause)

        coVerify(exactly = 1) { userDao.fetchUser(username, password) }
        verify(exactly = 1) { currentUserPreferences.saveCurrentUserId(entity.id) }
        verify(exactly = 0) { userMapper.toDomain(any()) }
        confirmVerified(userDao, userMapper, currentUserPreferences)
    }

    @Test
    fun `seedDefaultUserIfEmpty returns success when dao insert succeeds`() = runTest {
        // Seeding should succeed when the DAO insert does not throw.
        val username = "admin"
        val password = "admin"
        coEvery { userDao.insertDefaultIfEmpty(username = username, password = password) } returns Unit
        coEvery { userDao.fetchUser(username = username, password = password) } returns
            UserEntity(id = 123L, username = username, password = password)
        coEvery { walletDao.insertWalletIfMissing(userId = 123L, pesoBalance = 1000.0) } returns Unit

        val result = repository.seedDefaultUserIfEmpty(username = username, password = password)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { userDao.insertDefaultIfEmpty(username = username, password = password) }
        coVerify(exactly = 1) { userDao.fetchUser(username = username, password = password) }
        coVerify(exactly = 1) { walletDao.insertWalletIfMissing(userId = 123L, pesoBalance = 1000.0) }
        verify(exactly = 0) { currentUserPreferences.saveCurrentUserId(any()) }
        confirmVerified(userDao, walletDao, userMapper, currentUserPreferences)
    }

    @Test
    fun `seedDefaultUserIfEmpty returns failure when dao insert throws`() = runTest {
        // Seeding should surface underlying data-layer failures.
        val username = "admin"
        val password = "admin"
        val expectedError = IllegalStateException("boom")
        coEvery { userDao.insertDefaultIfEmpty(username = username, password = password) } throws expectedError

        val result = repository.seedDefaultUserIfEmpty(username = username, password = password)

        assertTrue(result.isFailure)
        assertSame(expectedError, result.exceptionOrNull())
        coVerify(exactly = 1) { userDao.insertDefaultIfEmpty(username = username, password = password) }
        coVerify(exactly = 0) { userDao.fetchUser(any(), any()) }
        coVerify(exactly = 0) { walletDao.insertWalletIfMissing(any(), any()) }
        verify(exactly = 0) { currentUserPreferences.saveCurrentUserId(any()) }
        confirmVerified(userDao, walletDao, userMapper, currentUserPreferences)
    }
}
