package com.forma.app.domain.model

import kotlin.math.roundToInt

enum class Sport(
    val id: String,
    val displayName: String,
    val emoji: String,
    /** Título de la pantalla de equipo, que cambia según el deporte. */
    val equipmentTitle: String,
    val equipmentSubtitle: String,
) {
    GYM("gym", "Gym", "\uD83C\uDFCB\uFE0F", "Tu gimnasio", "Selecciona las máquinas disponibles en tu gym"),
    YOGA("yoga", "Yoga", "\uD83E\uDDD8", "Tu material", "¿Con qué cuentas para practicar?"),
    RUNNING("running", "Running", "\uD83C\uDFC3", "Tus superficies", "¿Dónde y cómo sueles correr?"),
    PILATES("pilates", "Pilates", "\uD83E\uDD38", "Tu estudio", "Marca el equipo al que tienes acceso"),
    CYCLING("cycling", "Ciclismo", "\uD83D\uDEB4", "Tu bici y terreno", "¿Qué usas para rodar?"),
    SWIMMING("swimming", "Natación", "\uD83C\uDFCA", "Tu alberca", "Cuéntanos dónde nadas"),
    CROSSFIT("crossfit", "CrossFit", "\u26A1", "Tu box", "Selecciona el equipo disponible"),
    BOXING("boxing", "Boxeo", "\uD83E\uDD4A", "Tu equipo", "Marca lo que tienes para entrenar");

    companion object {
        fun fromId(id: String?): Sport = entries.firstOrNull { it.id == id } ?: GYM
    }
}

enum class ExperienceLevel(val id: String, val displayName: String, val description: String) {
    BEGINNER("beginner", "Principiante", "Menos de 6 meses entrenando"),
    INTERMEDIATE("intermediate", "Intermedio", "Entre 6 meses y 2 años"),
    ADVANCED("advanced", "Avanzado", "Más de 2 años con constancia");

    companion object {
        fun fromId(id: String?): ExperienceLevel =
            entries.firstOrNull { it.id == id } ?: INTERMEDIATE
    }
}

enum class Goal(val id: String, val displayName: String, val emoji: String, val calorieShift: Int) {
    LOSE_FAT("lose_fat", "Bajar grasa", "\uD83D\uDD25", -350),
    GAIN_MUSCLE("gain_muscle", "Ganar músculo", "\uD83D\uDCAA", 300),
    MAINTAIN("maintain", "Mantenerme", "\u2696\uFE0F", 0),
    ENDURANCE("endurance", "Más resistencia", "\uD83D\uDE80", 150);

    companion object {
        fun fromId(id: String?): Goal = entries.firstOrNull { it.id == id } ?: MAINTAIN
    }
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val photoUri: String? = null,
    val age: Int = 21,
    val weightKg: Int = 65,
    val heightCm: Int = 170,
    val sport: Sport = Sport.GYM,
    val level: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    val goal: Goal = Goal.GAIN_MUSCLE,
    val equipment: Set<String> = emptySet(),
    val likedIngredients: Set<String> = emptySet(),
    val onboardingCompleted: Boolean = false,
) {
    val bmi: Double
        get() {
            val meters = heightCm / 100.0
            if (meters <= 0) return 0.0
            return ((weightKg / (meters * meters)) * 10).roundToInt() / 10.0
        }

    val bmiLabel: String
        get() = when {
            bmi < 18.5 -> "Bajo"
            bmi < 25 -> "Normal"
            bmi < 30 -> "Alto"
            else -> "Muy alto"
        }

    val firstName: String
        get() = name.trim().split(" ").firstOrNull().orEmpty().ifBlank { "atleta" }

    /** Mifflin–St Jeor con factor de actividad medio, ajustado por el objetivo. */
    val dailyCalories: Int
        get() {
            val base = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
            val withActivity = base * when (level) {
                ExperienceLevel.BEGINNER -> 1.375
                ExperienceLevel.INTERMEDIATE -> 1.55
                ExperienceLevel.ADVANCED -> 1.725
            }
            return (((withActivity + goal.calorieShift) / 10).roundToInt() * 10)
        }

    val proteinTargetG: Int
        get() = when (goal) {
            Goal.GAIN_MUSCLE -> (weightKg * 2.0).roundToInt()
            Goal.LOSE_FAT -> (weightKg * 2.2).roundToInt()
            else -> (weightKg * 1.7).roundToInt()
        }
}
