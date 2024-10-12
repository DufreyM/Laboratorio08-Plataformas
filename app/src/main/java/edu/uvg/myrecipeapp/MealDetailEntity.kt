package edu.uvg.myrecipeapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_details")
data class MealDetailEntity(
    @PrimaryKey val idMeal: String,
    @ColumnInfo(name = "strMeal") val strMeal: String,
    @ColumnInfo(name = "strMealThumb") val strMealThumb: String,
    @ColumnInfo(name = "strInstructions") val strInstructions: String
)

