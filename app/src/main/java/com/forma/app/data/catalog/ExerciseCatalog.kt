package com.forma.app.data.catalog

data class CatalogExercise(
    val id: String,
    val name: String,
    val muscle: String,
    val tip: String,
    /** Todo el equipo listado debe estar disponible para proponer el ejercicio. */
    val requires: List<String> = emptyList(),
    val repsLow: String = "6-8",
    val repsMid: String = "8-12",
    val repsHigh: String = "12-15",
)

/** Grupos musculares que usa el generador de rutinas de gym. */
object MuscleGroup {
    const val CHEST = "pecho"
    const val BACK = "espalda"
    const val LEGS = "pierna"
    const val SHOULDERS = "hombro"
    const val ARMS = "brazo"
    const val CORE = "core"
    const val CARDIO = "cardio"
}

object GymExerciseCatalog {

    val byGroup: Map<String, List<CatalogExercise>> = mapOf(
        MuscleGroup.CHEST to listOf(
            CatalogExercise(
                "chest_press_banca", "Press banca plana barra", "Pecho",
                "Arco lumbar neutro, escápulas retraídas.",
                listOf("banca_plana", "barra_olimpica"),
            ),
            CatalogExercise(
                "chest_press_inclinado", "Press inclinado mancuernas", "Pecho superior",
                "Baja hasta estirar el pecho completamente.",
                listOf("banca_inclinada", "mancuernas"),
            ),
            CatalogExercise(
                "chest_crossover", "Cable crossover bajo", "Pecho inferior",
                "Cruza las manos al frente.",
                listOf("cable_crossover"),
            ),
            CatalogExercise(
                "chest_pec_deck", "Pec deck (mariposa)", "Pecho",
                "Codos a la altura de los hombros, sin encogerlos.",
                listOf("pec_deck"),
            ),
            CatalogExercise(
                "chest_fondos", "Fondos en paralelas", "Pecho/Tríceps",
                "Inclínate hacia adelante para pecho.",
                listOf("paralelas"),
            ),
            CatalogExercise(
                "chest_press_mancuernas", "Press banca con mancuernas", "Pecho",
                "Controla la bajada tres segundos.",
                listOf("banca_plana", "mancuernas"),
            ),
            CatalogExercise(
                "chest_smith", "Press en Smith machine", "Pecho",
                "Ideal si entrenas sin compañero.",
                listOf("smith_machine"),
            ),
            CatalogExercise(
                "chest_flexiones", "Flexiones con pausa", "Pecho",
                "Cuerpo en línea recta, pausa de un segundo abajo.",
                repsMid = "12-20",
            ),
            CatalogExercise(
                "chest_flexiones_declinadas", "Flexiones declinadas", "Pecho superior",
                "Pies elevados en banco o silla.",
                repsMid = "10-15",
            ),
        ),
        MuscleGroup.BACK to listOf(
            CatalogExercise(
                "back_jalon", "Jalón al pecho", "Dorsal",
                "Lleva los codos al bolsillo, no a los lados.",
                listOf("jalon_pecho"),
            ),
            CatalogExercise(
                "back_remo_polea", "Remo en polea baja", "Espalda media",
                "Pecho arriba y tira con los codos.",
                listOf("remo_polea"),
            ),
            CatalogExercise(
                "back_dominadas", "Dominadas", "Dorsal",
                "Si te cuesta, usa banda o asistencia.",
                listOf("barra_dominadas"),
                repsMid = "6-10",
            ),
            CatalogExercise(
                "back_remo_barra", "Remo con barra", "Espalda media",
                "Tronco a 45°, sin balanceo.",
                listOf("barra_olimpica"),
            ),
            CatalogExercise(
                "back_remo_mancuerna", "Remo a una mano con mancuerna", "Dorsal",
                "Apoya la rodilla y mantén la cadera cuadrada.",
                listOf("mancuernas"),
            ),
            CatalogExercise(
                "back_pullover", "Pullover en polea alta", "Dorsal",
                "Brazos casi rectos, aprieta el dorsal al final.",
                listOf("poleas_altas"),
            ),
            CatalogExercise(
                "back_hiperextension", "Hiperextensiones", "Lumbar",
                "Sube solo hasta la línea del cuerpo.",
                listOf("hiperextension"),
                repsMid = "12-15",
            ),
            CatalogExercise(
                "back_remo_ergo", "Remo ergómetro", "Espalda/Cardio",
                "Secuencia: piernas, cadera, brazos.",
                listOf("remo_ergometro"),
                repsMid = "500 m",
            ),
            CatalogExercise(
                "back_superman", "Superman en el suelo", "Lumbar",
                "Aprieta glúteos y mantén dos segundos arriba.",
                repsMid = "12-15",
            ),
        ),
        MuscleGroup.LEGS to listOf(
            CatalogExercise(
                "legs_sentadilla", "Sentadilla libre con barra", "Cuádriceps",
                "Rodillas en línea con los pies, baja controlando.",
                listOf("rack_sentadillas", "barra_olimpica"),
            ),
            CatalogExercise(
                "legs_prensa", "Prensa de piernas", "Cuádriceps/Glúteo",
                "No bloquees las rodillas arriba.",
                listOf("prensa_piernas"),
            ),
            CatalogExercise(
                "legs_extension", "Extensiones de cuádriceps", "Cuádriceps",
                "Aprieta arriba un segundo.",
                listOf("extension_cuadriceps"),
                repsMid = "12-15",
            ),
            CatalogExercise(
                "legs_femoral", "Curl femoral tumbado", "Isquios",
                "Cadera pegada al banco todo el rango.",
                listOf("curl_femoral"),
                repsMid = "10-15",
            ),
            CatalogExercise(
                "legs_peso_muerto", "Peso muerto rumano", "Isquios/Glúteo",
                "Lleva la cadera atrás, espalda neutra.",
                listOf("barra_olimpica"),
            ),
            CatalogExercise(
                "legs_zancadas", "Zancadas con mancuernas", "Glúteo/Cuádriceps",
                "Paso largo para glúteo, corto para cuádriceps.",
                listOf("mancuernas"),
                repsMid = "10-12 por pierna",
            ),
            CatalogExercise(
                "legs_hip_thrust", "Hip thrust en máquina", "Glúteo",
                "Barbilla al pecho y aprieta arriba.",
                listOf("maquina_gluteos"),
                repsMid = "10-12",
            ),
            CatalogExercise(
                "legs_aductores", "Aductores / abductores", "Aductores",
                "Movimiento lento, sin rebote.",
                listOf("aductores"),
                repsMid = "15-20",
            ),
            CatalogExercise(
                "legs_bulgara", "Sentadilla búlgara", "Cuádriceps/Glúteo",
                "Pie trasero elevado, torso ligeramente adelante.",
                listOf("mancuernas"),
                repsMid = "8-12 por pierna",
            ),
            CatalogExercise(
                "legs_gemelos", "Elevación de talones de pie", "Gemelos",
                "Rango completo y pausa arriba.",
                repsMid = "15-20",
            ),
            CatalogExercise(
                "legs_sentadilla_peso_corporal", "Sentadilla goblet o libre", "Cuádriceps",
                "Talones apoyados y pecho alto.",
                repsMid = "15-20",
            ),
        ),
        MuscleGroup.SHOULDERS to listOf(
            CatalogExercise(
                "sh_press_maquina", "Press de hombros en máquina", "Deltoide frontal",
                "No arquees la espalda al empujar.",
                listOf("press_hombros"),
            ),
            CatalogExercise(
                "sh_press_militar", "Press militar con barra", "Deltoide frontal",
                "Glúteos y abdomen apretados.",
                listOf("barra_olimpica"),
            ),
            CatalogExercise(
                "sh_laterales", "Elevaciones laterales", "Deltoide lateral",
                "Sube hasta la línea del hombro, sin impulso.",
                listOf("mancuernas"),
                repsMid = "12-15",
            ),
            CatalogExercise(
                "sh_pajaros", "Pájaros (deltoide posterior)", "Deltoide posterior",
                "Pulgares abajo y codos ligeramente flexionados.",
                listOf("mancuernas"),
                repsMid = "12-15",
            ),
            CatalogExercise(
                "sh_face_pull", "Face pull en polea alta", "Deltoide posterior",
                "Lleva la cuerda a la frente, codos altos.",
                listOf("poleas_altas"),
                repsMid = "15-20",
            ),
            CatalogExercise(
                "sh_arnold", "Press Arnold", "Deltoide completo",
                "Gira las muñecas durante todo el recorrido.",
                listOf("mancuernas"),
            ),
            CatalogExercise(
                "sh_encogimientos", "Encogimientos de trapecio", "Trapecio",
                "Sube recto, no gires los hombros.",
                listOf("mancuernas"),
                repsMid = "12-15",
            ),
            CatalogExercise(
                "sh_pike", "Flexiones pike", "Deltoide frontal",
                "Cadera alta, la cabeza baja entre las manos.",
                repsMid = "8-12",
            ),
        ),
        MuscleGroup.ARMS to listOf(
            CatalogExercise(
                "arm_curl_barra", "Curl con barra", "Bíceps",
                "Codos pegados al cuerpo todo el rango.",
                listOf("barra_olimpica"),
            ),
            CatalogExercise(
                "arm_curl_mancuernas", "Curl alterno con mancuernas", "Bíceps",
                "Supina la muñeca al subir.",
                listOf("mancuernas"),
                repsMid = "10-12 por brazo",
            ),
            CatalogExercise(
                "arm_scott", "Curl en banco Scott", "Bíceps",
                "No despegues los codos del soporte.",
                listOf("banco_scott"),
                repsMid = "10-12",
            ),
            CatalogExercise(
                "arm_martillo", "Curl martillo", "Braquial",
                "Muñeca neutra y sin balanceo.",
                listOf("mancuernas"),
                repsMid = "10-12",
            ),
            CatalogExercise(
                "arm_triceps_polea", "Extensión de tríceps en polea", "Tríceps",
                "Codos fijos, solo se mueve el antebrazo.",
                listOf("poleas_altas"),
                repsMid = "12-15",
            ),
            CatalogExercise(
                "arm_press_frances", "Press francés", "Tríceps",
                "Baja hacia la frente controlando.",
                listOf("barra_olimpica"),
            ),
            CatalogExercise(
                "arm_triceps_maquina", "Extensión de tríceps en máquina", "Tríceps",
                "Aprieta dos segundos al final.",
                listOf("extension_triceps"),
                repsMid = "12-15",
            ),
            CatalogExercise(
                "arm_curl_maquina", "Curl de bíceps en máquina", "Bíceps",
                "Ideal para cerrar el entrenamiento al fallo.",
                listOf("curl_biceps_maquina"),
                repsMid = "12-15",
            ),
            CatalogExercise(
                "arm_fondos_banca", "Fondos en banca", "Tríceps",
                "Codos hacia atrás, no hacia afuera.",
                repsMid = "12-15",
            ),
        ),
        MuscleGroup.CORE to listOf(
            CatalogExercise(
                "core_plancha", "Plancha frontal", "Core",
                "Glúteo apretado, cadera sin caer.",
                repsMid = "45-60 s",
            ),
            CatalogExercise(
                "core_hollow", "Hollow hold", "Core profundo",
                "Lumbar pegada al suelo.",
                repsMid = "30-40 s",
            ),
            CatalogExercise(
                "core_dead_bug", "Dead bug", "Core profundo",
                "Exhala al estirar la pierna.",
                repsMid = "10-12 por lado",
            ),
            CatalogExercise(
                "core_piernas_colgado", "Elevación de piernas colgado", "Abdomen bajo",
                "Sin balanceo, sube con el abdomen.",
                listOf("barra_dominadas"),
                repsMid = "10-15",
            ),
            CatalogExercise(
                "core_crunch_polea", "Crunch en polea alta", "Abdomen",
                "Redondea la espalda alta, no la cadera.",
                listOf("poleas_altas"),
                repsMid = "15-20",
            ),
            CatalogExercise(
                "core_russian", "Russian twist", "Oblicuos",
                "Gira desde el tronco, no desde los brazos.",
                repsMid = "20 por lado",
            ),
            CatalogExercise(
                "core_plancha_lateral", "Plancha lateral", "Oblicuos",
                "Cadera alta y hombro estable.",
                repsMid = "30-40 s por lado",
            ),
        ),
        MuscleGroup.CARDIO to listOf(
            CatalogExercise(
                "cardio_caminadora", "Intervalos en caminadora", "Cardio",
                "1 min fuerte / 2 min suave.",
                listOf("caminadora"),
                repsMid = "12 min",
            ),
            CatalogExercise(
                "cardio_eliptica", "Elíptica continua", "Cardio",
                "Mantén el pulso en zona 2.",
                listOf("eliptica"),
                repsMid = "20 min",
            ),
            CatalogExercise(
                "cardio_bici", "Bicicleta estática", "Cardio",
                "Cadencia alta con resistencia media.",
                listOf("bicicleta_estatica"),
                repsMid = "20 min",
            ),
            CatalogExercise(
                "cardio_escaladora", "Escaladora", "Cardio",
                "No te recargues en el barandal.",
                listOf("escaladora"),
                repsMid = "15 min",
            ),
            CatalogExercise(
                "cardio_cuerda", "Cuerda de saltar", "Cardio",
                "Series de 1 minuto con 30 s de pausa.",
                repsMid = "10 min",
            ),
            CatalogExercise(
                "cardio_caminata", "Caminata inclinada o al aire libre", "Cardio",
                "Ritmo sostenido que te deje hablar.",
                repsMid = "25 min",
            ),
        ),
    )

    fun available(group: String, equipment: Set<String>): List<CatalogExercise> {
        val pool = byGroup[group].orEmpty()
        val usable = pool.filter { exercise -> equipment.containsAll(exercise.requires) }
        // Siempre queda al menos la variante sin equipo para que ninguna sesión salga vacía.
        return usable.ifEmpty { pool.filter { it.requires.isEmpty() } }
    }
}
