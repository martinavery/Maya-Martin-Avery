package com.example.maya_exam_martin_avery.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Wallet is modeled as 1:1 with User; deleting a user deletes the related wallet row.
@Entity(
    tableName = "wallets",
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
    ],
)
data class WalletEntity(
    @PrimaryKey val userId: Long,
)