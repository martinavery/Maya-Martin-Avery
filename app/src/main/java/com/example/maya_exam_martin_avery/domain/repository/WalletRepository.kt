package com.example.maya_exam_martin_avery.domain.repository

import com.example.maya_exam_martin_avery.domain.model.WalletDomain

interface WalletRepository {
    suspend fun getWalletByUserId(userId: Long): Result<WalletDomain>

    // Persists a new balance for the user's wallet.
    suspend fun updateWalletBalance(userId: Long, newBalance: Double): Result<Unit>
}

