package com.forma.app.data.repository

import com.forma.app.data.catalog.IngredientCatalog
import com.forma.app.data.local.dao.NutritionDao
import com.forma.app.data.local.entity.MealChoiceEntity
import com.forma.app.data.remote.CloudSyncManager
import com.forma.app.data.remote.FormaCloudStore
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
    private val cloud: FormaCloudStore,
    private val sync: CloudSyncManager,
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
        val entity = MealChoiceEntity(
            dayIndex = day.index,
            slotId = slot.id,
            recipeId = recipeId,
            plateUri = if (existing?.recipeId == recipeId) existing.plateUri else null,
        )
        dao.upsert(entity)
        pushMeal(entity)
    }

    override suspend fun setPlatePhoto(
        day: WeekDay,
        slot: MealSlot,
        recipeId: String,
        uri: String?,
    ) {
        val uploaded = sync.pushLocalFile(uri, "plates")
        val existing = dao.choice(day.index, slot.id)
        val entity = existing?.copy(plateUri = uploaded) ?: MealChoiceEntity(
            dayIndex = day.index,
            slotId = slot.id,
            recipeId = recipeId,
            plateUri = uploaded,
        )
        dao.upsert(entity)
        pushMeal(entity)
    }

    override fun ingredientCatalog(): List<Ingredient> = IngredientCatalog.all

    override fun choicesChanged(): Flow<Long> =
        dao.observeChoices().map { list -> list.sumOf { it.recipeId.hashCode().toLong() } + list.size }

    private suspend fun pushMeal(entity: MealChoiceEntity) {
        val uid = sync.currentUid() ?: return
        if (cloud.isEnabled) runCatching { cloud.upsertMealChoice(uid, entity) }
    }
}
