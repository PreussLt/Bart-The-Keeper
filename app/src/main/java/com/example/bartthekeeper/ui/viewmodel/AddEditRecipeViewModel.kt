package com.example.bartthekeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.data.model.InventoryItemEntity
import com.example.bartthekeeper.data.model.RecipeEntity
import com.example.bartthekeeper.data.model.RecipeIngredientEntity
import com.example.bartthekeeper.data.model.UnitConverter
import com.example.bartthekeeper.data.repository.MocktailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class EditableIngredient(
    val tempId: String = UUID.randomUUID().toString(),
    val id: Long = 0L,
    val name: String = "",
    val category: IngredientCategory = IngredientCategory.SIRUP,
    val amountText: String = "",
    val unit: String = "cl"
)

class AddEditRecipeViewModel(
    private val repository: MocktailRepository
) : ViewModel() {

    private var editingRecipeId: Long = 0L

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _instructions = MutableStateFlow("")
    val instructions: StateFlow<String> = _instructions.asStateFlow()

    private val _glassType = MutableStateFlow("Highball Glas")
    val glassType: StateFlow<String> = _glassType.asStateFlow()

    private val _rating = MutableStateFlow(8) // 1 to 10 rating
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _ingredients = MutableStateFlow<List<EditableIngredient>>(
        listOf(
            EditableIngredient(category = IngredientCategory.SIRUP, unit = "cl"),
            EditableIngredient(category = IngredientCategory.SAEFTE, unit = "cl"),
            EditableIngredient(category = IngredientCategory.ADD_INS, unit = "Stk")
        )
    )
    val ingredients: StateFlow<List<EditableIngredient>> = _ingredients.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val availableInventoryItems: StateFlow<List<InventoryItemEntity>> = repository.allInventory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadRecipe(recipeId: Long) {
        if (recipeId == 0L || editingRecipeId == recipeId) return
        editingRecipeId = recipeId

        viewModelScope.launch {
            repository.getRecipeById(recipeId).collect { recipeWithIngs ->
                recipeWithIngs?.let {
                    _name.value = it.recipe.name
                    _description.value = it.recipe.description
                    _instructions.value = it.recipe.instructions
                    _glassType.value = it.recipe.glassType
                    _rating.value = it.recipe.rating
                    _isFavorite.value = it.recipe.isFavorite

                    _ingredients.value = it.ingredients.map { ing ->
                        EditableIngredient(
                            id = ing.id,
                            name = ing.name,
                            category = ing.category,
                            amountText = UnitConverter.formatAmount(ing.amount),
                            unit = ing.unit
                        )
                    }
                }
            }
        }
    }

    fun setName(value: String) {
        _name.value = value
        _errorMessage.value = null
    }

    fun setDescription(value: String) {
        _description.value = value
    }

    fun setInstructions(value: String) {
        _instructions.value = value
    }

    fun setGlassType(value: String) {
        _glassType.value = value
    }

    fun setRating(value: Int) {
        _rating.value = value.coerceIn(1, 10)
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun addIngredient(category: IngredientCategory) {
        val defaultUnit = when (category) {
            IngredientCategory.SIRUP -> "cl"
            IngredientCategory.SAEFTE -> "cl"
            IngredientCategory.ADD_INS -> "Stk"
        }
        val current = _ingredients.value.toMutableList()
        current.add(
            EditableIngredient(
                category = category,
                unit = defaultUnit
            )
        )
        _ingredients.value = current
    }

    fun updateIngredient(
        tempId: String,
        name: String? = null,
        category: IngredientCategory? = null,
        amountText: String? = null,
        unit: String? = null
    ) {
        _ingredients.value = _ingredients.value.map { ing ->
            if (ing.tempId == tempId) {
                ing.copy(
                    name = name ?: ing.name,
                    category = category ?: ing.category,
                    amountText = amountText ?: ing.amountText,
                    unit = unit ?: ing.unit
                )
            } else {
                ing
            }
        }
    }

    fun selectInventorySuggestion(tempId: String, item: InventoryItemEntity) {
        _ingredients.value = _ingredients.value.map { ing ->
            if (ing.tempId == tempId) {
                ing.copy(
                    name = item.name,
                    category = item.category,
                    unit = item.unit
                )
            } else {
                ing
            }
        }
    }

    fun removeIngredient(tempId: String) {
        _ingredients.value = _ingredients.value.filter { it.tempId != tempId }
    }

    fun saveRecipe(onSuccess: (Long) -> Unit) {
        val currentName = _name.value.trim()
        if (currentName.isBlank()) {
            _errorMessage.value = "Bitte gib dem Mocktail einen Namen."
            return
        }

        val validIngredients = _ingredients.value.filter { it.name.trim().isNotBlank() }
        if (validIngredients.isEmpty()) {
            _errorMessage.value = "Bitte füge mindestens eine Zutat hinzu."
            return
        }

        viewModelScope.launch {
            val recipe = RecipeEntity(
                id = editingRecipeId,
                name = currentName,
                description = _description.value.trim(),
                instructions = _instructions.value.trim(),
                glassType = _glassType.value.trim().ifBlank { "Highball Glas" },
                rating = _rating.value,
                isFavorite = _isFavorite.value
            )

            val ingredientEntities = validIngredients.map { ing ->
                val parsedAmount = ing.amountText.replace(',', '.').toDoubleOrNull() ?: 1.0
                RecipeIngredientEntity(
                    id = ing.id,
                    recipeId = editingRecipeId,
                    name = ing.name.trim(),
                    category = ing.category,
                    amount = maxOf(0.01, parsedAmount),
                    unit = ing.unit.trim().ifBlank { "cl" }
                )
            }

            val savedId = repository.saveRecipe(recipe, ingredientEntities)
            onSuccess(savedId)
        }
    }

    val GLASS_TYPES = listOf(
        "Highball Glas",
        "Tumbler Glas",
        "Hurricaneglas",
        "Ballonglas",
        "Longdrinkglas",
        "Cocktailschale",
        "Mason Jar",
        "Kupferbecher"
    )
}
