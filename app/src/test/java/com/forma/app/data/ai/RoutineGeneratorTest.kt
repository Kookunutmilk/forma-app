package com.forma.app.data.ai

import com.forma.app.data.catalog.EquipmentCatalog
import com.forma.app.domain.model.ExperienceLevel
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.Sport
import com.forma.app.domain.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutineGeneratorTest {

    private fun profile(
        sport: Sport = Sport.GYM,
        level: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
        goal: Goal = Goal.GAIN_MUSCLE,
    ) = UserProfile(
        id = "u_test",
        name = "Frida González",
        email = "frida@forma.app",
        sport = sport,
        level = level,
        goal = goal,
        equipment = EquipmentCatalog.defaultSelection(sport),
        onboardingCompleted = true,
    )

    @Test
    fun `la rutina cubre los siete dias de la semana`() {
        val routine = RoutineGenerator.generate(profile())
        assertEquals(7, routine.sessions.size)
        assertEquals(7, routine.sessions.map { it.day }.distinct().size)
    }

    @Test
    fun `el mismo perfil siempre produce la misma rutina`() {
        val first = RoutineGenerator.generate(profile())
        val second = RoutineGenerator.generate(profile())
        assertEquals(first, second)
    }

    @Test
    fun `cada dia de entreno trae ejercicios y una estimacion de esfuerzo`() {
        val routine = RoutineGenerator.generate(profile())
        val training = routine.sessions.filterNot { it.isRest }
        assertTrue("Debe haber al menos 3 días de entreno", training.size >= 3)
        training.forEach { session ->
            assertTrue("${session.title} sin ejercicios", session.exercises.isNotEmpty())
            assertTrue("${session.title} sin minutos", session.estimatedMinutes > 0)
            assertTrue("${session.title} sin kcal", session.estimatedKcal > 0)
        }
    }

    @Test
    fun `la rutina de gym solo usa equipo que el usuario marco`() {
        val available = EquipmentCatalog.defaultSelection(Sport.GYM)
        val routine = RoutineGenerator.generate(profile())
        routine.sessions
            .flatMap { it.exercises }
            .mapNotNull { it.equipmentId }
            .forEach { assertTrue("Equipo no disponible: $it", it in available) }
    }

    @Test
    fun `cada deporte genera su propia rutina`() {
        Sport.entries.forEach { sport ->
            val routine = RoutineGenerator.generate(profile(sport = sport))
            assertEquals(sport, routine.sport)
            assertTrue(
                "$sport no generó sesiones de entreno",
                routine.sessions.any { !it.isRest && it.exercises.isNotEmpty() },
            )
        }
    }
}
