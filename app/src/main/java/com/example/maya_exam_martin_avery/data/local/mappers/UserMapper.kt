package com.example.maya_exam_martin_avery.data.local.mappers

import com.example.maya_exam_martin_avery.data.local.entities.UserEntity
import com.example.maya_exam_martin_avery.domain.model.UserDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserMapper  @Inject constructor(){
    // Domain model intentionally exposes only what's needed by the app/UI.
    fun toDomain(userEntity: UserEntity): UserDomain {
        return UserDomain(userName = userEntity.username, userId = userEntity.id)
    }
}