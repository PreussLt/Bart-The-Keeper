package com.example.bartthekeeper.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_items",
    indices = [Index(value = ["name"], unique = true)]
)
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val category: IngredientCategory,
    val amount: Double,
    val unit: String,
    val minThreshold: Double = 0.0,
    val notes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
