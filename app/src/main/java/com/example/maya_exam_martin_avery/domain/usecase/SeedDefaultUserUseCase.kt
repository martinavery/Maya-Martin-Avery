package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.repository.UserRepository
import javax.inject.Inject

class SeedDefaultUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        // Centralizes the app's default login for first-run seeding.
        return userRepository.seedDefaultUserIfEmpty(
            username = DEFAULT_USERNAME,
            password = DEFAULT_PASSWORD,
        )
    }

    private companion object {
        const val DEFAULT_USERNAME = "admin"
        const val DEFAULT_PASSWORD = "admin"
    }
}

