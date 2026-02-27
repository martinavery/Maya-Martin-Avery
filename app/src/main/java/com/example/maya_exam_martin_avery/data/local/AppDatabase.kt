package com.example.maya_exam_martin_avery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.maya_exam_martin_avery.data.local.dao.UserDao
import com.example.maya_exam_martin_avery.data.local.dao.WalletDao
import com.example.maya_exam_martin_avery.data.local.entities.UserEntity
import com.example.maya_exam_martin_avery.data.local.entities.WalletEntity

// Minimal Room database used to verify Room + KSP + Hilt wiring.
@Database(
    entities = [UserEntity::class, WalletEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun walletDao(): WalletDao
}

