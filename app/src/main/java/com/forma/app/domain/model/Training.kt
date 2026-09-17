package com.forma.app.domain.model

enum class WeekDay(val index: Int, val short: String, val letter: String, val full: String) {
    MONDAY(0, "Lun", "L", "Lunes"),
    TUESDAY(1, "Mar", "M", "Martes"),
    WEDNESDAY(2, "Mié", "X", "Miércoles"),
    THURSDAY(3, "Jue", "J", "Jueves"),
    FRIDAY(4, "Vie", "V", "Viernes"),
    SATURDAY(5, "Sáb", "S", "Sábado"),
    SUNDAY(6, "Dom", "D", "Domingo");

    companion object {
        fun fromIndex(index: Int): WeekDay = entries[((index % 7) + 7) % 7]
    }
}

data class Exercise(
    val id: String,
    val name: String,
    val muscle: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val tip: String,
    val equipmentId: String? = null,
)

data class RoutineSession(
    val day: WeekDay,
    /** Etiqueta corta que se muestra en la pastilla del día, p. ej. "Pech". */
    val shortTitle: String,
    val title: String,
    val focus: String,
    val exercises: List<Exercise>,
    val estimatedMinutes: Int,
    val estimatedKcal: Int,
    val isRest: Boolean = false,
) {
    val exerciseCount: Int get() = exercises.size
}

data class WeeklyRoutine(
    val sport: Sport,
    val level: ExperienceLevel,
    val sessions: List<RoutineSession>,
) {
    fun sessionFor(day: WeekDay): RoutineSession? = sessions.firstOrNull { it.day == day }
}

data class ExerciseProgress(
    val exerciseId: String,
    val day: WeekDay,
    val completed: Boolean,
)

data class WorkoutSession(
    val id: String,
    val dateEpochMillis: Long,
    val sportId: String,
    val title: String,
    val durationMinutes: Int,
    val kcal: Int,
    val photoUri: String? = null,
    val note: String = "",
)

data class TrainingStats(
    val workoutsThisWeek: Int,
    val streakDays: Int,
    val kcalThisWeek: Int,
    val completedDays: Set<WeekDay>,
)
