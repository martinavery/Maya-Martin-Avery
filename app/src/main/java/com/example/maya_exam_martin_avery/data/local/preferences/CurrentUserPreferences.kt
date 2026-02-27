package com.example.maya_exam_martin_avery.data.local.preferences

interface CurrentUserPreferences {
    fun saveCurrentUserId(userId: Long)

    fun getCurrentUserId(): Long?

    fun clearCurrentUserId()
}
