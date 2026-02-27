package com.example.maya_exam_martin_avery.presentation.start

import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class StartViewModelTest {
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mockk()

    @Test
    fun `destination is Login when current user id is null`() {
        every { getCurrentUserIdUseCase.invoke() } returns null

        val vm = StartViewModel(getCurrentUserIdUseCase)

        assertEquals(StartDestination.Login, vm.destination)
        verify(exactly = 1) { getCurrentUserIdUseCase.invoke() }
        confirmVerified(getCurrentUserIdUseCase)
    }

    @Test
    fun `destination is Login when current user id is zero or negative`() {
        every { getCurrentUserIdUseCase.invoke() } returns 0L
        val vmZero = StartViewModel(getCurrentUserIdUseCase)
        assertEquals(StartDestination.Login, vmZero.destination)

        every { getCurrentUserIdUseCase.invoke() } returns -1L
        val vmNegative = StartViewModel(getCurrentUserIdUseCase)
        assertEquals(StartDestination.Login, vmNegative.destination)

        verify(exactly = 2) { getCurrentUserIdUseCase.invoke() }
        confirmVerified(getCurrentUserIdUseCase)
    }

    @Test
    fun `destination is Wallet when current user id is positive`() {
        every { getCurrentUserIdUseCase.invoke() } returns 123L

        val vm = StartViewModel(getCurrentUserIdUseCase)

        assertEquals(StartDestination.Wallet, vm.destination)
        verify(exactly = 1) { getCurrentUserIdUseCase.invoke() }
        confirmVerified(getCurrentUserIdUseCase)
    }
}

