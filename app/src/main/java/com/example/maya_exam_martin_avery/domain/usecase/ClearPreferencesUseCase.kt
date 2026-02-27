package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearPreferencesUseCase @Inject constructor(
    private val currentUserPreferences: CurrentUserPreferences,
) {
    fun invoke() {
        // Logout behavior: wipe everything stored in maya_preferences.
        currentUserPreferences.clearAll()
    }
}

