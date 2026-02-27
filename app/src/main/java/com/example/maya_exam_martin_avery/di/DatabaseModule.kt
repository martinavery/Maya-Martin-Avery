package com.example.maya_exam_martin_avery.di

import android.content.Context
import androidx.room.Room
import com.example.maya_exam_martin_avery.data.local.AppDatabase
import com.example.maya_exam_martin_avery.data.local.dao.UserDao
import com.example.maya_exam_martin_avery.data.local.dao.WalletDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Provides a singleton Room database + DAO via Hilt.
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "maya.db")
            // Keeps the sample DB from crashing at runtime during schema iteration.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    fun provideWalletDao(db: AppDatabase): WalletDao {
        return db.walletDao()
    }
}

