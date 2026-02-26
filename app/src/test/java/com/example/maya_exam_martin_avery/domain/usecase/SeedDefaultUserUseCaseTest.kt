package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedDefaultUserUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val seedDefaultUserUseCase = SeedDefaultUserUseCase(userRepository)

    @Test
    fun `invoke seeds admin admin`() = runTest {
        // Keep default credentials centralized and consistent for first run.
        coEvery {
            userRepository.seedDefaultUserIfEmpty(username = "admin", password = "admin")
        } returns Result.success(Unit)

        val result = seedDefaultUserUseCase()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            userRepository.seedDefaultUserIfEmpty(username = "admin", password = "admin")
        }
        confirmVerified(userRepository)
    }

    @Test
    fun `invoke propagates repository failure`() = runTest {
        // Use case should not swallow repository errors.
        val expectedError = RuntimeException("db error")
        coEvery {
            userRepository.seedDefaultUserIfEmpty(username = "admin", password = "admin")
        } returns Result.failure(expectedError)

        val result = seedDefaultUserUseCase()

        assertTrue(result.isFailure)
        assertSame(expectedError, result.exceptionOrNull())
        coVerify(exactly = 1) {
            userRepository.seedDefaultUserIfEmpty(username = "admin", password = "admin")
        }
        confirmVerified(userRepository)
    }
}

