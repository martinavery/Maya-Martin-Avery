package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferences
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class SaveCurrentUserIdUseCaseTest {
    private val currentUserPreferences: CurrentUserPreferences = mockk()
    private val useCase = SaveCurrentUserIdUseCase(currentUserPreferences)

    @Test
    fun `invoke saves current user id`() {
        val userId = 123L
        every { currentUserPreferences.saveCurrentUserId(userId) } returns Unit

        useCase.invoke(userId)

        verify(exactly = 1) { currentUserPreferences.saveCurrentUserId(userId) }
        confirmVerified(currentUserPreferences)
    }
}

