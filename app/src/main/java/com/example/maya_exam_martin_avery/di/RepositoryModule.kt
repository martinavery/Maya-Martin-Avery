package com.example.maya_exam_martin_avery.di

import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferences
import com.example.maya_exam_martin_avery.data.local.preferences.CurrentUserPreferencesImpl
import com.example.maya_exam_martin_avery.data.local.repository.UserRepositoryImpl
import com.example.maya_exam_martin_avery.data.local.repository.WalletRepositoryImpl
import com.example.maya_exam_martin_avery.data.repository.TransactionRepositoryImpl
import com.example.maya_exam_martin_avery.domain.repository.TransactionRepository
import com.example.maya_exam_martin_avery.domain.repository.UserRepository
import com.example.maya_exam_martin_avery.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCurrentUserPreferences(
        currentUserPreferencesImpl: CurrentUserPreferencesImpl,
    ): CurrentUserPreferences

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindWalletRepository(walletRepositoryImpl: WalletRepositoryImpl): WalletRepository

    @Binds
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl,
    ): TransactionRepository
}
