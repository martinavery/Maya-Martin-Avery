package com.example.maya_exam_martin_avery.presentation.transactions

import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetRemoteTransactionsUseCase
import com.example.maya_exam_martin_avery.domain.usecase.ObserveLocalTransactionsUseCase
import com.example.maya_exam_martin_avery.testing.MainDispatcherRule

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TransactionsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mockk()
    private val observeLocalTransactionsUseCase: ObserveLocalTransactionsUseCase = mockk()
    private val getRemoteTransactionsUseCase: GetRemoteTransactionsUseCase = mockk()

    @Test
    fun `when userId is null it does not subscribe to local transactions`() = runTest {
        every { getCurrentUserIdUseCase.invoke() } returns null
        // Remote is still refreshed on init; keep it simple.
        coEvery { getRemoteTransactionsUseCase.invoke() } returns Result.success(emptyList())

        val vm = TransactionsViewModel(getCurrentUserIdUseCase, observeLocalTransactionsUseCase, getRemoteTransactionsUseCase)
        advanceUntilIdle()

        // Local subscription should be skipped entirely.
        verify(exactly = 0) { observeLocalTransactionsUseCase.invoke(any()) }
        assertTrue(vm.uiState.value.localTransactions.isEmpty())
    }

    @Test
    fun `subscribes to local transactions and updates uiState`() = runTest {
        val userId = 123L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        coEvery { getRemoteTransactionsUseCase.invoke() } returns Result.success(emptyList())

        val localFlow = MutableStateFlow<List<TransactionDomain.LocalSent>>(emptyList())
        every { observeLocalTransactionsUseCase.invoke(userId) } returns localFlow

        val vm = TransactionsViewModel(getCurrentUserIdUseCase, observeLocalTransactionsUseCase, getRemoteTransactionsUseCase)
        advanceUntilIdle()

        val item =
            TransactionDomain.LocalSent(
                id = "local-1",
                userId = userId,
                amount = 1.23,
                description = "Sent ₱1.23",
                createdAtEpochMs = 1_700_000_000_000L,
            )
        localFlow.value = listOf(item)
        advanceUntilIdle()

        assertEquals(listOf(item), vm.uiState.value.localTransactions)
        verify(exactly = 1) { observeLocalTransactionsUseCase.invoke(userId) }
    }

    @Test
    fun `refreshRemote toggles loading and updates remote list on success`() = runTest {
        val userId = 123L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        every { observeLocalTransactionsUseCase.invoke(userId) } returns flowOf(emptyList())

        val expected =
            listOf(TransactionDomain.RemoteSample(id = "remote-1", userId = 1, title = "t", body = "b"))

        // Delay to allow asserting the intermediate loading state.
        coEvery { getRemoteTransactionsUseCase.invoke() } coAnswers {
            delay(1_000)
            Result.success(expected)
        }

        val vm = TransactionsViewModel(getCurrentUserIdUseCase, observeLocalTransactionsUseCase, getRemoteTransactionsUseCase)

        // init() already called refreshRemote(); at this point it should be loading.
        assertTrue(vm.uiState.value.isRemoteLoading)

        advanceTimeBy(1_000)
        advanceUntilIdle()

        assertTrue(!vm.uiState.value.isRemoteLoading)
        assertEquals(expected, vm.uiState.value.remoteTransactions)
    }

    @Test
    fun `refreshRemote sets error message on failure`() = runTest {
        val userId = 123L
        every { getCurrentUserIdUseCase.invoke() } returns userId
        every { observeLocalTransactionsUseCase.invoke(userId) } returns flowOf(emptyList())

        coEvery { getRemoteTransactionsUseCase.invoke() } returns Result.failure(IllegalStateException("boom"))

        val vm = TransactionsViewModel(getCurrentUserIdUseCase, observeLocalTransactionsUseCase, getRemoteTransactionsUseCase)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.screenErrorMessage.isNotBlank())
    }
}

