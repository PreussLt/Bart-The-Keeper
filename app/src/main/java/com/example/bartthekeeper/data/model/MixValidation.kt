package com.example.bartthekeeper.data.model

data class IngredientStockStatus(
    val ingredient: RecipeIngredientEntity,
    val requiredAmount: Double,
    val inventoryItem: InventoryItemEntity?,
    val availableAmountInIngredientUnit: Double?,
    val isSufficient: Boolean,
    val missingAmount: Double
)

data class RecipeMixability(
    val recipeWithIngredients: RecipeWithIngredients,
    val servings: Int = 1,
    val ingredientStatuses: List<IngredientStockStatus>
) {
    val isFullyMixable: Boolean
        get() = ingredientStatuses.isNotEmpty() && ingredientStatuses.all { it.isSufficient }

    val mixableIngredientsCount: Int
        get() = ingredientStatuses.count { it.isSufficient }

    val totalIngredientsCount: Int
        get() = ingredientStatuses.size

    val availableRatio: Float
        get() = if (totalIngredientsCount > 0) mixableIngredientsCount.toFloat() / totalIngredientsCount else 0f

    val missingIngredients: List<IngredientStockStatus>
        get() = ingredientStatuses.filter { !it.isSufficient }
}

sealed class MixResult {
    data class Success(
        val recipeName: String,
        val servings: Int,
        val deductedItems: List<DeductedItem>
    ) : MixResult()

    data class Failure(
        val reason: String,
        val missingItems: List<IngredientStockStatus>
    ) : MixResult()
}

data class DeductedItem(
    val itemName: String,
    val category: IngredientCategory,
    val deductedAmount: Double,
    val unit: String,
    val remainingAmount: Double
)
