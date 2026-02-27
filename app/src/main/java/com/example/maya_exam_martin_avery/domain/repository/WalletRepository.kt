package com.example.maya_exam_martin_avery.domain.repository

import com.example.maya_exam_martin_avery.domain.model.WalletDomain

interface WalletRepository {
    suspend fun getWalletByUserId(userId: Long): Result<WalletDomain>
}

