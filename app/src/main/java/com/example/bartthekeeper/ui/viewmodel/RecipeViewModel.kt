package com.example.bartthekeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bartthekeeper.data.local.AppPreferences
import com.example.bartthekeeper.data.model.MixResult
import com.example.bartthekeeper.data.model.RecipeEntity
import com.example.bartthekeeper.data.model.RecipeMixability
import com.example.bartthekeeper.data.repository.MocktailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class RecipeFilter {
    ALL,
    MIXABLE_NOW,
    FAVORITES,
    TOP_RATED // Rating 8-10
}

enum class RecipeSort {
    RATING_DESC,
    NAME_ASC,
    NEWEST
}

class RecipeViewModel(
    private val repository: MocktailRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    val isAutoModeEnabled: StateFlow<Boolean> = preferences.isAutoModeEnabled

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(RecipeFilter.ALL)
    val selectedFilter: StateFlow<RecipeFilter> = _selectedFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(RecipeSort.RATING_DESC)
    val selectedSort: StateFlow<RecipeSort> = _selectedSort.asStateFlow()

    private val _quickMixResult = MutableStateFlow<MixResult?>(null)
    val quickMixResult: StateFlow<MixResult?> = _quickMixResult.asStateFlow()

    val filteredRecipes: StateFlow<List<RecipeMixability>> = combine(
        repository.recipesWithMixability,
        _searchQuery,
        _selectedFilter,
        _selectedSort
    ) { recipes, query, filter, sort ->
        var list = recipes

        // 1. Search Query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { mixability ->
                val recipe = mixability.recipeWithIngredients.recipe
                val ingredients = mixability.recipeWithIngredients.ingredients
                recipe.name.lowercase().contains(q) ||
                recipe.description.lowercase().contains(q) ||
                recipe.glassType.lowercase().contains(q) ||
                ingredients.any { it.name.lowercase().contains(q) }
            }
        }

        // 2. Filter
        list = when (filter) {
            RecipeFilter.ALL -> list
            RecipeFilter.MIXABLE_NOW -> list.filter { it.isFullyMixable }
            RecipeFilter.FAVORITES -> list.filter { it.recipeWithIngredients.recipe.isFavorite }
            RecipeFilter.TOP_RATED -> list.filter { it.recipeWithIngredients.recipe.rating >= 8 }
        }

        // 3. Sort
        when (sort) {
            RecipeSort.RATING_DESC -> list.sortedWith(
                compareByDescending<RecipeMixability> { it.recipeWithIngredients.recipe.rating }
                    .thenBy { it.recipeWithIngredients.recipe.name }
            )
            RecipeSort.NAME_ASC -> list.sortedBy { it.recipeWithIngredients.recipe.name.lowercase() }
            RecipeSort.NEWEST -> list.sortedByDescending { it.recipeWithIngredients.recipe.createdAt }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: RecipeFilter) {
        _selectedFilter.value = filter
    }

    fun setSort(sort: RecipeSort) {
        _selectedSort.value = sort
    }

    fun toggleFavorite(recipeId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId, !isFavorite)
        }
    }

    fun updateRating(recipeId: Long, rating: Int) {
        viewModelScope.launch {
            repository.updateRating(recipeId, rating)
        }
    }

    fun deleteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun quickMix(recipeId: Long, servings: Int = 1) {
        viewModelScope.launch {
            val result = repository.mixRecipe(recipeId, servings)
            _quickMixResult.value = result
        }
    }

    fun clearQuickMixResult() {
        _quickMixResult.value = null
    }
}
