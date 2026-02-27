package com.example.maya_exam_martin_avery.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.maya_exam_martin_avery.data.local.entities.WalletEntity

@Dao
interface WalletDao {
    // Idempotent seed insert: if a wallet row already exists for the userId (PK), do nothing.
    @Query(
        """
        INSERT OR IGNORE INTO wallets(userId, balance)
        VALUES(:userId, :pesoBalance)
        """
    )
    suspend fun insertWalletIfMissing(userId: Long, pesoBalance: Double)

    // Returns null when no wallet exists for the given userId.
    @Query(
        """
        SELECT *
        FROM wallets
        WHERE userId = :userId
        LIMIT 1
        """
    )
    suspend fun getWalletByUserId(userId: Long): WalletEntity?

    // Returns number of rows affected; 0 means no wallet exists for userId.
    @Query(
        """
        UPDATE wallets
        SET balance = :newBalance
        WHERE userId = :userId
        """
    )
    suspend fun updateWalletBalance(userId: Long, newBalance: Double): Int
}

