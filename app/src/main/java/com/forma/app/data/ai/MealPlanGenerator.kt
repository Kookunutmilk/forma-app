package com.forma.app.data.ai

import com.forma.app.data.catalog.RecipeCatalog
import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.Recipe
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import kotlin.math.abs
import kotlin.random.Random

/**
 * Arma 5 opciones por comida y por día. Cada receta se puntúa por dos cosas: cuántos de sus
 * ingredientes marcó el usuario como favoritos y qué tan cerca está del objetivo calórico
 * de ese momento del día. La rotación por día evita comer lo mismo toda la semana.
 */
object MealPlanGenerator {

    const val OPTIONS_PER_MEAL = 5

    fun optionsFor(profile: UserProfile, day: WeekDay): Map<MealSlot, List<Recipe>> {
        val liked = profile.likedIngredients
        val targetKcal = profile.dailyCalories
        val random = Random(profile.id.hashCode() xor (day.index * 977))

        return MealSlot.entries.associateWith { slot ->
            val slotTarget = targetKcal * slot.share
            val pool = RecipeCatalog.bySlot[slot].orEmpty()

            val scored = pool.map { recipe ->
                val matches = recipe.ingredients.count { it.ingredientId in liked }
                val coverage = if (recipe.ingredients.isEmpty()) {
                    0.0
                } else {
                    matches.toDouble() / recipe.ingredients.size
                }
                val kcalPenalty = abs(recipe.kcal - slotTarget) / slotTarget
                // Pequeño ruido determinista por día para rotar el orden sin perder coherencia.
                val jitter = random.nextDouble() * 0.12
                recipe to (coverage * 1.6 - kcalPenalty * 0.9 + jitter)
            }.sortedByDescending { it.second }

            // Rotamos la lista según el día para que el lunes y el jueves no propongan lo mismo.
            val ordered = scored.map { it.first }
            val rotation = day.index % maxOf(1, ordered.size)
            val rotated = ordered.drop(rotation) + ordered.take(rotation)

            val head = rotated.take(OPTIONS_PER_MEAL)
            if (head.size >= OPTIONS_PER_MEAL) head else (head + pool).distinct().take(OPTIONS_PER_MEAL)
        }
    }
}
