package com.example.bartthekeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mix_history")
data class MixHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val recipeId: Long,
    val recipeName: String,
    val servings: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String = ""
)
