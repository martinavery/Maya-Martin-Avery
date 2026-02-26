package com.example.maya_exam_martin_avery.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Minimal Room entity used to verify Room + KSP + Hilt wiring.
@Entity(tableName = "example_items")
data class ExampleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

