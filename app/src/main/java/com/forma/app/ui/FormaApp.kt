package com.forma.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.LoadingState
import com.forma.app.ui.ai.AiScreen
import com.forma.app.ui.auth.AuthScreen
import com.forma.app.ui.community.CommunityScreen
import com.forma.app.ui.diet.DietScreen
import com.forma.app.ui.diet.IngredientsScreen
import com.forma.app.ui.diet.RecipeDetailScreen
import com.forma.app.ui.home.HomeScreen
import com.forma.app.ui.home.NotificationsScreen
import com.forma.app.ui.learn.ArticleScreen
import com.forma.app.ui.learn.LearnScreen
import com.forma.app.ui.navigation.FormaBottomBar
import com.forma.app.ui.navigation.Routes
import com.forma.app.ui.navigation.bottomTabs
import com.forma.app.ui.onboarding.OnboardingScreen
import com.forma.app.ui.profile.ProfileScreen
import com.forma.app.ui.routine.FinishWorkoutScreen
import com.forma.app.ui.routine.RoutineScreen

@Composable
fun FormaApp(viewModel: RootViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        // En tablets o pantallas muy anchas la app se queda en una columna legible y centrada.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = MaxContentWidth),
        ) {
            when (state) {
                RootState.Loading -> LoadingState(
                    message = "Cargando FORMA…",
                    modifier = Modifier.align(Alignment.Center),
                )

                RootState.NeedsAuth -> AuthScreen()

                is RootState.NeedsOnboarding -> OnboardingScreen(
                    user = (state as RootState.NeedsOnboarding).user,
                )

                is RootState.Ready -> MainNavigation()
            }
        }
    }
}

@Composable
private fun MainNavigation(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomTabs.any { it.route == currentRoute }

    Box(Modifier.fillMaxSize().background(FormaBackground)) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenTab = { route -> navController.navigateToTab(route) },
                    onOpenArticle = { id -> navController.navigate(Routes.article(id)) },
                    onOpenProfile = { navController.navigate(Routes.PROFILE) },
                    onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                )
            }

            composable(Routes.ROUTINE) {
                RoutineScreen(
                    onFinishWorkout = { dayIndex ->
                        navController.navigate(Routes.finishWorkout(dayIndex))
                    },
                )
            }

            composable(Routes.DIET) {
                DietScreen(
                    onOpenRecipe = { recipeId, dayIndex, slotId ->
                        navController.navigate(Routes.recipe(recipeId, dayIndex, slotId))
                    },
                    onOpenIngredients = { navController.navigate(Routes.INGREDIENTS) },
                )
            }

            composable(Routes.COMMUNITY) {
                CommunityScreen(onOpenProfile = { navController.navigate(Routes.PROFILE) })
            }

            composable(Routes.AI) { AiScreen() }

            composable(Routes.LEARN) {
                LearnScreen(onOpenArticle = { id -> navController.navigate(Routes.article(id)) })
            }

            composable(Routes.PROFILE) {
                ProfileScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.NOTIFICATIONS) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.INGREDIENTS) {
                IngredientsScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = Routes.ARTICLE,
                arguments = listOf(navArgument("articleId") { type = NavType.StringType }),
            ) {
                ArticleScreen(
                    onBack = { navController.popBackStack() },
                    onOpenArticle = { id -> navController.navigate(Routes.article(id)) },
                )
            }

            composable(
                route = Routes.RECIPE,
                arguments = listOf(
                    navArgument("recipeId") { type = NavType.StringType },
                    navArgument("dayIndex") { type = NavType.IntType },
                    navArgument("slotId") { type = NavType.StringType },
                ),
            ) {
                RecipeDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = Routes.FINISH_WORKOUT,
                arguments = listOf(navArgument("dayIndex") { type = NavType.IntType }),
            ) {
                FinishWorkoutScreen(
                    onBack = { navController.popBackStack() },
                    onPublished = {
                        navController.popBackStack()
                        navController.navigateToTab(Routes.COMMUNITY)
                    },
                )
            }
        }

        AnimatedVisibility(
            visible = showBottomBar,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
        ) {
            FormaBottomBar(
                currentRoute = currentRoute,
                onSelect = { route -> navController.navigateToTab(route) },
            )
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Alto reservado para que el contenido no quede debajo de la barra inferior. */
val BottomBarSpacing = 112.dp

/** Ancho máximo de la columna de contenido; evita líneas larguísimas en pantallas anchas. */
val MaxContentWidth = 600.dp
