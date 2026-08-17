package com.example.bartthekeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bartthekeeper.data.local.AppPreferences
import com.example.bartthekeeper.data.model.MixResult
import com.example.bartthekeeper.data.model.RecipeMixability
import com.example.bartthekeeper.data.model.RecipeWithIngredients
import com.example.bartthekeeper.data.repository.MocktailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val repository: MocktailRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    val isAutoModeEnabled: StateFlow<Boolean> = preferences.isAutoModeEnabled

    private val _recipeId = MutableStateFlow<Long?>(null)
    private val _servings = MutableStateFlow(1)
    val servings: StateFlow<Int> = _servings.asStateFlow()

    private val _isMixing = MutableStateFlow(false)
    val isMixing: StateFlow<Boolean> = _isMixing.asStateFlow()

    private val _mixResult = MutableStateFlow<MixResult?>(null)
    val mixResult: StateFlow<MixResult?> = _mixResult.asStateFlow()

    val recipeDetails: StateFlow<RecipeMixability?> = combine(
        _recipeId,
        _servings,
        repository.allRecipes,
        repository.allInventory
    ) { id, currentServings, recipes, inventory ->
        if (id == null) return@combine null
        val recipe = recipes.firstOrNull { it.recipe.id == id } ?: return@combine null
        repository.checkMixability(recipe, inventory, currentServings)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun loadRecipe(id: Long) {
        _recipeId.value = id
    }

    fun setServings(count: Int) {
        _servings.value = count.coerceIn(1, 20)
    }

    fun incrementServings() {
        _servings.value = (_servings.value + 1).coerceAtMost(20)
    }

    fun decrementServings() {
        _servings.value = (_servings.value - 1).coerceAtLeast(1)
    }

    fun toggleFavorite() {
        val details = recipeDetails.value ?: return
        val currentFav = details.recipeWithIngredients.recipe.isFavorite
        val id = details.recipeWithIngredients.recipe.id
        viewModelScope.launch {
            repository.toggleFavorite(id, !currentFav)
        }
    }

    fun updateRating(newRating: Int) {
        val details = recipeDetails.value ?: return
        val id = details.recipeWithIngredients.recipe.id
        viewModelScope.launch {
            repository.updateRating(id, newRating)
        }
    }

    fun deleteRecipe(onDeleted: () -> Unit) {
        val details = recipeDetails.value ?: return
        val recipe = details.recipeWithIngredients.recipe
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
            onDeleted()
        }
    }

    /**
     * Executes cocktail mixing: automatically deducts ingredients from inventory
     */
    fun mixCocktail() {
        val details = recipeDetails.value ?: return
        val recipeId = details.recipeWithIngredients.recipe.id
        val currentServings = _servings.value

        viewModelScope.launch {
            _isMixing.value = true
            val result = repository.mixRecipe(recipeId, currentServings)
            _isMixing.value = false
            _mixResult.value = result
        }
    }

    fun clearMixResult() {
        _mixResult.value = null
    }
}
