package edu.uvg.myrecipeapp

data class CategorieState(
    val loading: Boolean = false,
    val error: String? = null,
    val list: List<Category> = emptyList()
)
