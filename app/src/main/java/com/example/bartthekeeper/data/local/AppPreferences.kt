package com.example.bartthekeeper.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val displayName: String) {
    SYSTEM("System-Standard"),
    LIGHT("Heller Modus"),
    DARK("Dunkler Modus")
}

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("bart_the_keeper_prefs", Context.MODE_PRIVATE)

    private val _isAutoModeEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_AUTO_MODE, false)
    )
    /**
     * Autonomie-Modus: Cocktails werden bei Klick auf "Mixen" direkt ohne Zwischenbestätigung
     * gemixt und die entsprechenden Zutatenmengen automatisch vom Vorrat abgezogen.
     */
    val isAutoModeEnabled: StateFlow<Boolean> = _isAutoModeEnabled.asStateFlow()

    private val _isLowStockWarningEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_LOW_STOCK_WARNING, true)
    )
    val isLowStockWarningEnabled: StateFlow<Boolean> = _isLowStockWarningEnabled.asStateFlow()

    private val _themeMode = MutableStateFlow(
        AppThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name)
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setAutoModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_MODE, enabled).apply()
        _isAutoModeEnabled.value = enabled
    }

    fun setLowStockWarningEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOW_STOCK_WARNING, enabled).apply()
        _isLowStockWarningEnabled.value = enabled
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    companion object {
        private const val KEY_AUTO_MODE = "key_auto_mode"
        private const val KEY_LOW_STOCK_WARNING = "key_low_stock_warning"
        private const val KEY_THEME_MODE = "key_theme_mode"

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
