package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferences
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetCurrentUserIdUseCaseTest {
    private val currentUserPreferences: CurrentUserPreferences = mockk()
    private val getCurrentUserIdUseCase = GetCurrentUserIdUseCase(currentUserPreferences)

    @Test
    fun `invoke returns user id when present`() {
        val expected = 123L
        every { currentUserPreferences.getCurrentUserId() } returns expected

        val result = getCurrentUserIdUseCase.invoke()

        assertEquals(expected, result)
        verify(exactly = 1) { currentUserPreferences.getCurrentUserId() }
        confirmVerified(currentUserPreferences)
    }

    @Test
    fun `invoke returns null when missing`() {
        every { currentUserPreferences.getCurrentUserId() } returns null

        val result = getCurrentUserIdUseCase.invoke()

        assertNull(result)
        verify(exactly = 1) { currentUserPreferences.getCurrentUserId() }
        confirmVerified(currentUserPreferences)
    }
}

