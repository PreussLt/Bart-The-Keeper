package com.example.bartthekeeper.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<RecipeIngredientEntity>
) {
    val syrups: List<RecipeIngredientEntity>
        get() = ingredients.filter { it.category == IngredientCategory.SIRUP }

    val juices: List<RecipeIngredientEntity>
        get() = ingredients.filter { it.category == IngredientCategory.SAEFTE }

    val addIns: List<RecipeIngredientEntity>
        get() = ingredients.filter { it.category == IngredientCategory.ADD_INS }
}
