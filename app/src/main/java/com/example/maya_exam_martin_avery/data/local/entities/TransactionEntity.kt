package com.example.maya_exam_martin_avery.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local source of truth for transactions created inside the app.
 *
 * We persist these because the fake API used for posting does not retain writes.
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["createdAtEpochMs"]),
    ],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val amount: Double,
    val description: String,
    val type: String,
    val createdAtEpochMs: Long,
)

