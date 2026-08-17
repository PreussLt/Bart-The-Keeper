package com.example.bartthekeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.data.model.InventoryItemEntity
import com.example.bartthekeeper.data.repository.MocktailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class InventoryCategoryFilter {
    ALL,
    SIRUP,
    SAEFTE,
    ADD_INS,
    LOW_STOCK
}

class InventoryViewModel(
    private val repository: MocktailRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(InventoryCategoryFilter.ALL)
    val selectedFilter: StateFlow<InventoryCategoryFilter> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _itemToEdit = MutableStateFlow<InventoryItemEntity?>(null)
    val itemToEdit: StateFlow<InventoryItemEntity?> = _itemToEdit.asStateFlow()

    private val _isAddEditOpen = MutableStateFlow(false)
    val isAddEditOpen: StateFlow<Boolean> = _isAddEditOpen.asStateFlow()

    val rawInventory: StateFlow<List<InventoryItemEntity>> = repository.allInventory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredInventory: StateFlow<List<InventoryItemEntity>> = combine(
        repository.allInventory,
        _selectedFilter,
        _searchQuery
    ) { items, filter, query ->
        var list = items

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { it.name.lowercase().contains(q) || it.notes.lowercase().contains(q) }
        }

        when (filter) {
            InventoryCategoryFilter.ALL -> list
            InventoryCategoryFilter.SIRUP -> list.filter { it.category == IngredientCategory.SIRUP }
            InventoryCategoryFilter.SAEFTE -> list.filter { it.category == IngredientCategory.SAEFTE }
            InventoryCategoryFilter.ADD_INS -> list.filter { it.category == IngredientCategory.ADD_INS }
            InventoryCategoryFilter.LOW_STOCK -> list.filter { it.amount <= it.minThreshold && it.minThreshold > 0 || it.amount == 0.0 }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: InventoryCategoryFilter) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openAddItem() {
        _itemToEdit.value = null
        _isAddEditOpen.value = true
    }

    fun openEditItem(item: InventoryItemEntity) {
        _itemToEdit.value = item
        _isAddEditOpen.value = true
    }

    fun closeAddEdit() {
        _isAddEditOpen.value = false
        _itemToEdit.value = null
    }

    fun adjustAmount(id: Long, delta: Double) {
        viewModelScope.launch {
            repository.adjustInventoryAmount(id, delta)
        }
    }

    fun setExactAmount(id: Long, amount: Double) {
        viewModelScope.launch {
            repository.updateInventoryAmount(id, amount)
        }
    }

    fun saveItem(
        id: Long,
        name: String,
        category: IngredientCategory,
        amount: Double,
        unit: String,
        minThreshold: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val item = InventoryItemEntity(
                id = id,
                name = name.trim(),
                category = category,
                amount = maxOf(0.0, amount),
                unit = unit.trim(),
                minThreshold = maxOf(0.0, minThreshold),
                notes = notes.trim()
            )
            repository.saveInventoryItem(item)
            closeAddEdit()
        }
    }

    fun deleteItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaultData()
        }
    }
}
