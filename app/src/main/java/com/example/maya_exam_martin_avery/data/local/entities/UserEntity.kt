package com.example.maya_exam_martin_avery.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Minimal Room entity used to verify Room + KSP + Hilt wiring.
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String = "",
    val password: String = "" //Plain text for demo purposes only
)