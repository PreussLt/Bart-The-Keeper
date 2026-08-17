package com.example.bartthekeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bartthekeeper.data.model.MixHistoryEntity
import com.example.bartthekeeper.data.repository.MocktailRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MixHistoryViewModel(
    private val repository: MocktailRepository
) : ViewModel() {

    val historyList: StateFlow<List<MixHistoryEntity>> = repository.mixHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
