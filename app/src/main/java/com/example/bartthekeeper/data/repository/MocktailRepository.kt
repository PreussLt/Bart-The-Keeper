package com.example.bartthekeeper.data.repository

import com.example.bartthekeeper.data.local.AppDatabase
import com.example.bartthekeeper.data.local.InitialData
import com.example.bartthekeeper.data.local.InventoryDao
import com.example.bartthekeeper.data.local.MixHistoryDao
import com.example.bartthekeeper.data.local.RecipeDao
import com.example.bartthekeeper.data.model.DeductedItem
import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.data.model.IngredientStockStatus
import com.example.bartthekeeper.data.model.InventoryItemEntity
import com.example.bartthekeeper.data.model.MixHistoryEntity
import com.example.bartthekeeper.data.model.MixResult
import com.example.bartthekeeper.data.model.RecipeEntity
import com.example.bartthekeeper.data.model.RecipeIngredientEntity
import com.example.bartthekeeper.data.model.RecipeMixability
import com.example.bartthekeeper.data.model.RecipeWithIngredients
import com.example.bartthekeeper.data.model.UnitConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class MocktailRepository(
    private val recipeDao: RecipeDao,
    private val inventoryDao: InventoryDao,
    private val mixHistoryDao: MixHistoryDao
) {
    val allRecipes: Flow<List<RecipeWithIngredients>> = recipeDao.getAllRecipes()
    val allInventory: Flow<List<InventoryItemEntity>> = inventoryDao.getAllItems()
    val mixHistory: Flow<List<MixHistoryEntity>> = mixHistoryDao.getAllHistory()

    fun getRecipeById(id: Long): Flow<RecipeWithIngredients?> = recipeDao.getRecipeById(id)

    // Flow of recipes combined with mixability info
    val recipesWithMixability: Flow<List<RecipeMixability>> = combine(
        allRecipes,
        allInventory
    ) { recipes, inventory ->
        recipes.map { recipe ->
            checkMixability(recipe, inventory, servings = 1)
        }
    }

    suspend fun saveRecipe(recipe: RecipeEntity, ingredients: List<RecipeIngredientEntity>): Long {
        return if (recipe.id == 0L) {
            recipeDao.insertRecipeWithIngredients(recipe, ingredients)
        } else {
            recipeDao.updateRecipeWithIngredients(recipe, ingredients)
            recipe.id
        }
    }

    suspend fun deleteRecipe(recipe: RecipeEntity) {
        recipeDao.deleteRecipe(recipe)
    }

    suspend fun toggleFavorite(recipeId: Long, isFavorite: Boolean) {
        recipeDao.updateFavorite(recipeId, isFavorite)
    }

    suspend fun updateRating(recipeId: Long, rating: Int) {
        val validRating = rating.coerceIn(1, 10)
        recipeDao.updateRating(recipeId, validRating)
    }

    suspend fun saveInventoryItem(item: InventoryItemEntity): Long {
        return if (item.id == 0L) {
            inventoryDao.insertItem(item)
        } else {
            inventoryDao.updateItem(item)
            item.id
        }
    }

    suspend fun updateInventoryAmount(id: Long, newAmount: Double) {
        val clamped = maxOf(0.0, newAmount)
        inventoryDao.updateAmount(id, clamped)
    }

    suspend fun adjustInventoryAmount(id: Long, delta: Double) {
        val all = inventoryDao.getAllItemsOnce()
        val item = all.firstOrNull { it.id == id } ?: return
        val newAmount = maxOf(0.0, item.amount + delta)
        inventoryDao.updateAmount(id, newAmount)
    }

    suspend fun deleteInventoryItem(item: InventoryItemEntity) {
        inventoryDao.deleteItem(item)
    }

    suspend fun clearHistory() {
        mixHistoryDao.clearHistory()
    }

    suspend fun resetToDefaultData() {
        inventoryDao.deleteAll()
        InitialData.getDefaultInventory().forEach { inventoryDao.insertItem(it) }
        InitialData.getDefaultRecipes().forEach { (recipe, ingredients) ->
            recipeDao.insertRecipeWithIngredients(recipe, ingredients)
        }
    }

    /**
     * Checks if a recipe can be mixed with current inventory.
     */
    fun checkMixability(
        recipe: RecipeWithIngredients,
        inventory: List<InventoryItemEntity>,
        servings: Int = 1
    ): RecipeMixability {
        val statuses = recipe.ingredients.map { ingredient ->
            val requiredAmount = ingredient.amount * servings
            val matchingItem = findMatchingInventoryItem(ingredient.name, ingredient.category, inventory)

            if (matchingItem == null) {
                IngredientStockStatus(
                    ingredient = ingredient,
                    requiredAmount = requiredAmount,
                    inventoryItem = null,
                    availableAmountInIngredientUnit = null,
                    isSufficient = false,
                    missingAmount = requiredAmount
                )
            } else {
                // Convert matching item amount into the unit the recipe ingredient uses
                val availableInRecipeUnit = UnitConverter.convert(
                    amount = matchingItem.amount,
                    fromUnit = matchingItem.unit,
                    toUnit = ingredient.unit
                )

                if (availableInRecipeUnit != null) {
                    val isSufficient = availableInRecipeUnit >= requiredAmount - 0.0001
                    val missingAmount = maxOf(0.0, requiredAmount - availableInRecipeUnit)
                    IngredientStockStatus(
                        ingredient = ingredient,
                        requiredAmount = requiredAmount,
                        inventoryItem = matchingItem,
                        availableAmountInIngredientUnit = availableInRecipeUnit,
                        isSufficient = isSufficient,
                        missingAmount = missingAmount
                    )
                } else {
                    // Units not directly convertible - fallback to raw amount check if names match
                    val isSufficient = matchingItem.amount >= requiredAmount
                    val missingAmount = maxOf(0.0, requiredAmount - matchingItem.amount)
                    IngredientStockStatus(
                        ingredient = ingredient,
                        requiredAmount = requiredAmount,
                        inventoryItem = matchingItem,
                        availableAmountInIngredientUnit = matchingItem.amount,
                        isSufficient = isSufficient,
                        missingAmount = missingAmount
                    )
                }
            }
        }

        return RecipeMixability(
            recipeWithIngredients = recipe,
            servings = servings,
            ingredientStatuses = statuses
        )
    }

    /**
     * Mixes a recipe, deducting the required ingredient amounts from the inventory in real-time.
     */
    suspend fun mixRecipe(recipeId: Long, servings: Int = 1): MixResult {
        val recipe = recipeDao.getRecipeByIdOnce(recipeId)
            ?: return MixResult.Failure("Rezept mit ID $recipeId wurde nicht gefunden.", emptyList())

        val inventory = inventoryDao.getAllItemsOnce()
        val mixability = checkMixability(recipe, inventory, servings)

        val deductedItems = mutableListOf<DeductedItem>()

        // Perform deduction for all matched items
        for (status in mixability.ingredientStatuses) {
            val matchingItem = status.inventoryItem
            if (matchingItem != null) {
                // Determine how much to deduct in inventory item's unit
                val requiredInInventoryUnit = UnitConverter.convert(
                    amount = status.requiredAmount,
                    fromUnit = status.ingredient.unit,
                    toUnit = matchingItem.unit
                ) ?: status.requiredAmount

                val newAmount = maxOf(0.0, matchingItem.amount - requiredInInventoryUnit)
                inventoryDao.updateAmount(matchingItem.id, newAmount)

                deductedItems.add(
                    DeductedItem(
                        itemName = matchingItem.name,
                        category = matchingItem.category,
                        deductedAmount = requiredInInventoryUnit,
                        unit = matchingItem.unit,
                        remainingAmount = newAmount
                    )
                )
            }
        }

        // Record in Mix History
        val details = buildString {
            append("Gemixt: ${recipe.recipe.name} (${servings} Portion${if (servings > 1) "en" else ""}). ")
            if (deductedItems.isNotEmpty()) {
                append("Abgezogen: ")
                append(deductedItems.joinToString(", ") { "${UnitConverter.formatAmount(it.deductedAmount)} ${it.unit} ${it.itemName}" })
            }
        }

        mixHistoryDao.insertHistory(
            MixHistoryEntity(
                recipeId = recipe.recipe.id,
                recipeName = recipe.recipe.name,
                servings = servings,
                details = details
            )
        )

        return MixResult.Success(
            recipeName = recipe.recipe.name,
            servings = servings,
            deductedItems = deductedItems
        )
    }

    private fun findMatchingInventoryItem(
        ingredientName: String,
        category: IngredientCategory,
        inventory: List<InventoryItemEntity>
    ): InventoryItemEntity? {
        val cleanName = ingredientName.trim().lowercase()

        // 1. Exact match (case insensitive)
        val exact = inventory.firstOrNull { it.name.trim().equals(cleanName, ignoreCase = true) }
        if (exact != null) return exact

        // 2. Fuzzy match in same category
        val categoryMatch = inventory.firstOrNull { item ->
            val itemName = item.name.trim().lowercase()
            item.category == category && (
                cleanName.contains(itemName) || itemName.contains(cleanName)
            )
        }
        if (categoryMatch != null) return categoryMatch

        // 3. Any category contains match
        return inventory.firstOrNull { item ->
            val itemName = item.name.trim().lowercase()
            cleanName.contains(itemName) || itemName.contains(cleanName)
        }
    }
}
