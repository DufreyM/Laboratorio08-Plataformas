package edu.uvg.myrecipeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

@Composable
fun ShowMealDetail(viewState: MainViewModel.MealDetailState) {
    viewState.mealDetail?.let { meal ->
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Image(
                    painter = rememberAsyncImagePainter(meal.strMealThumb),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                )

                Text(
                    text = meal.strMeal,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Instructions:",
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = meal.strInstructions,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MealDetailScreen(mealId: String) {
    val viewModel: MainViewModel = viewModel()
    val viewState by viewModel.mealDetailState
    val context = LocalContext.current  // Obtenemos el contexto actual

    // Llamamos a la función que busca los detalles del platillo al iniciar
    LaunchedEffect(key1 = mealId) {
        viewModel.fetchMealDetails(context, mealId)  // Pasamos el contexto
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            viewState.loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            viewState.error != null -> {
                Text("Error occurred: ${viewState.error}", modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                ShowMealDetail(viewState = viewState)
            }
        }
    }
}

