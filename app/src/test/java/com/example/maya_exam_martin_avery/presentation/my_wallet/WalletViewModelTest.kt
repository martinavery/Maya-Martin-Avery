package com.example.maya_exam_martin_avery.presentation.my_wallet

import com.example.maya_exam_martin_avery.domain.model.WalletDomain
import com.example.maya_exam_martin_avery.domain.usecase.ClearPreferencesUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetWalletByUserIdUseCase
import com.example.maya_exam_martin_avery.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class WalletViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getWalletByUserIdUseCase: GetWalletByUserIdUseCase = mockk()
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mockk()
    private val clearPreferencesUseCase: ClearPreferencesUseCase = mockk()

    @Test
    fun `init sets error when current user is missing`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null

        val vm = WalletViewModel(getWalletByUserIdUseCase, getCurrentUserIdUseCase, clearPreferencesUseCase)

        assertEquals("No current user found. Please log in.", vm.uiState.value.errorMessage)
        verify(exactly = 1) { getCurrentUserIdUseCase.invoke() }
        coVerify(exactly = 0) { getWalletByUserIdUseCase.invoke(any()) }
        verify(exactly = 0) { clearPreferencesUseCase.invoke() }
        confirmVerified(getWalletByUserIdUseCase, getCurrentUserIdUseCase, clearPreferencesUseCase)
    }

    @Test
    fun `init loads wallet balance on success`() = runTest {
        val userId = 1L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.success(
            WalletDomain(userId = userId, balance = 1000.0),
        )

        val vm = WalletViewModel(getWalletByUserIdUseCase, getCurrentUserIdUseCase, clearPreferencesUseCase)
        advanceUntilIdle()

        assertEquals(1000.0, vm.uiState.value.balance, 0.0)
        assertEquals("", vm.uiState.value.errorMessage)
        coVerify(exactly = 1) { getWalletByUserIdUseCase.invoke(userId) }
    }

    @Test
    fun `init sets error message when wallet load fails`() = runTest {
        val userId = 1L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.failure(IllegalStateException("boom"))

        val vm = WalletViewModel(getWalletByUserIdUseCase, getCurrentUserIdUseCase, clearPreferencesUseCase)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.errorMessage.isNotBlank())
        coVerify(exactly = 1) { getWalletByUserIdUseCase.invoke(userId) }
    }

    @Test
    fun `onToggleBalanceVisibility toggles state`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null
        val vm = WalletViewModel(getWalletByUserIdUseCase, getCurrentUserIdUseCase, clearPreferencesUseCase)

        assertTrue(vm.uiState.value.isBalanceVisible)
        vm.onToggleBalanceVisibility()
        assertTrue(!vm.uiState.value.isBalanceVisible)
        vm.onToggleBalanceVisibility()
        assertTrue(vm.uiState.value.isBalanceVisible)
    }

    @Test
    fun `onLogout clears preferences and emits NavigateToLogin`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null
        every { clearPreferencesUseCase.invoke() } returns Unit

        val vm = WalletViewModel(getWalletByUserIdUseCase, getCurrentUserIdUseCase, clearPreferencesUseCase)

        // Start collecting synchronously to avoid missing the one-shot effect emission.
        val effect = async(start = CoroutineStart.UNDISPATCHED) { vm.effects.first() }
        vm.onLogout()

        assertEquals(WalletEffect.NavigateToLogin, effect.await())
        verify(exactly = 1) { getCurrentUserIdUseCase.invoke() }
        verify(exactly = 1) { clearPreferencesUseCase.invoke() }
        confirmVerified(getWalletByUserIdUseCase, getCurrentUserIdUseCase, clearPreferencesUseCase)
    }
}

