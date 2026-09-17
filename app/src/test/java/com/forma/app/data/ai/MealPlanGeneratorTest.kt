package com.forma.app.data.ai

import com.forma.app.data.catalog.IngredientCatalog
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class MealPlanGeneratorTest {

    private val profile = UserProfile(
        id = "u_test",
        name = "Frida González",
        email = "frida@forma.app",
        weightKg = 60,
        heightCm = 165,
        age = 21,
        goal = Goal.GAIN_MUSCLE,
        likedIngredients = IngredientCatalog.all.map { it.id }.toSet(),
        onboardingCompleted = true,
    )

    @Test
    fun `cada comida ofrece cinco opciones sin repetir`() {
        val plan = MealPlanGenerator.optionsFor(profile, WeekDay.MONDAY)
        assertEquals(MealSlot.entries.size, plan.size)
        plan.forEach { (slot, options) ->
            assertEquals("$slot no trae 5 opciones", 5, options.size)
            assertEquals("$slot repite recetas", 5, options.map { it.id }.distinct().size)
            options.forEach { assertEquals("Receta fuera de su comida", slot, it.slot) }
        }
    }

    @Test
    fun `la seleccion por defecto se acerca a la meta calorica del dia`() {
        val plan = MealPlanGenerator.optionsFor(profile, WeekDay.MONDAY)
        val total = plan.values.sumOf { it.first().kcal }
        val target = profile.dailyCalories
        val deviation = abs(total - target).toDouble() / target
        assertTrue(
            "El plan suma $total kcal frente a una meta de $target",
            deviation <= 0.20,
        )
    }

    @Test
    fun `el plan cambia entre dias para no comer siempre lo mismo`() {
        val monday = MealPlanGenerator.optionsFor(profile, WeekDay.MONDAY)
        val thursday = MealPlanGenerator.optionsFor(profile, WeekDay.THURSDAY)
        val repeated = MealSlot.entries.count {
            monday.getValue(it).first().id == thursday.getValue(it).first().id
        }
        assertTrue("Lunes y jueves proponen el mismo menú completo", repeated < MealSlot.entries.size)
    }

    @Test
    fun `el mismo perfil y dia devuelven siempre el mismo plan`() {
        assertEquals(
            MealPlanGenerator.optionsFor(profile, WeekDay.WEDNESDAY),
            MealPlanGenerator.optionsFor(profile, WeekDay.WEDNESDAY),
        )
    }

    @Test
    fun `prioriza recetas con los ingredientes que le gustan al usuario`() {
        val picky = profile.copy(likedIngredients = setOf("pollo", "arroz", "brocoli", "huevo"))
        val plan = MealPlanGenerator.optionsFor(picky, WeekDay.MONDAY)
        val withLiked = plan.getValue(MealSlot.LUNCH).count { recipe ->
            recipe.ingredients.any { it.ingredientId in picky.likedIngredients }
        }
        assertTrue("Ninguna opción de almuerzo usa sus ingredientes", withLiked >= 3)
    }
}
