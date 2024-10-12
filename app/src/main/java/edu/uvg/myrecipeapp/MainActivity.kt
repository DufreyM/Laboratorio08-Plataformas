package edu.uvg.myrecipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.uvg.myrecipeapp.ui.theme.MyRecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyRecipeAppTheme {
                // NavController para manejar la navegación
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Configuramos el NavHost para manejar la navegación entre pantallas
                    MyNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MyNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "categories", modifier = modifier) {
        // Pantalla de categorías
        composable("categories") {
            RecipeScreen(onCategoryClick = { category ->
                navController.navigate("meals/$category")
            })
        }

        // Pantalla de recetas por categoría
        composable("meals/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: return@composable
            MealScreen(category = category, onMealClick = { mealId ->
                navController.navigate("mealDetails/$mealId")
            })
        }

        // Nueva pantalla de detalles del platillo
        composable("mealDetails/{mealId}") { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: return@composable
            MealDetailScreen(mealId = mealId)
        }
    }
}
