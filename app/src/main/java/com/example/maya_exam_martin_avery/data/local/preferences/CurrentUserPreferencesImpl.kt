package com.example.maya_exam_martin_avery.data.local.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class CurrentUserPreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : CurrentUserPreferences {
    override fun saveCurrentUserId(userId: Long) {
        sharedPreferences.edit { putLong(KEY_CURRENT_USER_ID, userId) }
    }

    override fun getCurrentUserId(): Long? {
        if (!sharedPreferences.contains(KEY_CURRENT_USER_ID)) {
            return null
        }
        return sharedPreferences.getLong(KEY_CURRENT_USER_ID, DEFAULT_MISSING_USER_ID)
    }

    override fun clearCurrentUserId() {
        sharedPreferences.edit { remove(KEY_CURRENT_USER_ID) }
    }

    private companion object {
        const val KEY_CURRENT_USER_ID = "current_user_id"
        const val DEFAULT_MISSING_USER_ID = -1L
    }
}
