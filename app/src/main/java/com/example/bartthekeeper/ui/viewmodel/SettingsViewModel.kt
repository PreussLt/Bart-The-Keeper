package com.example.bartthekeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bartthekeeper.data.local.AppPreferences
import com.example.bartthekeeper.data.local.AppThemeMode
import com.example.bartthekeeper.data.repository.MocktailRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: MocktailRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    val isAutoModeEnabled: StateFlow<Boolean> = preferences.isAutoModeEnabled
    val isLowStockWarningEnabled: StateFlow<Boolean> = preferences.isLowStockWarningEnabled
    val themeMode: StateFlow<AppThemeMode> = preferences.themeMode

    fun setAutoMode(enabled: Boolean) {
        preferences.setAutoModeEnabled(enabled)
    }

    fun setLowStockWarning(enabled: Boolean) {
        preferences.setLowStockWarningEnabled(enabled)
    }

    fun setThemeMode(mode: AppThemeMode) {
        preferences.setThemeMode(mode)
    }

    fun resetToDefaultData() {
        viewModelScope.launch {
            repository.resetToDefaultData()
        }
    }
}
