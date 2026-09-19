package com.forma.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val AUTH = "auth"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val ROUTINE = "routine"
    const val DIET = "diet"
    const val COMMUNITY = "community"
    const val AI = "ai"
    const val LEARN = "learn"
    const val PROFILE = "profile"
    const val INGREDIENTS = "ingredients"
    const val NOTIFICATIONS = "notifications"

    const val RECIPE = "recipe/{recipeId}/{dayIndex}/{slotId}"
    fun recipe(recipeId: String, dayIndex: Int, slotId: String) = "recipe/$recipeId/$dayIndex/$slotId"

    const val ARTICLE = "article/{articleId}"
    fun article(id: String) = "article/$id"

    const val FINISH_WORKOUT = "finish/{dayIndex}"
    fun finishWorkout(dayIndex: Int) = "finish/$dayIndex"
}

data class BottomTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val icon: ImageVector,
)

val bottomTabs = listOf(
    BottomTab(Routes.HOME, "Inicio", Icons.Rounded.Home, Icons.Outlined.Home),
    BottomTab(
        Routes.ROUTINE,
        "Rutina",
        Icons.AutoMirrored.Rounded.ViewList,
        Icons.AutoMirrored.Outlined.ViewList,
    ),
    BottomTab(Routes.DIET, "Dieta", Icons.Rounded.Restaurant, Icons.Outlined.Restaurant),
    BottomTab(Routes.COMMUNITY, "Comunidad", Icons.Rounded.Groups, Icons.Outlined.Groups),
    BottomTab(Routes.AI, "IA", Icons.Rounded.AutoAwesome, Icons.Outlined.AutoAwesome),
    BottomTab(
        Routes.LEARN,
        "Aprender",
        Icons.AutoMirrored.Rounded.MenuBook,
        Icons.AutoMirrored.Outlined.MenuBook,
    ),
)
