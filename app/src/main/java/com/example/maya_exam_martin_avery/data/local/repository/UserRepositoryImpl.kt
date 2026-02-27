package com.example.maya_exam_martin_avery.data.local.repository

import com.example.maya_exam_martin_avery.data.local.dao.UserDao
import com.example.maya_exam_martin_avery.data.local.dao.WalletDao
import com.example.maya_exam_martin_avery.data.local.mappers.UserMapper
import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferences
import com.example.maya_exam_martin_avery.domain.error.InvalidCredentialsException
import com.example.maya_exam_martin_avery.domain.error.LoginAppException
import com.example.maya_exam_martin_avery.domain.model.UserDomain
import com.example.maya_exam_martin_avery.domain.repository.UserRepository
import javax.inject.Inject



class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val walletDao: WalletDao,
    private val userMapper: UserMapper,
    private val currentUserPreferences: CurrentUserPreferences,
) : UserRepository {
    override suspend fun fetchUser(username: String, password: String): Result<UserDomain> {
        // Distinguish "invalid credentials" (expected) from "app/data error" (unexpected).
        return try {
            val userEntity = userDao.fetchUser(username, password)
            if (userEntity == null) {
                Result.failure(InvalidCredentialsException())
            } else {
                currentUserPreferences.saveCurrentUserId(userEntity.id)
                Result.success(userMapper.toDomain(userEntity))
            }
        } catch (t: Throwable) {
            Result.failure(LoginAppException(t))
        }
    }

    override suspend fun seedDefaultUserIfEmpty(username: String, password: String): Result<Unit> {
        return runCatching {
            // Delegates to an atomic DAO insert that becomes a no-op once any user exists.
            userDao.insertDefaultIfEmpty(username = username, password = password)

            // Seeding should also ensure the default user's wallet exists (idempotent insert).
            val seededUser = userDao.fetchUser(username = username, password = password)
            if (seededUser != null) {
                walletDao.insertWalletIfMissing(userId = seededUser.id, pesoBalance = 1000.0)
            }
        }
    }
}
