package com.example.bartthekeeper.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bartthekeeper.data.model.IngredientCategory
import com.example.bartthekeeper.data.model.InventoryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {

    @Query("SELECT * FROM inventory_items ORDER BY category ASC, name ASC")
    fun getAllItems(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE category = :category ORDER BY name ASC")
    fun getItemsByCategory(category: IngredientCategory): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    fun getItemById(id: Long): Flow<InventoryItemEntity?>

    @Query("SELECT * FROM inventory_items WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name)) LIMIT 1")
    suspend fun getItemByName(name: String): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items")
    suspend fun getAllItemsOnce(): List<InventoryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<InventoryItemEntity>)

    @Update
    suspend fun updateItem(item: InventoryItemEntity)

    @Delete
    suspend fun deleteItem(item: InventoryItemEntity)

    @Query("UPDATE inventory_items SET amount = :newAmount, lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateAmount(id: Long, newAmount: Double, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM inventory_items")
    suspend fun deleteAll()
}
