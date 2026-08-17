package com.example.bartthekeeper.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class IngredientCategory(
    val displayName: String,
    val iconEmoji: String,
    val description: String
) {
    SIRUP(
        displayName = "Sirup",
        iconEmoji = "🍯",
        description = "Süße Sirupe wie Grenadine, Kokos, Vanille, Karamell"
    ),
    SAEFTE(
        displayName = "Säfte",
        iconEmoji = "🍊",
        description = "Fruchtsäfte wie Orangen-, Ananas-, Maracuja- & Limettensaft"
    ),
    ADD_INS(
        displayName = "Add-Ins",
        iconEmoji = "🌿",
        description = "Zutaten wie Minze, Soda, Sahne, Eis, Früchte & Spices"
    );

    companion object {
        fun fromString(value: String): IngredientCategory {
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) 
            } ?: ADD_INS
        }
    }
}
