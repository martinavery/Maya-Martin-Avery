package com.example.maya_exam_martin_avery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Minimal Room database used to verify Room + KSP + Hilt wiring.
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

