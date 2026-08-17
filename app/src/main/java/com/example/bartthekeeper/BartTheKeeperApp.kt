package com.example.bartthekeeper

import android.app.Application
import com.example.bartthekeeper.data.local.AppDatabase
import com.example.bartthekeeper.data.local.AppPreferences
import com.example.bartthekeeper.data.repository.MocktailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BartTheKeeperApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy {
        AppDatabase.getDatabase(this, applicationScope)
    }

    val preferences by lazy {
        AppPreferences.getInstance(this)
    }

    val repository by lazy {
        MocktailRepository(
            recipeDao = database.recipeDao(),
            inventoryDao = database.inventoryDao(),
            mixHistoryDao = database.mixHistoryDao()
        )
    }
}
