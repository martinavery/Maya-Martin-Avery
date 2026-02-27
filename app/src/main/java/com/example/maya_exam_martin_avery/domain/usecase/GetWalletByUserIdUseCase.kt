package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.model.WalletDomain
import com.example.maya_exam_martin_avery.domain.repository.WalletRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetWalletByUserIdUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(userId: Long): Result<WalletDomain> {
        return walletRepository.getWalletByUserId(userId)
    }
}

