package edu.uvg.myrecipeapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val recipeService: ApiService = retrofit.create(ApiService::class.java)

    private val _categorieState = mutableStateOf(RecipeState())
    val categoriesState: State<RecipeState> = _categorieState

    private val _mealsState = mutableStateOf(MealState())
    val mealsState: State<MealState> = _mealsState

    private val db = Room.databaseBuilder(application, RecipeDatabase::class.java, "recipe-db").build()

    private val _mealDetailState = mutableStateOf(MealDetailState())
    val mealDetailState: State<MealDetailState> = _mealDetailState

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        val isAvailable = networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        Log.d("InternetCheck", "Internet available: $isAvailable")
        return isAvailable
    }

    init {
        fetchCategories()
    }
    // Función para obtener las categorías
    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isInternetAvailable(getApplication<Application>().applicationContext)) {
                    // Try to fetch categories from the API
                    val response = recipeService.getCategories()

                    // If the response is valid, insert categories into the database
                    val categoryEntities = response.categories.map { category ->
                        CategoryEntity(
                            idCategory = category.idCategory,
                            strCategory = category.strCategory,
                            strCategoryThumb = category.strCategoryThumb,
                            strCategoryDescription = category.strCategoryDescription
                        )
                    }

                    db.categoryDao().insertAllCategories(categoryEntities)

                    withContext(Dispatchers.Main) {
                        _categorieState.value = _categorieState.value.copy(
                            list = response.categories,
                            loading = false,
                            error = null
                        )
                    }
                } else {
                    // If no internet, try to load categories from the local database
                    val categoriesFromDb = db.categoryDao().getAllCategories()
                    if (categoriesFromDb.isNotEmpty()) {
                        val categories = categoriesFromDb.map { categoryEntity ->
                            Category(
                                idCategory = categoryEntity.idCategory,
                                strCategory = categoryEntity.strCategory,
                                strCategoryThumb = categoryEntity.strCategoryThumb,
                                strCategoryDescription = categoryEntity.strCategoryDescription
                            )
                        }
                        withContext(Dispatchers.Main) {
                            _categorieState.value = _categorieState.value.copy(
                                list = categories,
                                loading = false,
                                error = null
                            )
                        }
                    } else {
                        // If no local categories, show an error message
                        withContext(Dispatchers.Main) {
                            _categorieState.value = _categorieState.value.copy(
                                loading = false,
                                error = "No categories available offline"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Capture any exception that occurs and log it
                Log.e("FetchCategoriesError", "Error fetching categories", e)
                withContext(Dispatchers.Main) {
                    _categorieState.value = _categorieState.value.copy(
                        loading = false,
                        error = "An error occurred: ${e.localizedMessage}"
                    )
                }
            }
        }
    }



    // Función para obtener recetas por categoría
    fun fetchMealsByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isInternetAvailable(getApplication<Application>().applicationContext)) {
                try {
                    val response = recipeService.getMealsByCategory(category)

                    // Insertar comidas en la base de datos local
                    val mealEntities = response.meals.map { meal ->
                        MealEntity(
                            idMeal = meal.idMeal,
                            strMeal = meal.strMeal,
                            strMealThumb = meal.strMealThumb,
                            strCategory = category
                        )
                    }
                    db.MealDao().insertMeals(mealEntities)

                    withContext(Dispatchers.Main) {
                        _mealsState.value = _mealsState.value.copy(
                            meals = response.meals,
                            loading = false,
                            error = null
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _mealsState.value = _mealsState.value.copy(
                            loading = false,
                            error = "Error fetching meals: ${e.localizedMessage}"
                        )
                    }
                }
            } else {
                // Cargar comidas desde la base de datos local si no hay internet
                val mealsFromDb = db.MealDao().getMealsByCategory(category)
                val meals = mealsFromDb.map { mealEntity ->
                    Meal(
                        idMeal = mealEntity.idMeal,
                        strMeal = mealEntity.strMeal,
                        strMealThumb = mealEntity.strMealThumb
                    )
                }

                withContext(Dispatchers.Main) {
                    _mealsState.value = _mealsState.value.copy(
                        meals = meals,
                        loading = false,
                        error = if (meals.isEmpty()) "No meals found offline for this category" else null
                    )
                }
            }
        }
    }


    // Función para obtener los detalles de una receta por ID
    fun fetchMealDetails(context: Context, mealId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isInternetAvailable(context)) {
                try {
                    // Obtener detalles desde el API
                    val response = recipeService.getMealDetailsById(mealId)
                    val mealDetail = response.meals.firstOrNull()

                    mealDetail?.let {
                        // Insertar detalles en la base de datos local
                        db.mealDetailDao().insertMealDetail(
                            MealDetailEntity(
                                idMeal = it.idMeal,
                                strMeal = it.strMeal,
                                strMealThumb = it.strMealThumb,
                                strInstructions = it.strInstructions
                            )
                        )
                    }

                    withContext(Dispatchers.Main) {
                        _mealDetailState.value = _mealDetailState.value.copy(
                            mealDetail = mealDetail,
                            loading = false,
                            error = if (mealDetail == null) "No se encontraron detalles de la receta" else null
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _mealDetailState.value = _mealDetailState.value.copy(
                            loading = false,
                            error = "Error al obtener los detalles: ${e.localizedMessage}"
                        )
                    }
                }
            } else {
                // Cargar los detalles desde la base de datos local
                val mealDetailEntity = db.mealDetailDao().getMealDetail(mealId)
                withContext(Dispatchers.Main) {
                    _mealDetailState.value = _mealDetailState.value.copy(
                        mealDetail = mealDetailEntity?.let {
                            MealDetail(it.idMeal, it.strMeal, it.strMealThumb, it.strInstructions)
                        },
                        loading = false,
                        error = if (mealDetailEntity == null) "Receta no encontrada en modo offline" else null
                    )
                }
            }
        }
    }


    // Estado para almacenar los detalles de la receta
    data class MealDetailState(
        val mealDetail: MealDetail? = null,
        val loading: Boolean = true,
        val error: String? = null
    )

    data class RecipeState(
        val loading: Boolean = true,
        val list: List<Category> = emptyList(),
        val error: String? = null
    )

    data class MealState(
        val loading: Boolean = true,
        val meals: List<Meal> = emptyList(),
        val error: String? = null
    )
}

