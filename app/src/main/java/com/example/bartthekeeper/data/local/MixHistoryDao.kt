package com.example.bartthekeeper.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bartthekeeper.data.model.MixHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MixHistoryDao {

    @Query("SELECT * FROM mix_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<MixHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: MixHistoryEntity): Long

    @Delete
    suspend fun deleteHistory(history: MixHistoryEntity)

    @Query("DELETE FROM mix_history")
    suspend fun clearHistory()
}
