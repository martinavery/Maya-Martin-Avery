package com.example.maya_exam_martin_avery.data.local.mappers

import com.example.maya_exam_martin_avery.data.local.entities.UserEntity
import com.example.maya_exam_martin_avery.domain.model.UserDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMapperTest {
    private val mapper = UserMapper()

    @Test
    fun `toDomain maps username to domain userName`() {
        // Domain model should expose only the username used by the app/UI.
        val entity = UserEntity(id = 1L, username = "maya", password = "pw")

        val domain = mapper.toDomain(entity)

        assertEquals(UserDomain(userName = "maya"), domain)
    }
}

