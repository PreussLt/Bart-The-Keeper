package com.example.bartthekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bartthekeeper.data.local.AppThemeMode
import com.example.bartthekeeper.ui.navigation.BartNavGraph
import com.example.bartthekeeper.ui.theme.BartTheKeeperTheme
import com.example.bartthekeeper.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as BartTheKeeperApp
        val viewModelFactory = ViewModelFactory(app.repository, app.preferences)

        setContent {
            val themeMode by app.preferences.themeMode.collectAsState()
            val isDark = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            BartTheKeeperTheme(darkTheme = isDark) {
                BartNavGraph(viewModelFactory = viewModelFactory)
            }
        }
    }
}
