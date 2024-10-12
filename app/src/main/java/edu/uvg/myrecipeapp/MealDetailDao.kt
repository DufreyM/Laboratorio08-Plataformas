package edu.uvg.myrecipeapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns

@Dao
interface MealDetailDao {
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM meal_details WHERE idMeal = :mealId")
    suspend fun getMealDetail(mealId: String): MealDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealDetail(mealDetail: MealDetailEntity): Long

}
