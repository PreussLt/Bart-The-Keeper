package com.example.bartthekeeper.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bartthekeeper.data.model.InventoryItemEntity
import com.example.bartthekeeper.data.model.MixHistoryEntity
import com.example.bartthekeeper.data.model.RecipeEntity
import com.example.bartthekeeper.data.model.RecipeIngredientEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        InventoryItemEntity::class,
        MixHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun mixHistoryDao(): MixHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bart_the_keeper.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(database: AppDatabase) {
                // Populate initial inventory
                val inventoryDao = database.inventoryDao()
                val recipeDao = database.recipeDao()

                InitialData.getDefaultInventory().forEach { item ->
                    inventoryDao.insertItem(item)
                }

                // Populate initial recipes
                InitialData.getDefaultRecipes().forEach { (recipe, ingredients) ->
                    recipeDao.insertRecipeWithIngredients(recipe, ingredients)
                }
            }
        }
    }
}
