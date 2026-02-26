package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.model.UserDomain
import com.example.maya_exam_martin_avery.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val loginUseCase = LoginUseCase(userRepository)

    @Test
    fun `invoke delegates to repository and returns success`() = runTest {
        // Use case should be a simple pass-through to the repository.
        val expected = UserDomain(userName = "maya")
        val username = "maya"
        val password = "pw"

        coEvery { userRepository.fetchUser(username, password) } returns Result.success(expected)

        val result = loginUseCase.invoke(username, password)

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
        coVerify(exactly = 1) { userRepository.fetchUser(username, password) }
        confirmVerified(userRepository)
    }

    @Test
    fun `invoke delegates to repository and returns failure`() = runTest {
        // Use case should not swallow or change repository failures.
        val username = "maya"
        val password = "bad_pw"
        val expectedError = IllegalStateException("nope")

        coEvery { userRepository.fetchUser(username, password) } returns Result.failure(expectedError)

        val result = loginUseCase.invoke(username, password)

        assertTrue(result.isFailure)
        assertSame(expectedError, result.exceptionOrNull())
        coVerify(exactly = 1) { userRepository.fetchUser(username, password) }
        confirmVerified(userRepository)
    }
}

