package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferences
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ClearPreferencesUseCaseTest {
    private val currentUserPreferences: CurrentUserPreferences = mockk()
    private val useCase = ClearPreferencesUseCase(currentUserPreferences)

    @Test
    fun `invoke clears all preferences`() {
        // Logout behavior: wipe all stored preferences.
        every { currentUserPreferences.clearAll() } returns Unit

        useCase.invoke()

        verify(exactly = 1) { currentUserPreferences.clearAll() }
        confirmVerified(currentUserPreferences)
    }
}

