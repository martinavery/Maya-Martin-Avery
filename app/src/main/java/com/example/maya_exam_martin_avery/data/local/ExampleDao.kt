package com.example.maya_exam_martin_avery.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Minimal DAO used to verify Room code generation (KSP).
@Dao
interface ExampleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ExampleItem): Long

    @Query("SELECT * FROM example_items ORDER BY id DESC")
    suspend fun getAll(): List<ExampleItem>
}

