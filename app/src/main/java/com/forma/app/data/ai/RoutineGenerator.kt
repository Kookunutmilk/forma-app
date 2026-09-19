package com.forma.app.data.ai

import com.forma.app.data.catalog.CatalogExercise
import com.forma.app.data.catalog.GymExerciseCatalog
import com.forma.app.data.catalog.MuscleGroup
import com.forma.app.data.catalog.SportSessionTemplates
import com.forma.app.domain.model.Exercise
import com.forma.app.domain.model.ExperienceLevel
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.RoutineSession
import com.forma.app.domain.model.Sport
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.model.WeeklyRoutine
import kotlin.random.Random

/**
 * Generador determinista de rutinas. La semilla sale del perfil, así que el mismo usuario
 * siempre ve la misma rutina hasta que cambia sus datos o su equipo.
 */
object RoutineGenerator {

    private data class GymDay(
        val day: WeekDay,
        val shortTitle: String,
        val title: String,
        val focus: String,
        val groups: List<String>,
        val exerciseCount: Int,
    )

    fun generate(profile: UserProfile): WeeklyRoutine {
        val sessions = if (profile.sport == Sport.GYM) {
            gymSessions(profile)
        } else {
            SportSessionTemplates.sessions(profile.sport, profile.level, profile.equipment)
        }
        return WeeklyRoutine(profile.sport, profile.level, sessions)
    }

    private fun seedOf(profile: UserProfile): Int =
        (profile.id.hashCode() * 31) xor
            profile.equipment.sorted().joinToString().hashCode() xor
            (profile.level.ordinal * 7) xor
            (profile.goal.ordinal * 13)

    private fun gymSessions(profile: UserProfile): List<RoutineSession> {
        val plan = gymPlan(profile.level, profile.goal)
        val seed = seedOf(profile)
        return plan.map { gymDay ->
            if (gymDay.groups.isEmpty()) {
                RoutineSession(
                    day = gymDay.day,
                    shortTitle = "Desc",
                    title = gymDay.title,
                    focus = gymDay.focus,
                    exercises = emptyList(),
                    estimatedMinutes = 0,
                    estimatedKcal = 0,
                    isRest = true,
                )
            } else {
                val picked = pickExercises(
                    groups = gymDay.groups,
                    count = gymDay.exerciseCount,
                    equipment = profile.equipment,
                    random = Random(seed + gymDay.day.index),
                )
                val exercises = picked.mapIndexed { index, catalog ->
                    toExercise(catalog, index, profile.level, profile.goal)
                }
                val minutes = exercises.sumOf { it.sets } * 3 + 10
                RoutineSession(
                    day = gymDay.day,
                    shortTitle = gymDay.shortTitle,
                    title = gymDay.title,
                    focus = gymDay.focus,
                    exercises = exercises,
                    estimatedMinutes = minutes,
                    estimatedKcal = (minutes * 8.5).toInt(),
                )
            }
        }
    }

    private fun gymPlan(level: ExperienceLevel, goal: Goal): List<GymDay> {
        val extraCardio = goal == Goal.LOSE_FAT || goal == Goal.ENDURANCE
        return when (level) {
            ExperienceLevel.BEGINNER -> listOf(
                GymDay(WeekDay.MONDAY, "Full", "Cuerpo completo A", "Patrones básicos", listOf(MuscleGroup.LEGS, MuscleGroup.CHEST, MuscleGroup.BACK), 5),
                GymDay(WeekDay.TUESDAY, "Card", "Cardio y core", "Base aeróbica", listOf(MuscleGroup.CARDIO, MuscleGroup.CORE), 4),
                GymDay(WeekDay.WEDNESDAY, "Sup", "Tren superior", "Empuje y tracción", listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.SHOULDERS), 5),
                GymDay(WeekDay.THURSDAY, "Desc", "Descanso activo", "Camina y estira", emptyList(), 0),
                GymDay(WeekDay.FRIDAY, "Inf", "Tren inferior", "Pierna completa", listOf(MuscleGroup.LEGS), 5),
                GymDay(WeekDay.SATURDAY, "Core", "Core y movilidad", "Estabilidad", listOf(MuscleGroup.CORE, if (extraCardio) MuscleGroup.CARDIO else MuscleGroup.ARMS), 4),
                GymDay(WeekDay.SUNDAY, "Desc", "Descanso", "Recuperación total", emptyList(), 0),
            )

            ExperienceLevel.INTERMEDIATE -> listOf(
                GymDay(WeekDay.MONDAY, "Pech", "Pecho", "Empuje horizontal", listOf(MuscleGroup.CHEST), 4),
                GymDay(WeekDay.TUESDAY, "Espa", "Espalda", "Tracción", listOf(MuscleGroup.BACK), 4),
                GymDay(WeekDay.WEDNESDAY, "Pier", "Pierna", "Cuádriceps y glúteo", listOf(MuscleGroup.LEGS), 5),
                GymDay(WeekDay.THURSDAY, "Homb", "Hombro", "Deltoides completo", listOf(MuscleGroup.SHOULDERS), 4),
                GymDay(WeekDay.FRIDAY, "Braz", "Brazo", "Bíceps y tríceps", listOf(MuscleGroup.ARMS), 5),
                GymDay(WeekDay.SATURDAY, "Full", "Full body y core", "Cuerpo completo", listOf(MuscleGroup.CORE, if (extraCardio) MuscleGroup.CARDIO else MuscleGroup.LEGS), 4),
                GymDay(WeekDay.SUNDAY, "Desc", "Descanso", "Recuperación total", emptyList(), 0),
            )

            ExperienceLevel.ADVANCED -> listOf(
                GymDay(WeekDay.MONDAY, "Pech", "Pecho y tríceps", "Empuje", listOf(MuscleGroup.CHEST, MuscleGroup.ARMS), 6),
                GymDay(WeekDay.TUESDAY, "Espa", "Espalda y bíceps", "Tracción", listOf(MuscleGroup.BACK, MuscleGroup.ARMS), 6),
                GymDay(WeekDay.WEDNESDAY, "Pier", "Pierna pesada", "Fuerza", listOf(MuscleGroup.LEGS), 6),
                GymDay(WeekDay.THURSDAY, "Homb", "Hombro y core", "Deltoides", listOf(MuscleGroup.SHOULDERS, MuscleGroup.CORE), 6),
                GymDay(WeekDay.FRIDAY, "Braz", "Brazo y antebrazo", "Aislamiento", listOf(MuscleGroup.ARMS), 6),
                GymDay(WeekDay.SATURDAY, "Full", "Volumen extra", "Puntos débiles", listOf(MuscleGroup.LEGS, MuscleGroup.BACK, if (extraCardio) MuscleGroup.CARDIO else MuscleGroup.CORE), 6),
                GymDay(WeekDay.SUNDAY, "Desc", "Descanso", "Recuperación total", emptyList(), 0),
            )
        }
    }

    private fun pickExercises(
        groups: List<String>,
        count: Int,
        equipment: Set<String>,
        random: Random,
    ): List<CatalogExercise> {
        val picked = mutableListOf<CatalogExercise>()
        val pools = groups.map { GymExerciseCatalog.available(it, equipment).shuffled(random) }
        var index = 0
        while (picked.size < count) {
            var addedThisRound = false
            for (pool in pools) {
                if (picked.size >= count) break
                val candidate = pool.getOrNull(index)
                if (candidate != null && picked.none { it.id == candidate.id }) {
                    picked += candidate
                    addedThisRound = true
                }
            }
            index++
            if (!addedThisRound) break
        }
        return picked
    }

    private fun toExercise(
        catalog: CatalogExercise,
        position: Int,
        level: ExperienceLevel,
        goal: Goal,
    ): Exercise {
        val isCompound = position == 0
        val sets = when (level) {
            ExperienceLevel.BEGINNER -> 3
            ExperienceLevel.INTERMEDIATE -> if (isCompound) 4 else 3
            ExperienceLevel.ADVANCED -> if (isCompound) 5 else 4
        }
        val reps = when {
            goal == Goal.LOSE_FAT || goal == Goal.ENDURANCE -> catalog.repsHigh
            isCompound && level != ExperienceLevel.BEGINNER -> catalog.repsLow
            level == ExperienceLevel.BEGINNER -> catalog.repsHigh
            else -> catalog.repsMid
        }
        val rest = when {
            goal == Goal.LOSE_FAT -> if (isCompound) 90 else 45
            isCompound -> 120
            level == ExperienceLevel.BEGINNER -> 60
            else -> 90
        }
        return Exercise(
            id = catalog.id,
            name = catalog.name,
            muscle = catalog.muscle,
            sets = sets,
            reps = reps,
            restSeconds = rest,
            tip = catalog.tip,
            equipmentId = catalog.requires.firstOrNull(),
        )
    }
}
