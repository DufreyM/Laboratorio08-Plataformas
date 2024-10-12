package edu.uvg.myrecipeapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CategoryEntity::class, MealEntity::class, MealDetailEntity::class], version = 1)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun mealDetailDao(): MealDetailDao
}
