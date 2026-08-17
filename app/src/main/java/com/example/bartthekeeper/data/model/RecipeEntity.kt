package com.example.bartthekeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val instructions: String = "",
    val glassType: String = "Highball Glas",
    val rating: Int = 8, // 1 to 10 rating
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
