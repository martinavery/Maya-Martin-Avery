package com.example.maya_exam_martin_avery.presentation.send_money

import com.example.maya_exam_martin_avery.domain.model.WalletDomain
import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetWalletByUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.PostTransactionUseCase
import com.example.maya_exam_martin_avery.domain.usecase.SaveLocalTransactionUseCase
import com.example.maya_exam_martin_avery.domain.usecase.SendMoneyUseCase
import com.example.maya_exam_martin_avery.testing.MainDispatcherRule

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SendMoneyViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mockk()
    private val getWalletByUserIdUseCase: GetWalletByUserIdUseCase = mockk()
    private val sendMoneyUseCase: SendMoneyUseCase = mockk()
    private val saveLocalTransactionUseCase: SaveLocalTransactionUseCase = mockk()
    private val postTransactionUseCase: PostTransactionUseCase = mockk()

    @Test
    fun `init sets error when current user is missing`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null

        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )

        assertEquals("No current user found. Please log in.", vm.uiState.value.screenErrorMessage)
        coVerify(exactly = 0) { getWalletByUserIdUseCase.invoke(any()) }
    }

    @Test
    fun `init loads balance when wallet load succeeds`() = runTest {
        val userId = 1L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.success(WalletDomain(userId = userId, balance = 100.0))

        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )
        advanceUntilIdle()

        assertEquals(100.0, vm.uiState.value.balance, 0.0)
        assertEquals("", vm.uiState.value.screenErrorMessage)
    }

    @Test
    fun `onAmountChanged sanitizes input and clears inline error`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null
        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )

        vm.onAmountChanged(" 1a2.3456 ")

        assertEquals("12.34", vm.uiState.value.amountInput)
        assertEquals("", vm.uiState.value.screenErrorMessage)
    }

    @Test
    fun `onSubmit shows failure sheet when amount is not a number`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null
        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )

        vm.onAmountChanged("abc")
        vm.onSubmit()

        assertEquals(SendMoneySheetState.Failure("Please enter a valid amount."), vm.uiState.value.sheet)
    }

    @Test
    fun `onSubmit shows failure sheet when amount is zero or negative`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null
        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )

        vm.onAmountChanged("0")
        vm.onSubmit()
        assertEquals(SendMoneySheetState.Failure("Amount must be greater than 0."), vm.uiState.value.sheet)

        vm.onDismissSheet()
        assertNull(vm.uiState.value.sheet)

        vm.onAmountChanged("-1")
        vm.onSubmit()
        assertEquals(SendMoneySheetState.Failure("Amount must be greater than 0."), vm.uiState.value.sheet)
    }

    @Test
    fun `onSubmit shows failure sheet when amount exceeds balance`() = runTest {
        val userId = 1L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.success(WalletDomain(userId = userId, balance = 10.0))

        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )
        advanceUntilIdle()

        vm.onAmountChanged("11")
        vm.onSubmit()

        assertEquals(
            SendMoneySheetState.Failure("Amount must be less than or equal to your available balance."),
            vm.uiState.value.sheet,
        )
        coVerify(exactly = 0) { sendMoneyUseCase.invoke(any(), any()) }
    }

    @Test
    fun `onSubmit shows failure sheet when current user is missing`() = runTest {
        val userId = 1L
        // First call for init->loadBalance, second call for submit->user check.
        every { getCurrentUserIdUseCase.invoke() } returnsMany listOf(userId, null)
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.success(WalletDomain(userId = userId, balance = 10.0))

        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )
        advanceUntilIdle()

        vm.onAmountChanged("1")
        vm.onSubmit()

        assertEquals(SendMoneySheetState.Failure("No current user found. Please log in."), vm.uiState.value.sheet)
        coVerify(exactly = 0) { sendMoneyUseCase.invoke(any(), any()) }
    }

    @Test
    fun `onSubmit success sets success sheet and calls local save and best-effort post`() = runTest {
        val userId = 1L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.success(WalletDomain(userId = userId, balance = 100.0))

        coEvery { sendMoneyUseCase.invoke(userId = userId, amount = 12.34) } returns Result.success(87.66)
        coEvery { saveLocalTransactionUseCase.invoke(userId = userId, amount = 12.34, description = "Sent ₱12.34", createdAtEpochMs = any()) } returns Result.success(Unit)
        coEvery { postTransactionUseCase.invoke(userId = userId, amount = 12.34, description = "Sent ₱12.34") } returns Result.success(Unit)

        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )
        advanceUntilIdle()

        vm.onAmountChanged("12.34")
        vm.onSubmit()
        advanceUntilIdle()

        assertEquals(SendMoneySheetState.Success(sentAmount = 12.34), vm.uiState.value.sheet)
        assertTrue(!vm.uiState.value.isLoading)

        coVerify(exactly = 1) { sendMoneyUseCase.invoke(userId = userId, amount = 12.34) }
        coVerify(exactly = 1) {
            saveLocalTransactionUseCase.invoke(
                userId = userId,
                amount = 12.34,
                description = "Sent ₱12.34",
                createdAtEpochMs = any(),
            )
        }
        coVerify(exactly = 1) { postTransactionUseCase.invoke(userId = userId, amount = 12.34, description = "Sent ₱12.34") }
    }

    @Test
    fun `onSubmit failure sets failure sheet with throwable message`() = runTest {
        val userId = 1L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.success(WalletDomain(userId = userId, balance = 100.0))

        coEvery { sendMoneyUseCase.invoke(userId = userId, amount = 1.0) } returns Result.failure(IllegalArgumentException("Insufficient balance."))

        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )
        advanceUntilIdle()

        vm.onAmountChanged("1")
        vm.onSubmit()
        advanceUntilIdle()

        assertEquals(SendMoneySheetState.Failure("Insufficient balance."), vm.uiState.value.sheet)
    }

    @Test
    fun `onDoneFromSheet emits navigation effect only for success`() = runTest {
        val userId = 1L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.success(WalletDomain(userId = userId, balance = 100.0))

        coEvery { sendMoneyUseCase.invoke(userId = userId, amount = 1.0) } returns Result.success(99.0)
        coEvery { saveLocalTransactionUseCase.invoke(any(), any(), any(), any()) } returns Result.success(Unit)
        coEvery { postTransactionUseCase.invoke(any(), any(), any()) } returns Result.success(Unit)

        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )
        advanceUntilIdle()

        vm.onAmountChanged("1")
        vm.onSubmit()
        advanceUntilIdle()

        // Start collecting synchronously to avoid missing the one-shot effect emission.
        val effect = async(start = CoroutineStart.UNDISPATCHED) { vm.effects.first() }
        vm.onDoneFromSheet()

        assertEquals(SendMoneyEffect.NavigateBackToWallet, effect.await())

        // Failure sheet should not emit a navigation effect.
        vm.onAmountChanged("abc")
        vm.onSubmit()
        val noEffect = async { vm.effects.first() }
        vm.onDoneFromSheet()
        advanceUntilIdle()

        // If a new effect was emitted, this would have completed.
        assertTrue(!noEffect.isCompleted)
        noEffect.cancel()
    }

    @Test
    fun `onSubmit ignores re-entry while loading`() = runTest {
        val userId = 1L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getWalletByUserIdUseCase.invoke(userId) } returns Result.success(WalletDomain(userId = userId, balance = 100.0))

        coEvery { sendMoneyUseCase.invoke(userId = userId, amount = 1.0) } coAnswers {
            // Keep suspended so isLoading remains true.
            delay(1_000)
            Result.success(99.0)
        }
        coEvery { saveLocalTransactionUseCase.invoke(any(), any(), any(), any()) } returns Result.success(Unit)
        coEvery { postTransactionUseCase.invoke(any(), any(), any()) } returns Result.success(Unit)

        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )
        advanceUntilIdle()

        vm.onAmountChanged("1")
        vm.onSubmit()
        vm.onSubmit()

        advanceTimeBy(1_000)

        coVerify(exactly = 1) { sendMoneyUseCase.invoke(userId = userId, amount = 1.0) }
    }

    @Test
    fun `onDismissSheet clears sheet state`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null
        val vm =
            SendMoneyViewModel(
                getCurrentUserIdUseCase,
                getWalletByUserIdUseCase,
                sendMoneyUseCase,
                saveLocalTransactionUseCase,
                postTransactionUseCase,
            )

        vm.onAmountChanged("abc")
        vm.onSubmit()
        assertTrue(vm.uiState.value.sheet is SendMoneySheetState.Failure)

        vm.onDismissSheet()
        assertNull(vm.uiState.value.sheet)
    }
}

