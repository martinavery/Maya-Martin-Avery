package com.example.maya_exam_martin_avery.data.local.repository

import com.example.maya_exam_martin_avery.data.local.dao.WalletDao
import com.example.maya_exam_martin_avery.data.local.mappers.WalletMapper
import com.example.maya_exam_martin_avery.domain.error.WalletAppException
import com.example.maya_exam_martin_avery.domain.error.WalletNotFoundException
import com.example.maya_exam_martin_avery.domain.model.WalletDomain
import com.example.maya_exam_martin_avery.domain.repository.WalletRepository
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val walletDao: WalletDao,
    private val walletMapper: WalletMapper,
) : WalletRepository {

    override suspend fun getWalletByUserId(userId: Long): Result<WalletDomain> {
        return try {
            val entity = walletDao.getWalletByUserId(userId)
            if (entity == null) {
                Result.failure(WalletNotFoundException(userId))
            } else {
                Result.success(walletMapper.toDomain(entity))
            }
        } catch (t: Throwable) {
            Result.failure(WalletAppException(t))
        }
    }
}

