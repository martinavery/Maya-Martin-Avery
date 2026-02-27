package com.example.maya_exam_martin_avery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.maya_exam_martin_avery.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: TransactionEntity): Long

    @Query(
        """
        SELECT *
        FROM transactions
        WHERE userId = :userId
        ORDER BY createdAtEpochMs DESC
        """
    )
    fun observeByUserId(userId: Long): Flow<List<TransactionEntity>>
}

