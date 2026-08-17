package com.example.bartthekeeper.data.local

import androidx.room.TypeConverter
import com.example.bartthekeeper.data.model.IngredientCategory

class Converters {

    @TypeConverter
    fun fromIngredientCategory(category: IngredientCategory?): String? {
        return category?.name
    }

    @TypeConverter
    fun toIngredientCategory(value: String?): IngredientCategory? {
        return value?.let { IngredientCategory.fromString(it) }
    }
}
