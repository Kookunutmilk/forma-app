package com.forma.app.data.ai

import com.forma.app.domain.model.ChatMessage
import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.Recipe
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.model.WeeklyRoutine
import com.forma.app.domain.repository.AiRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de IA que corre entera en el dispositivo. Es determinista: los mismos datos de
 * perfil producen siempre la misma rutina y el mismo plan de comidas, lo que hace la app usable
 * y testeable sin ninguna credencial.
 */
@Singleton
class LocalAiRepository @Inject constructor() : AiRepository {

    override val providerName: String = "FORMA local"

    override suspend fun generateWeeklyRoutine(profile: UserProfile): WeeklyRoutine {
        // Latencia simbólica para que los estados de carga de la UI sean reales.
        delay(220)
        return RoutineGenerator.generate(profile)
    }

    override suspend fun generateDayMeals(
        profile: UserProfile,
        day: WeekDay,
    ): Map<MealSlot, List<Recipe>> {
        delay(180)
        return MealPlanGenerator.optionsFor(profile, day)
    }

    override suspend fun answer(
        profile: UserProfile?,
        history: List<ChatMessage>,
        question: String,
    ): String {
        delay(600)
        return LocalCoach.answer(profile, question)
    }
}
