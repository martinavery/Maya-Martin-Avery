package com.example.maya_exam_martin_avery.di

import com.example.maya_exam_martin_avery.data.local.repository.UserRepositoryImpl
import com.example.maya_exam_martin_avery.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}