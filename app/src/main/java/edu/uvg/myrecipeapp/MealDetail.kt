package edu.uvg.myrecipeapp

data class MealDetail(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val strInstructions: String // Instrucciones para preparar el platillo
)

data class MealDetailResponse(val meals: List<MealDetail>)