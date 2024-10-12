package edu.uvg.myrecipeapp

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "MealEntity")
data class MealEntity(
    @PrimaryKey val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val strCategory: String
)
