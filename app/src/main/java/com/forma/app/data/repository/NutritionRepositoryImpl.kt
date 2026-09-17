package com.forma.app.data.repository

import com.forma.app.data.catalog.IngredientCatalog
import com.forma.app.data.local.dao.NutritionDao
import com.forma.app.data.local.entity.MealChoiceEntity
import com.forma.app.domain.model.DayMealPlan
import com.forma.app.domain.model.Ingredient
import com.forma.app.domain.model.MealOptions
import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.repository.AiRepository
import com.forma.app.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionRepositoryImpl @Inject constructor(
    private val dao: NutritionDao,
    private val ai: AiRepository,
) : NutritionRepository {

    override suspend fun dayPlan(profile: UserProfile, day: WeekDay): DayMealPlan {
        val generated = ai.generateDayMeals(profile, day)
        val saved = dao.choicesForDay(day.index).associateBy { it.slotId }

        val meals = MealSlot.entries.mapNotNull { slot ->
            val options = generated[slot].orEmpty()
            if (options.isEmpty()) return@mapNotNull null
            val choice = saved[slot.id]
            val selectedId = choice?.recipeId?.takeIf { id -> options.any { it.id == id } }
                ?: options.first().id
            MealOptions(
                slot = slot,
                options = options,
                selectedId = selectedId,
                plateUri = choice?.plateUri,
            )
        }

        return DayMealPlan(day = day, meals = meals, targetKcal = profile.dailyCalories)
    }

    override suspend fun chooseOption(day: WeekDay, slot: MealSlot, recipeId: String) {
        val existing = dao.choice(day.index, slot.id)
        dao.upsert(
            MealChoiceEntity(
                dayIndex = day.index,
                slotId = slot.id,
                recipeId = recipeId,
                // Al cambiar de receta la foto del plato anterior deja de tener sentido.
                plateUri = if (existing?.recipeId == recipeId) existing.plateUri else null,
            ),
        )
    }

    override suspend fun setPlatePhoto(
        day: WeekDay,
        slot: MealSlot,
        recipeId: String,
        uri: String?,
    ) {
        val existing = dao.choice(day.index, slot.id)
        dao.upsert(
            existing?.copy(plateUri = uri) ?: MealChoiceEntity(
                dayIndex = day.index,
                slotId = slot.id,
                recipeId = recipeId,
                plateUri = uri,
            ),
        )
    }

    override fun ingredientCatalog(): List<Ingredient> = IngredientCatalog.all

    override fun choicesChanged(): Flow<Long> =
        dao.observeChoices().map { list -> list.sumOf { it.recipeId.hashCode().toLong() } + list.size }
}
