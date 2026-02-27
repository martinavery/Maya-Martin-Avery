package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveCurrentUserIdUseCase @Inject constructor(private val currentUserPreferences: CurrentUserPreferences) {
    fun invoke(userId: Long) {
        currentUserPreferences.saveCurrentUserId(userId)
    }
}
