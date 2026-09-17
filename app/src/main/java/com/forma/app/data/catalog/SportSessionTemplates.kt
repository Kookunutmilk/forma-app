package com.forma.app.data.catalog

import com.forma.app.domain.model.Exercise
import com.forma.app.domain.model.ExperienceLevel
import com.forma.app.domain.model.RoutineSession
import com.forma.app.domain.model.Sport
import com.forma.app.domain.model.WeekDay

/**
 * Plantillas de sesión para los deportes que no son gym. El gym se genera con
 * [GymExerciseCatalog] filtrando por las máquinas del usuario; aquí la personalización viene
 * del nivel y de las respuestas de la pantalla de equipo.
 */
object SportSessionTemplates {

    fun sessions(
        sport: Sport,
        level: ExperienceLevel,
        equipment: Set<String>,
    ): List<RoutineSession> = when (sport) {
        Sport.RUNNING -> running(level, equipment)
        Sport.YOGA -> yoga(level, equipment)
        Sport.PILATES -> pilates(level, equipment)
        Sport.CYCLING -> cycling(level, equipment)
        Sport.SWIMMING -> swimming(level, equipment)
        Sport.CROSSFIT -> crossfit(level, equipment)
        Sport.BOXING -> boxing(level, equipment)
        Sport.GYM -> emptyList()
    }

    private fun drill(
        id: String,
        name: String,
        muscle: String,
        sets: Int,
        reps: String,
        rest: Int,
        tip: String,
    ) = Exercise(
        id = id,
        name = name,
        muscle = muscle,
        sets = sets,
        reps = reps,
        restSeconds = rest,
        tip = tip,
    )

    private fun rest(day: WeekDay, title: String, focus: String) = RoutineSession(
        day = day,
        shortTitle = "Desc",
        title = title,
        focus = focus,
        exercises = emptyList(),
        estimatedMinutes = 0,
        estimatedKcal = 0,
        isRest = true,
    )

    private fun volumeFactor(level: ExperienceLevel) = when (level) {
        ExperienceLevel.BEGINNER -> 0.7
        ExperienceLevel.INTERMEDIATE -> 1.0
        ExperienceLevel.ADVANCED -> 1.3
    }

    // ---------------------------------------------------------------- Running

    private fun running(level: ExperienceLevel, equipment: Set<String>): List<RoutineSession> {
        val treadmillOnly = equipment.contains("caminadora_run") &&
            equipment.none { it in setOf("asfalto", "pista", "parque", "trail", "cuestas") }
        val surface = when {
            treadmillOnly -> "en caminadora"
            equipment.contains("pista") -> "en pista"
            equipment.contains("parque") -> "en parque"
            else -> "en asfalto"
        }
        val km = { base: Double -> "%.0f km".format(base * volumeFactor(level)) }

        return listOf(
            RoutineSession(
                WeekDay.MONDAY, "Suav", "Rodaje suave", "Zona 2 · base aeróbica",
                listOf(
                    drill("run_mon_1", "Calentamiento caminando", "Movilidad", 1, "8 min", 0, "Sube el pulso poco a poco."),
                    drill("run_mon_2", "Rodaje continuo $surface", "Aeróbico", 1, km(6.0), 0, "Ritmo en el que puedas conversar."),
                    drill("run_mon_3", "Educativos de técnica", "Técnica", 3, "60 m", 45, "Skipping, talones al glúteo y zancada larga."),
                    drill("run_mon_4", "Estiramiento de isquios y gemelos", "Flexibilidad", 2, "40 s", 20, "Sin rebotes, respira profundo."),
                ),
                45, 420,
            ),
            RoutineSession(
                WeekDay.TUESDAY, "Seri", "Series cortas", "Velocidad · VO2 máx",
                listOf(
                    drill("run_tue_1", "Trote de activación", "Aeróbico", 1, "12 min", 0, "Termina con 4 progresiones de 60 m."),
                    drill("run_tue_2", "Series de 800 m a ritmo 10K", "Velocidad", if (level == ExperienceLevel.ADVANCED) 6 else 4, "800 m", 120, "Mismo tiempo en todas; si bajas, acorta la serie."),
                    drill("run_tue_3", "Recuperación trotando", "Aeróbico", 1, "8 min", 0, "Muy suave, deja que baje el pulso."),
                    drill("run_tue_4", "Core para corredores", "Core", 3, "40 s", 30, "Plancha, plancha lateral y dead bug."),
                ),
                55, 560,
            ),
            RoutineSession(
                WeekDay.WEDNESDAY, "Fuer", "Fuerza para correr", "Prevención de lesiones",
                listOf(
                    drill("run_wed_1", "Sentadilla búlgara", "Cuádriceps/Glúteo", 3, "10 por pierna", 75, "Rodilla estable, no la dejes caer hacia dentro."),
                    drill("run_wed_2", "Puente de glúteo a una pierna", "Glúteo", 3, "12 por lado", 60, "Cadera al mismo nivel todo el tiempo."),
                    drill("run_wed_3", "Elevación de talones", "Gemelo/Sóleo", 4, "15-20", 60, "Clave para el tendón de Aquiles."),
                    drill("run_wed_4", "Plancha con apoyo alterno", "Core", 3, "45 s", 45, "Cadera quieta al levantar la mano."),
                ),
                40, 300,
            ),
            RoutineSession(
                WeekDay.THURSDAY, "Tempo", "Rodaje a tempo", "Umbral",
                listOf(
                    drill("run_thu_1", "Calentamiento progresivo", "Aeróbico", 1, "10 min", 0, "Termina rodando cómodo."),
                    drill("run_thu_2", "Bloque a tempo", "Umbral", if (level == ExperienceLevel.BEGINNER) 2 else 3, "8 min", 90, "Cómodamente difícil: podrías decir frases cortas."),
                    drill("run_thu_3", "Vuelta a la calma", "Aeróbico", 1, "10 min", 0, "Suelta las piernas."),
                ),
                50, 500,
            ),
            RoutineSession(
                WeekDay.FRIDAY, "Móvil", "Movilidad y descarga", "Recuperación activa",
                listOf(
                    drill("run_fri_1", "Foam roller de cuádriceps y gemelos", "Fascia", 2, "60 s", 30, "Detente en los puntos sensibles."),
                    drill("run_fri_2", "Movilidad de tobillo contra la pared", "Tobillo", 3, "10 por lado", 30, "Rodilla pasa la punta sin despegar el talón."),
                    drill("run_fri_3", "Caminata suave", "Aeróbico", 1, "20 min", 0, "Mantén el pulso bajo."),
                ),
                30, 160,
            ),
            RoutineSession(
                WeekDay.SATURDAY, "Larga", "Tirada larga", "Resistencia",
                listOf(
                    drill("run_sat_1", "Rodaje largo $surface", "Aeróbico", 1, km(12.0), 0, "Ritmo constante, hidrata cada 20 min."),
                    drill("run_sat_2", "Últimos minutos progresivos", "Aeróbico", 1, "6 min", 0, "Acelera un poco el último tramo."),
                    drill("run_sat_3", "Estiramiento completo", "Flexibilidad", 1, "8 min", 0, "Cadena posterior y flexores de cadera."),
                ),
                75, 780,
            ),
            rest(WeekDay.SUNDAY, "Descanso total", "Duerme bien y come suficiente"),
        )
    }

    // ------------------------------------------------------------------- Yoga

    private fun yoga(level: ExperienceLevel, equipment: Set<String>): List<RoutineSession> {
        val hasBlocks = equipment.contains("bloques")
        val blockTip = if (hasBlocks) "Usa los bloques para acercar el suelo a ti." else "Apoya las manos en las espinillas si no llegas al suelo."
        return listOf(
            RoutineSession(
                WeekDay.MONDAY, "Flow", "Vinyasa energizante", "Cuerpo completo",
                listOf(
                    drill("yoga_mon_1", "Saludo al sol A", "Cuerpo completo", 5, "1 ronda", 30, "Una respiración por movimiento."),
                    drill("yoga_mon_2", "Guerrero I y II", "Piernas/Cadera", 3, "45 s por lado", 20, "Rodilla sobre el tobillo."),
                    drill("yoga_mon_3", "Perro boca abajo con pedaleo", "Cadena posterior", 3, "60 s", 20, blockTip),
                    drill("yoga_mon_4", "Savasana", "Relajación", 1, "5 min", 0, "Respiración natural, sin controlar."),
                ),
                40, 210,
            ),
            RoutineSession(
                WeekDay.TUESDAY, "Cad", "Movilidad de cadera", "Cadera y psoas",
                listOf(
                    drill("yoga_tue_1", "Paloma con apoyo", "Cadera", 2, "90 s por lado", 30, "Si molesta la rodilla, apoya la cadera."),
                    drill("yoga_tue_2", "Lunge bajo con rotación", "Psoas", 3, "45 s por lado", 20, "Mete el coxis para estirar el psoas."),
                    drill("yoga_tue_3", "Mariposa sentada", "Aductores", 2, "90 s", 30, "Deja que la gravedad haga el trabajo."),
                    drill("yoga_tue_4", "Piernas en la pared", "Recuperación", 1, "5 min", 0, "Perfecto después de entrenar piernas."),
                ),
                35, 150,
            ),
            RoutineSession(
                WeekDay.WEDNESDAY, "Fuer", "Fuerza y equilibrio", "Core y estabilidad",
                listOf(
                    drill("yoga_wed_1", "Plancha a chaturanga", "Core/Brazos", 4, "8 repeticiones", 45, "Codos pegados al costado."),
                    drill("yoga_wed_2", "Silla (utkatasana)", "Cuádriceps", 3, "45 s", 30, "Peso en los talones."),
                    drill("yoga_wed_3", "Árbol", "Equilibrio", 3, "45 s por lado", 20, "Fija la mirada en un punto."),
                    drill("yoga_wed_4", "Barco (navasana)", "Core", 3, "30 s", 30, "Pecho alto antes que piernas rectas."),
                ),
                40, 230,
            ),
            RoutineSession(
                WeekDay.THURSDAY, "Yin", "Yin restaurativo", "Tejido profundo",
                listOf(
                    drill("yoga_thu_1", "Cisne dormido", "Cadera", 1, "3 min por lado", 0, "Busca incomodidad, nunca dolor."),
                    drill("yoga_thu_2", "Oruga sentada", "Cadena posterior", 1, "3 min", 0, "Suelta el cuello por completo."),
                    drill("yoga_thu_3", "Torsión tumbada", "Columna", 1, "2 min por lado", 0, "Hombros pegados al suelo."),
                    drill("yoga_thu_4", "Respiración 4-7-8", "Sistema nervioso", 1, "8 ciclos", 0, "Baja el pulso antes de dormir."),
                ),
                35, 120,
            ),
            RoutineSession(
                WeekDay.FRIDAY, "Sol", "Saludos y core", "Cuerpo completo",
                listOf(
                    drill("yoga_fri_1", "Saludo al sol B", "Cuerpo completo", if (level == ExperienceLevel.ADVANCED) 8 else 5, "1 ronda", 30, "Mantén el ritmo de la respiración."),
                    drill("yoga_fri_2", "Plancha lateral con variación", "Oblicuos", 3, "40 s por lado", 30, "Cadera arriba, sin colapsar el hombro."),
                    drill("yoga_fri_3", "Puente con bloque", "Glúteo", 3, "45 s", 30, blockTip),
                ),
                35, 190,
            ),
            RoutineSession(
                WeekDay.SATURDAY, "Full", "Flow completo", "Práctica larga",
                listOf(
                    drill("yoga_sat_1", "Calentamiento articular", "Movilidad", 1, "8 min", 0, "De la cabeza a los tobillos."),
                    drill("yoga_sat_2", "Secuencia de pie encadenada", "Cuerpo completo", 4, "5 min", 60, "Encadena guerreros, triángulo y media luna."),
                    drill("yoga_sat_3", "Inversión asistida", "Hombros/Core", 3, "60 s", 60, "Pared como apoyo si aún trabajas el equilibrio."),
                    drill("yoga_sat_4", "Savasana largo", "Relajación", 1, "8 min", 0, "Cierra siempre la práctica."),
                ),
                60, 300,
            ),
            rest(WeekDay.SUNDAY, "Descanso consciente", "Camina, respira y suelta"),
        )
    }

    // ---------------------------------------------------------------- Pilates

    private fun pilates(level: ExperienceLevel, equipment: Set<String>): List<RoutineSession> {
        val hasReformer = equipment.contains("reformer")
        val label = if (hasReformer) "en reformer" else "en mat"
        val reps = if (level == ExperienceLevel.BEGINNER) "8-10" else "12-15"
        return listOf(
            RoutineSession(
                WeekDay.MONDAY, "Core", "Powerhouse", "Centro y respiración",
                listOf(
                    drill("pil_mon_1", "The hundred", "Core", 1, "100 tiempos", 60, "Respiración coordinada, lumbar pegada."),
                    drill("pil_mon_2", "Roll up", "Abdomen", 3, reps, 45, "Vértebra por vértebra, sin impulso."),
                    drill("pil_mon_3", "Single leg stretch", "Core profundo", 3, reps, 45, "Ombligo hacia dentro."),
                    drill("pil_mon_4", "Puente de hombros $label", "Glúteo", 3, reps, 45, "Despega la columna poco a poco."),
                ),
                40, 220,
            ),
            RoutineSession(
                WeekDay.TUESDAY, "Pier", "Piernas y glúteo", "Tren inferior",
                listOf(
                    drill("pil_tue_1", "Side kick series", "Glúteo medio", 3, "15 por lado", 45, "Cadera apilada, sin rotar."),
                    drill("pil_tue_2", "Clam con banda", "Glúteo medio", 3, "15 por lado", 45, "Pies juntos, rodilla arriba."),
                    drill("pil_tue_3", "Footwork $label", "Cuádriceps", 3, reps, 60, "Empuje parejo de ambos pies."),
                    drill("pil_tue_4", "Stretch de isquios con banda", "Flexibilidad", 2, "45 s por lado", 30, "Rodilla ligeramente flexionada."),
                ),
                40, 210,
            ),
            RoutineSession(
                WeekDay.WEDNESDAY, "Post", "Postura y espalda", "Cadena posterior",
                listOf(
                    drill("pil_wed_1", "Swan prep", "Extensores", 3, reps, 45, "Alarga el cuello antes de subir."),
                    drill("pil_wed_2", "Swimming", "Espalda", 3, "30 s", 45, "Movimiento pequeño y rápido."),
                    drill("pil_wed_3", "Cat-cow con control", "Columna", 3, "10", 30, "Sincroniza con la respiración."),
                    drill("pil_wed_4", "Mermaid", "Oblicuos", 2, "8 por lado", 30, "Estira el costado completo."),
                ),
                35, 180,
            ),
            RoutineSession(
                WeekDay.THURSDAY, "Est", "Estabilidad", "Control y equilibrio",
                listOf(
                    drill("pil_thu_1", "Plank con toque de hombro", "Core", 3, "12 por lado", 45, "Cadera sin girar."),
                    drill("pil_thu_2", "Teaser progresivo", "Core", 3, "6-8", 60, "Empieza con una pierna si hace falta."),
                    drill("pil_thu_3", "Leg pull front", "Core/Hombro", 3, "8 por pierna", 45, "Hombros sobre las muñecas."),
                ),
                35, 190,
            ),
            RoutineSession(
                WeekDay.FRIDAY, "Flex", "Flexibilidad", "Movilidad global",
                listOf(
                    drill("pil_fri_1", "Spine stretch forward", "Columna", 3, "8", 30, "Crece antes de bajar."),
                    drill("pil_fri_2", "Saw", "Oblicuos", 3, "8 por lado", 30, "Gira desde la cintura."),
                    drill("pil_fri_3", "Foam roller de espalda alta", "Fascia", 2, "60 s", 30, "Abre el pecho al final."),
                ),
                30, 140,
            ),
            RoutineSession(
                WeekDay.SATURDAY, "Full", "Clase completa", "Cuerpo entero",
                listOf(
                    drill("pil_sat_1", "Serie clásica de mat", "Cuerpo completo", 1, "20 min", 0, "Encadena sin pausas largas."),
                    drill("pil_sat_2", "Trabajo con accesorios", "Cuerpo completo", 3, reps, 45, "Pelota, aro o banda según lo que tengas."),
                    drill("pil_sat_3", "Cierre de movilidad", "Flexibilidad", 1, "8 min", 0, "Baja pulsaciones estirando."),
                ),
                55, 280,
            ),
            rest(WeekDay.SUNDAY, "Descanso", "Tu centro también necesita recuperarse"),
        )
    }

    // --------------------------------------------------------------- Ciclismo

    private fun cycling(level: ExperienceLevel, equipment: Set<String>): List<RoutineSession> {
        val indoor = equipment.contains("interior") || equipment.contains("rodillo")
        val place = if (indoor) "en rodillo" else "en carretera"
        val factor = volumeFactor(level)
        val km = { base: Double -> "%.0f km".format(base * factor) }
        return listOf(
            RoutineSession(
                WeekDay.MONDAY, "Base", "Rodada base", "Zona 2",
                listOf(
                    drill("cyc_mon_1", "Calentamiento progresivo", "Aeróbico", 1, "10 min", 0, "Cadencia 85-95 rpm."),
                    drill("cyc_mon_2", "Rodada continua $place", "Aeróbico", 1, km(30.0), 0, "Pulso en zona 2 todo el bloque."),
                    drill("cyc_mon_3", "Vuelta a la calma", "Aeróbico", 1, "10 min", 0, "Piñón suave."),
                ),
                70, 650,
            ),
            RoutineSession(
                WeekDay.TUESDAY, "Int", "Intervalos", "Potencia",
                listOf(
                    drill("cyc_tue_1", "Calentamiento con aceleraciones", "Aeróbico", 1, "15 min", 0, "3 aceleraciones de 30 s."),
                    drill("cyc_tue_2", "Bloques de 4 min fuertes", "VO2", if (level == ExperienceLevel.ADVANCED) 6 else 4, "4 min", 180, "Ritmo que puedas repetir en todos los bloques."),
                    drill("cyc_tue_3", "Enfriamiento", "Aeróbico", 1, "10 min", 0, "Suelta las piernas."),
                ),
                65, 700,
            ),
            RoutineSession(
                WeekDay.WEDNESDAY, "Fuer", "Fuerza en gimnasio", "Prevención",
                listOf(
                    drill("cyc_wed_1", "Sentadilla o prensa", "Cuádriceps", 4, "8-10", 120, "Base de fuerza para subir mejor."),
                    drill("cyc_wed_2", "Peso muerto rumano", "Isquios", 3, "10", 90, "Compensa el trabajo del cuádriceps."),
                    drill("cyc_wed_3", "Plancha y antirrotación", "Core", 3, "45 s", 45, "Core fuerte = espalda sin dolor."),
                    drill("cyc_wed_4", "Movilidad torácica", "Columna", 2, "10", 30, "Clave para la posición aerodinámica."),
                ),
                45, 320,
            ),
            RoutineSession(
                WeekDay.THURSDAY, "Cues", "Cuestas", "Fuerza-resistencia",
                listOf(
                    drill("cyc_thu_1", "Calentamiento", "Aeróbico", 1, "15 min", 0, "Llega caliente a la primera subida."),
                    drill("cyc_thu_2", "Repeticiones en subida", "Fuerza", 5, "3 min", 240, "Cadencia baja, 60-70 rpm sentado."),
                    drill("cyc_thu_3", "Regreso rodando", "Aeróbico", 1, "15 min", 0, "Muy suave."),
                ),
                70, 720,
            ),
            RoutineSession(
                WeekDay.FRIDAY, "Rec", "Recuperación", "Descarga",
                listOf(
                    drill("cyc_fri_1", "Rodada suave", "Aeróbico", 1, "35 min", 0, "Piernas ligeras, sin fuerza."),
                    drill("cyc_fri_2", "Estiramiento de cadena posterior", "Flexibilidad", 2, "45 s", 30, "Isquios, glúteo y gemelo."),
                ),
                40, 260,
            ),
            RoutineSession(
                WeekDay.SATURDAY, "Larga", "Salida larga", "Resistencia",
                listOf(
                    drill("cyc_sat_1", "Salida larga $place", "Aeróbico", 1, km(60.0), 0, "Come cada 45 min y bebe cada 15."),
                    drill("cyc_sat_2", "Bloque final a tempo", "Umbral", 1, "15 min", 0, "Cierra fuerte pero controlado."),
                ),
                140, 1400,
            ),
            rest(WeekDay.SUNDAY, "Descanso", "Limpia la bici y descansa"),
        )
    }

    // --------------------------------------------------------------- Natación

    private fun swimming(level: ExperienceLevel, equipment: Set<String>): List<RoutineSession> {
        val poolLength = when {
            equipment.contains("alberca_50") -> 50
            equipment.contains("alberca_corta") -> 20
            else -> 25
        }
        val laps = { meters: Int -> "${meters}m (${meters / poolLength} largos)" }
        val factor = volumeFactor(level)
        val series = { base: Int -> (base * factor).toInt().coerceAtLeast(2) }
        return listOf(
            RoutineSession(
                WeekDay.MONDAY, "Téc", "Técnica de crol", "Eficiencia",
                listOf(
                    drill("swim_mon_1", "Calentamiento suave", "Aeróbico", 1, laps(400), 0, "Alterna crol y dorso."),
                    drill("swim_mon_2", "Ejercicio de puño cerrado", "Técnica", series(6), laps(50), 30, "Sentirás el antebrazo como remo."),
                    drill("swim_mon_3", "Batido con tabla", "Piernas", series(4), laps(50), 30, "Patada desde la cadera."),
                    drill("swim_mon_4", "Vuelta a la calma", "Aeróbico", 1, laps(200), 0, "Respiración cada 3 brazadas."),
                ),
                50, 450,
            ),
            RoutineSession(
                WeekDay.TUESDAY, "Res", "Resistencia", "Aeróbico",
                listOf(
                    drill("swim_tue_1", "Calentamiento", "Aeróbico", 1, laps(300), 0, "Progresivo."),
                    drill("swim_tue_2", "Series largas de crol", "Aeróbico", series(5), laps(200), 45, "Mismo tiempo en todas."),
                    drill("swim_tue_3", "Pull con pull buoy", "Brazos", series(4), laps(100), 30, "Trabaja el agarre sin patada."),
                ),
                55, 520,
            ),
            RoutineSession(
                WeekDay.WEDNESDAY, "Seco", "Trabajo en seco", "Fuerza y hombro",
                listOf(
                    drill("swim_wed_1", "Rotación externa con banda", "Manguito", 3, "15 por lado", 45, "Prevención número uno del hombro."),
                    drill("swim_wed_2", "Remo con banda o polea", "Espalda", 4, "12", 60, "Junta las escápulas."),
                    drill("swim_wed_3", "Plancha y hollow hold", "Core", 3, "45 s", 45, "Core estable, menos arrastre."),
                    drill("swim_wed_4", "Movilidad de hombro", "Hombro", 2, "10", 30, "Círculos con palo o banda."),
                ),
                40, 280,
            ),
            RoutineSession(
                WeekDay.THURSDAY, "Vel", "Velocidad", "Umbral",
                listOf(
                    drill("swim_thu_1", "Calentamiento con progresivos", "Aeróbico", 1, laps(400), 0, "Últimos 100 m fuertes."),
                    drill("swim_thu_2", "Sprints", "Velocidad", series(8), laps(50), 60, "Máxima frecuencia, técnica intacta."),
                    drill("swim_thu_3", "Recuperación", "Aeróbico", 1, laps(200), 0, "Dorso muy suave."),
                ),
                45, 480,
            ),
            RoutineSession(
                WeekDay.FRIDAY, "Est", "Estilos", "Variedad",
                listOf(
                    drill("swim_fri_1", "Calentamiento por estilos", "Técnica", 1, laps(400), 0, "Crol, dorso, pecho."),
                    drill("swim_fri_2", "Series combinadas", "Técnica", series(6), laps(100), 45, "Cambia de estilo cada serie."),
                    drill("swim_fri_3", "Patada de pecho", "Piernas", series(4), laps(50), 30, "Talones al glúteo y empuje circular."),
                ),
                50, 460,
            ),
            RoutineSession(
                WeekDay.SATURDAY, "Fondo", "Fondo continuo", "Resistencia larga",
                listOf(
                    drill("swim_sat_1", "Nado continuo", "Aeróbico", 1, laps(1500), 0, "Sin parar; controla la respiración."),
                    drill("swim_sat_2", "Suelta", "Recuperación", 1, laps(200), 0, "Dorso y estiramiento en el agua."),
                ),
                60, 600,
            ),
            rest(WeekDay.SUNDAY, "Descanso", "Hidrátate y cuida el hombro"),
        )
    }

    // --------------------------------------------------------------- CrossFit

    private fun crossfit(level: ExperienceLevel, equipment: Set<String>): List<RoutineSession> {
        val hasRower = equipment.contains("remo_cf")
        val cardio = if (hasRower) "Remo" else "Cuerda de saltar"
        val rounds = when (level) {
            ExperienceLevel.BEGINNER -> 3
            ExperienceLevel.INTERMEDIATE -> 4
            ExperienceLevel.ADVANCED -> 5
        }
        return listOf(
            RoutineSession(
                WeekDay.MONDAY, "Fuer", "Fuerza · sentadilla", "Pierna y core",
                listOf(
                    drill("cf_mon_1", "Back squat pesado", "Pierna", 5, "5", 180, "Sube el peso serie a serie."),
                    drill("cf_mon_2", "Front rack lunge", "Pierna", 3, "10 por pierna", 120, "Torso vertical."),
                    drill("cf_mon_3", "MetCon: 10 wall ball + 10 box jump", "Cuerpo completo", rounds, "1 ronda", 60, "Ritmo constante, sin ir al fallo."),
                ),
                60, 650,
            ),
            RoutineSession(
                WeekDay.TUESDAY, "Gim", "Gimnásticos", "Tracción y core",
                listOf(
                    drill("cf_tue_1", "Dominadas estrictas", "Dorsal", 5, "5-8", 120, "Estricto antes que kipping."),
                    drill("cf_tue_2", "Fondos en anillas o paralelas", "Tríceps/Pecho", 4, "6-10", 120, "Hombros abajo y atrás."),
                    drill("cf_tue_3", "Hollow + arch hold", "Core", 4, "30 s", 60, "Base para el kipping."),
                    drill("cf_tue_4", "$cardio continuo", "Cardio", 1, "10 min", 0, "Zona 2 para cerrar."),
                ),
                55, 520,
            ),
            RoutineSession(
                WeekDay.WEDNESDAY, "Met", "MetCon", "Acondicionamiento",
                listOf(
                    drill("cf_wed_1", "Calentamiento específico", "Movilidad", 1, "10 min", 0, "Prepara cadera y hombro."),
                    drill("cf_wed_2", "AMRAP 15: 12 kettlebell swing + 9 burpees + 6 pull ups", "Cuerpo completo", 1, "15 min", 0, "Anota las rondas para comparar."),
                    drill("cf_wed_3", "Enfriamiento", "Recuperación", 1, "8 min", 0, "Camina y respira."),
                ),
                45, 600,
            ),
            RoutineSession(
                WeekDay.THURSDAY, "Halt", "Halterofilia", "Técnica olímpica",
                listOf(
                    drill("cf_thu_1", "Clean & jerk técnico", "Cuerpo completo", 6, "2", 180, "Peso ligero, velocidad alta."),
                    drill("cf_thu_2", "Snatch desde bloques", "Cuerpo completo", 5, "3", 180, "Barra pegada al cuerpo."),
                    drill("cf_thu_3", "Overhead squat", "Movilidad/Pierna", 4, "5", 120, "Si falla la movilidad, baja el peso."),
                ),
                60, 500,
            ),
            RoutineSession(
                WeekDay.FRIDAY, "Post", "Cadena posterior", "Peso muerto",
                listOf(
                    drill("cf_fri_1", "Peso muerto", "Isquios/Espalda", 5, "5", 180, "Espalda neutra, barra pegada."),
                    drill("cf_fri_2", "Hip thrust o puente con barra", "Glúteo", 4, "10", 90, "Pausa de un segundo arriba."),
                    drill("cf_fri_3", "Farmer carry", "Core/Agarre", 4, "40 m", 90, "Hombros atrás, paso corto."),
                ),
                55, 520,
            ),
            RoutineSession(
                WeekDay.SATURDAY, "Team", "WOD largo", "Resistencia",
                listOf(
                    drill("cf_sat_1", "Calentamiento completo", "Movilidad", 1, "12 min", 0, "Sin prisa, el WOD es largo."),
                    drill("cf_sat_2", "For time: 5 rondas de 400 m + 15 thrusters", "Cuerpo completo", 5, "1 ronda", 0, "Divide los thrusters desde la primera ronda."),
                    drill("cf_sat_3", "Movilidad de cierre", "Recuperación", 1, "10 min", 0, "Cadera, hombro y tobillo."),
                ),
                70, 800,
            ),
            rest(WeekDay.SUNDAY, "Descanso", "El progreso pasa cuando descansas"),
        )
    }

    // ------------------------------------------------------------------ Boxeo

    private fun boxing(level: ExperienceLevel, equipment: Set<String>): List<RoutineSession> {
        val hasBag = equipment.contains("costal")
        val bagWork = if (hasBag) "en costal" else "al aire (sombra)"
        val rounds = if (level == ExperienceLevel.BEGINNER) 4 else 6
        return listOf(
            RoutineSession(
                WeekDay.MONDAY, "Téc", "Técnica básica", "Guardia y jab",
                listOf(
                    drill("box_mon_1", "Cuerda de saltar", "Cardio", 3, "3 min", 60, "Pies ligeros, rodillas suaves."),
                    drill("box_mon_2", "Sombra: jab y directo", "Técnica", rounds, "3 min", 60, "Regresa siempre la mano a la guardia."),
                    drill("box_mon_3", "Trabajo $bagWork", "Potencia", rounds, "3 min", 60, "Gira la cadera en cada golpe."),
                    drill("box_mon_4", "Core y cuello", "Core", 3, "45 s", 45, "Plancha, crunch y puente."),
                ),
                50, 560,
            ),
            RoutineSession(
                WeekDay.TUESDAY, "Cond", "Acondicionamiento", "Motor aeróbico",
                listOf(
                    drill("box_tue_1", "Trote o bici", "Cardio", 1, "20 min", 0, "Base aeróbica para aguantar rounds."),
                    drill("box_tue_2", "Circuito de burpees y sprints", "Cardio", 5, "1 min", 60, "Máximo esfuerzo en cada bloque."),
                    drill("box_tue_3", "Movilidad de hombro", "Hombro", 2, "10", 30, "Prepara el hombro para los combinados."),
                ),
                45, 520,
            ),
            RoutineSession(
                WeekDay.WEDNESDAY, "Comb", "Combinaciones", "Coordinación",
                listOf(
                    drill("box_wed_1", "Sombra con combinaciones 1-2-3", "Técnica", rounds, "3 min", 60, "Velocidad antes que fuerza."),
                    drill("box_wed_2", "Trabajo $bagWork con ráfagas", "Potencia", rounds, "3 min", 60, "Últimos 15 s a máxima frecuencia."),
                    drill("box_wed_3", "Esquivas y roll", "Defensa", 4, "2 min", 45, "Mueve la cabeza fuera de la línea."),
                ),
                50, 600,
            ),
            RoutineSession(
                WeekDay.THURSDAY, "Fuer", "Fuerza explosiva", "Potencia",
                listOf(
                    drill("box_thu_1", "Press de banca o flexiones explosivas", "Pecho", 4, "6-8", 120, "Empuje rápido, bajada controlada."),
                    drill("box_thu_2", "Sentadilla con salto", "Pierna", 4, "8", 90, "Aterriza suave."),
                    drill("box_thu_3", "Remo", "Espalda", 4, "10", 90, "Compensa el trabajo de empuje."),
                    drill("box_thu_4", "Antirrotación con banda", "Core", 3, "12 por lado", 60, "El golpe sale del core."),
                ),
                50, 450,
            ),
            RoutineSession(
                WeekDay.FRIDAY, "Spar", "Sparring o manoplas", "Aplicación",
                listOf(
                    drill("box_fri_1", "Calentamiento completo", "Movilidad", 1, "12 min", 0, "Cuello, hombro y cadera."),
                    drill("box_fri_2", "Rounds de manoplas o sparring ligero", "Técnica", rounds, "3 min", 60, "Prioriza defensa y distancia."),
                    drill("box_fri_3", "Sombra de enfriamiento", "Técnica", 2, "3 min", 45, "Repasa lo que falló."),
                ),
                55, 620,
            ),
            RoutineSession(
                WeekDay.SATURDAY, "Res", "Resistencia de rounds", "Aguante",
                listOf(
                    drill("box_sat_1", "Cuerda continua", "Cardio", 5, "3 min", 45, "Sin bajar el ritmo."),
                    drill("box_sat_2", "Trabajo $bagWork largo", "Potencia", 8, "2 min", 30, "Descansos cortos a propósito."),
                    drill("box_sat_3", "Abdomen completo", "Core", 4, "45 s", 30, "Termina siempre por el core."),
                ),
                55, 680,
            ),
            rest(WeekDay.SUNDAY, "Descanso", "Manos y hombros lo agradecen"),
        )
    }
}
