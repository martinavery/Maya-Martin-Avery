package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.model.UserDomain
import com.example.maya_exam_martin_avery.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend fun invoke(userName: String, password: String): Result<UserDomain> {
       return userRepository.fetchUser(userName, password)
    }
}