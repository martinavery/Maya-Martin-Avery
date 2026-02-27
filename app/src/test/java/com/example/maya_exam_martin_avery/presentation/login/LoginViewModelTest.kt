package com.example.maya_exam_martin_avery.presentation.login

import com.example.maya_exam_martin_avery.domain.error.InvalidCredentialsException
import com.example.maya_exam_martin_avery.domain.error.LoginAppException
import com.example.maya_exam_martin_avery.domain.model.UserDomain
import com.example.maya_exam_martin_avery.domain.usecase.LoginUseCase
import com.example.maya_exam_martin_avery.domain.usecase.SaveCurrentUserIdUseCase
import com.example.maya_exam_martin_avery.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginUseCase: LoginUseCase = mockk()
    private val saveCurrentUserIdUseCase: SaveCurrentUserIdUseCase = mockk()

    @Test
    fun `setUsername and setPassword update derived isButtonDisabled`() = runTest {
        val vm = LoginViewModel(loginUseCase, saveCurrentUserIdUseCase)

        // Initial: disabled because inputs are empty.
        assertTrue(vm.uiState.value.isButtonDisabled)

        vm.setUsername("maya")
        assertTrue(vm.uiState.value.isButtonDisabled) // still disabled; password missing

        vm.setPassword("pw")
        assertTrue(!vm.uiState.value.isButtonDisabled) // enabled when both inputs present
    }

    @Test
    fun `login shows validation message when username or password is blank`() = runTest {
        val vm = LoginViewModel(loginUseCase, saveCurrentUserIdUseCase)

        vm.setUsername("   ")
        vm.setPassword("")
        vm.login()

        assertEquals("Username and password are required.", vm.uiState.value.screenErrorMessage)
        // Should not call into domain layer on validation failure.
        coVerify(exactly = 0) { loginUseCase.invoke(any(), any()) }
        verify(exactly = 0) { saveCurrentUserIdUseCase.invoke(any()) }
        confirmVerified(loginUseCase, saveCurrentUserIdUseCase)
    }

    @Test
    fun `login success saves user id and emits NavigateToNext`() = runTest {
        val vm = LoginViewModel(loginUseCase, saveCurrentUserIdUseCase)
        vm.setUsername("maya")
        vm.setPassword("pw")

        val user = UserDomain(userName = "maya", userId = 123L)
        coEvery { loginUseCase.invoke(userName = "maya", password = "pw") } returns Result.success(user)
        every { saveCurrentUserIdUseCase.invoke(user.userId) } returns Unit

        // Start collecting synchronously to avoid missing the one-shot effect emission.
        val effect = async(start = CoroutineStart.UNDISPATCHED) { vm.effects.first() }

        vm.login()

        assertEquals(LoginEffect.NavigateToNext, effect.await())
        assertTrue(!vm.uiState.value.isLoading)
        assertEquals("", vm.uiState.value.screenErrorMessage)

        coVerify(exactly = 1) { loginUseCase.invoke(userName = "maya", password = "pw") }
        verify(exactly = 1) { saveCurrentUserIdUseCase.invoke(user.userId) }
        confirmVerified(loginUseCase, saveCurrentUserIdUseCase)
    }

    @Test
    fun `login maps InvalidCredentialsException to user friendly message`() = runTest {
        val vm = LoginViewModel(loginUseCase, saveCurrentUserIdUseCase)
        vm.setUsername("maya")
        vm.setPassword("bad")

        coEvery { loginUseCase.invoke(userName = "maya", password = "bad") } returns Result.failure(InvalidCredentialsException())

        vm.login()

        assertEquals("Invalid username or password.", vm.uiState.value.screenErrorMessage)
        assertTrue(!vm.uiState.value.isLoading)
        coVerify(exactly = 1) { loginUseCase.invoke(userName = "maya", password = "bad") }
        verify(exactly = 0) { saveCurrentUserIdUseCase.invoke(any()) }
        confirmVerified(loginUseCase, saveCurrentUserIdUseCase)
    }

    @Test
    fun `login maps LoginAppException to generic retry message`() = runTest {
        val vm = LoginViewModel(loginUseCase, saveCurrentUserIdUseCase)
        vm.setUsername("maya")
        vm.setPassword("pw")

        coEvery { loginUseCase.invoke(userName = "maya", password = "pw") } returns Result.failure(LoginAppException(IllegalStateException("boom")))

        vm.login()

        assertEquals("Login failed. Please try again.", vm.uiState.value.screenErrorMessage)
        coVerify(exactly = 1) { loginUseCase.invoke(userName = "maya", password = "pw") }
        verify(exactly = 0) { saveCurrentUserIdUseCase.invoke(any()) }
        confirmVerified(loginUseCase, saveCurrentUserIdUseCase)
    }

    @Test
    fun `login uses default message when throwable has no message`() = runTest {
        val vm = LoginViewModel(loginUseCase, saveCurrentUserIdUseCase)
        vm.setUsername("maya")
        vm.setPassword("pw")

        coEvery { loginUseCase.invoke(userName = "maya", password = "pw") } returns Result.failure(Exception())

        vm.login()

        assertEquals("Login failed.", vm.uiState.value.screenErrorMessage)
        coVerify(exactly = 1) { loginUseCase.invoke(userName = "maya", password = "pw") }
        verify(exactly = 0) { saveCurrentUserIdUseCase.invoke(any()) }
        confirmVerified(loginUseCase, saveCurrentUserIdUseCase)
    }

    @Test
    fun `login ignores re-entry while loading`() = runTest {
        val vm = LoginViewModel(loginUseCase, saveCurrentUserIdUseCase)
        vm.setUsername("maya")
        vm.setPassword("pw")

        val user = UserDomain(userName = "maya", userId = 123L)
        coEvery { loginUseCase.invoke(userName = "maya", password = "pw") } coAnswers {
            // Keep the coroutine suspended so the ViewModel remains in loading state.
            delay(1_000)
            Result.success(user)
        }
        every { saveCurrentUserIdUseCase.invoke(any()) } returns Unit

        vm.login()
        // While the first login coroutine is suspended, the ViewModel should reject new login attempts.
        vm.login()

        advanceTimeBy(1_000)

        coVerify(exactly = 1) { loginUseCase.invoke(userName = "maya", password = "pw") }
    }
}

