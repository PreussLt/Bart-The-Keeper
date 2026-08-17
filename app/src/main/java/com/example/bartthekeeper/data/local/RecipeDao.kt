package com.example.bartthekeeper.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.bartthekeeper.data.model.RecipeEntity
import com.example.bartthekeeper.data.model.RecipeIngredientEntity
import com.example.bartthekeeper.data.model.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Long): Flow<RecipeWithIngredients?>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeByIdOnce(id: Long): RecipeWithIngredients?

    @Transaction
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteRecipes(): Flow<List<RecipeWithIngredients>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<RecipeIngredientEntity>)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: Long)

    @Query("UPDATE recipes SET rating = :rating WHERE id = :recipeId")
    suspend fun updateRating(recipeId: Long, rating: Int)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavorite(recipeId: Long, isFavorite: Boolean)

    @Transaction
    suspend fun insertRecipeWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>
    ): Long {
        val recipeId = insertRecipe(recipe)
        val ingredientsWithRecipeId = ingredients.map { it.copy(recipeId = recipeId) }
        insertIngredients(ingredientsWithRecipeId)
        return recipeId
    }

    @Transaction
    suspend fun updateRecipeWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>
    ) {
        updateRecipe(recipe)
        deleteIngredientsByRecipeId(recipe.id)
        val ingredientsWithRecipeId = ingredients.map { it.copy(recipeId = recipe.id) }
        insertIngredients(ingredientsWithRecipeId)
    }
}
