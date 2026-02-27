package com.example.maya_exam_martin_avery.data.local.mappers

import com.example.maya_exam_martin_avery.data.local.entities.WalletEntity
import com.example.maya_exam_martin_avery.domain.model.WalletDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class WalletMapperTest {
    private val mapper = WalletMapper()

    @Test
    fun `toDomain maps wallet entity to domain`() {
        // Domain model should stay independent of Room entities.
        val entity = WalletEntity(userId = 7L, balance = 42.5)

        val domain = mapper.toDomain(entity)

        assertEquals(WalletDomain(userId = entity.userId, balance = entity.balance), domain)
    }
}

