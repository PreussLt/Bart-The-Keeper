package com.example.bartthekeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bartthekeeper.data.local.AppPreferences
import com.example.bartthekeeper.data.repository.MocktailRepository

class ViewModelFactory(
    private val repository: MocktailRepository,
    private val preferences: AppPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RecipeViewModel::class.java) -> {
                RecipeViewModel(repository, preferences) as T
            }
            modelClass.isAssignableFrom(RecipeDetailViewModel::class.java) -> {
                RecipeDetailViewModel(repository, preferences) as T
            }
            modelClass.isAssignableFrom(AddEditRecipeViewModel::class.java) -> {
                AddEditRecipeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(InventoryViewModel::class.java) -> {
                InventoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MixHistoryViewModel::class.java) -> {
                MixHistoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(repository, preferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
