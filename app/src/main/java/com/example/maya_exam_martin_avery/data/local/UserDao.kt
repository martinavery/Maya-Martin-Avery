package com.example.maya_exam_martin_avery.data.local

import androidx.room.Dao
import androidx.room.Query

// Minimal DAO used to verify Room code generation (KSP).
@Dao
interface UserDao {
    // Atomic "seed" insert: inserts only if the users table is empty (prevents duplicates on restart).
    @Query(
        """
        INSERT INTO users(username, password)
        SELECT :username, :password
        WHERE NOT EXISTS (SELECT 1 FROM users)
        """
    )
    suspend fun insertDefaultIfEmpty(username: String, password: String)

    @Query("""
    -- Return the full row so Room can map it into UserEntity.
    SELECT *
    FROM users
    WHERE username = :username
      AND password = :password
    LIMIT 1
""")
    suspend fun fetchUser(username: String, password: String): UserEntity?
}

