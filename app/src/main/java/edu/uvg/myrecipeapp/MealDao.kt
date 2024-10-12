package edu.uvg.myrecipeapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeals(meals: List<MealEntity>)

    @Query("SELECT * FROM MealEntity WHERE strCategory = :category")
    fun getMealsByCategory(category: String): List<MealEntity>
}

