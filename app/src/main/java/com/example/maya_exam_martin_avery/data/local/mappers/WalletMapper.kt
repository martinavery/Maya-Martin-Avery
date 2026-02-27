package com.example.maya_exam_martin_avery.data.local.mappers

import com.example.maya_exam_martin_avery.data.local.entities.WalletEntity
import com.example.maya_exam_martin_avery.domain.model.WalletDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletMapper @Inject constructor() {
    // Keep domain models independent of Room entities.
    fun toDomain(entity: WalletEntity): WalletDomain {
        return WalletDomain(
            userId = entity.userId,
            balance = entity.balance,
        )
    }
}

