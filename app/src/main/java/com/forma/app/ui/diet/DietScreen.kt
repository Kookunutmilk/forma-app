package com.forma.app.ui.diet

import androidx.compose.runtime.Composable

@Composable
fun DietScreen(
    onOpenRecipe: (String, Int, String) -> Unit,
    onOpenIngredients: () -> Unit,
) = Unit

@Composable
fun RecipeDetailScreen(onBack: () -> Unit) = Unit

@Composable
fun IngredientsScreen(onBack: () -> Unit) = Unit
