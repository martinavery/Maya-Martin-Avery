package com.example.maya_exam_martin_avery.domain.repository

import com.example.maya_exam_martin_avery.domain.model.UserDomain

interface UserRepository {
    // Failure means "no matching user" (or another data-layer error).
    suspend fun fetchUser(username: String, password: String): Result<UserDomain>

    // Seeds a single default user only when the users table is empty.
    suspend fun seedDefaultUserIfEmpty(username: String, password: String): Result<Unit>
}