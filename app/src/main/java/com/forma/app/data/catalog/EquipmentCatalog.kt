package com.forma.app.data.catalog

import com.forma.app.domain.model.Sport

data class EquipmentItem(
    val id: String,
    val label: String,
    /** Grupos que este equipo habilita; se usa para filtrar los ejercicios generados. */
    val enables: Set<String> = emptySet(),
)

data class EquipmentGroup(
    val title: String,
    val items: List<EquipmentItem>,
)

/**
 * La pantalla de equipo del onboarding cambia por completo según el deporte elegido.
 * Cada deporte define sus propios grupos y, cuando aplica, una preselección razonable.
 */
object EquipmentCatalog {

    fun groupsFor(sport: Sport): List<EquipmentGroup> = when (sport) {
        Sport.GYM -> gym
        Sport.YOGA -> yoga
        Sport.RUNNING -> running
        Sport.PILATES -> pilates
        Sport.CYCLING -> cycling
        Sport.SWIMMING -> swimming
        Sport.CROSSFIT -> crossfit
        Sport.BOXING -> boxing
    }

    fun allIds(sport: Sport): Set<String> =
        groupsFor(sport).flatMap { group -> group.items.map { it.id } }.toSet()

    fun defaultSelection(sport: Sport): Set<String> = when (sport) {
        Sport.GYM -> setOf(
            "banca_plana", "banca_inclinada", "press_hombros", "jalon_pecho",
            "remo_polea", "extension_cuadriceps", "curl_femoral", "prensa_piernas",
            "cable_crossover", "poleas_altas", "poleas_bajas", "curl_biceps_maquina",
            "mancuernas", "barra_olimpica", "caminadora",
        )
        Sport.YOGA -> setOf("tapete", "bloques", "cinturon")
        Sport.RUNNING -> setOf("asfalto", "parque", "tenis_amortiguacion", "reloj_gps")
        Sport.PILATES -> setOf("mat", "pelota", "banda_circular")
        Sport.CYCLING -> setOf("ruta", "bici_ruta", "casco", "ciclocomputadora")
        Sport.SWIMMING -> setOf("alberca_25", "gafas", "pull_buoy")
        Sport.CROSSFIT -> setOf("barra_olimpica_cf", "kettlebell", "cajon", "cuerda_saltar")
        Sport.BOXING -> setOf("guantes", "vendas", "costal", "cuerda")
    }

    fun labelFor(sport: Sport, id: String): String =
        groupsFor(sport).flatMap { it.items }.firstOrNull { it.id == id }?.label ?: id

    private val gym = listOf(
        EquipmentGroup(
            "Máquinas de tren superior",
            listOf(
                EquipmentItem("banca_plana", "Banca plana", setOf("pecho")),
                EquipmentItem("banca_inclinada", "Banca inclinada", setOf("pecho")),
                EquipmentItem("press_hombros", "Press de hombros (máquina)", setOf("hombro")),
                EquipmentItem("jalon_pecho", "Jalón al pecho", setOf("espalda")),
                EquipmentItem("remo_polea", "Remo en polea baja", setOf("espalda")),
                EquipmentItem("pec_deck", "Pec deck (mariposa)", setOf("pecho")),
                EquipmentItem("curl_biceps_maquina", "Curl de bíceps (máquina)", setOf("brazo")),
                EquipmentItem("extension_triceps", "Extensión de tríceps (máquina)", setOf("brazo")),
                EquipmentItem("banco_scott", "Banco Scott", setOf("brazo")),
            ),
        ),
        EquipmentGroup(
            "Máquinas de tren inferior",
            listOf(
                EquipmentItem("extension_cuadriceps", "Extensiones de cuádriceps", setOf("pierna")),
                EquipmentItem("curl_femoral", "Curl femoral", setOf("pierna")),
                EquipmentItem("prensa_piernas", "Prensa de piernas", setOf("pierna")),
                EquipmentItem("aductores", "Aductores/Abductores", setOf("pierna")),
                EquipmentItem("maquina_gluteos", "Máquina de glúteos", setOf("pierna")),
                EquipmentItem("rack_sentadillas", "Rack de sentadillas", setOf("pierna")),
                EquipmentItem("hiperextension", "Hiperextensión", setOf("espalda", "pierna")),
            ),
        ),
        EquipmentGroup(
            "Poleas y peso libre",
            listOf(
                EquipmentItem("cable_crossover", "Cable crossover", setOf("pecho", "brazo")),
                EquipmentItem("smith_machine", "Smith machine", setOf("pierna", "pecho")),
                EquipmentItem("poleas_altas", "Poleas altas", setOf("brazo", "espalda")),
                EquipmentItem("poleas_bajas", "Poleas bajas", setOf("espalda", "brazo")),
                EquipmentItem("mancuernas", "Mancuernas", setOf("pecho", "hombro", "brazo", "pierna", "espalda")),
                EquipmentItem("barra_olimpica", "Barra olímpica y discos", setOf("pecho", "espalda", "pierna", "hombro")),
                EquipmentItem("barra_dominadas", "Barra de dominadas", setOf("espalda", "brazo")),
                EquipmentItem("paralelas", "Paralelas / fondos", setOf("pecho", "brazo")),
            ),
        ),
        EquipmentGroup(
            "Cardio",
            listOf(
                EquipmentItem("caminadora", "Caminadora", setOf("cardio")),
                EquipmentItem("eliptica", "Elíptica", setOf("cardio")),
                EquipmentItem("bicicleta_estatica", "Bicicleta estática", setOf("cardio")),
                EquipmentItem("remo_ergometro", "Remo ergómetro", setOf("cardio", "espalda")),
                EquipmentItem("escaladora", "Escaladora", setOf("cardio")),
            ),
        ),
    )

    private val yoga = listOf(
        EquipmentGroup(
            "Material",
            listOf(
                EquipmentItem("tapete", "Tapete (mat)"),
                EquipmentItem("bloques", "Bloques"),
                EquipmentItem("cinturon", "Cinturón / strap"),
                EquipmentItem("bolster", "Bolster"),
                EquipmentItem("manta", "Manta"),
                EquipmentItem("rueda", "Rueda de yoga"),
            ),
        ),
        EquipmentGroup(
            "Estilo que practicas",
            listOf(
                EquipmentItem("hatha", "Hatha"),
                EquipmentItem("vinyasa", "Vinyasa"),
                EquipmentItem("ashtanga", "Ashtanga"),
                EquipmentItem("yin", "Yin"),
                EquipmentItem("restaurativo", "Restaurativo"),
            ),
        ),
        EquipmentGroup(
            "Dónde practicas",
            listOf(
                EquipmentItem("casa_yoga", "En casa"),
                EquipmentItem("estudio_yoga", "En estudio"),
                EquipmentItem("aire_libre_yoga", "Al aire libre"),
            ),
        ),
    )

    private val running = listOf(
        EquipmentGroup(
            "Superficies disponibles",
            listOf(
                EquipmentItem("asfalto", "Asfalto / calle"),
                EquipmentItem("pista", "Pista de atletismo"),
                EquipmentItem("parque", "Parque / terracería"),
                EquipmentItem("trail", "Montaña (trail)"),
                EquipmentItem("caminadora_run", "Caminadora"),
                EquipmentItem("cuestas", "Cuestas / pendientes"),
            ),
        ),
        EquipmentGroup(
            "Equipo",
            listOf(
                EquipmentItem("tenis_amortiguacion", "Tenis de amortiguación"),
                EquipmentItem("tenis_ritmo", "Tenis de ritmo"),
                EquipmentItem("reloj_gps", "Reloj GPS"),
                EquipmentItem("banda_pulso", "Banda de pulso"),
                EquipmentItem("chaleco_hidratacion", "Chaleco de hidratación"),
            ),
        ),
        EquipmentGroup(
            "Volumen semanal actual",
            listOf(
                EquipmentItem("vol_menos_10", "Menos de 10 km"),
                EquipmentItem("vol_10_25", "10 a 25 km"),
                EquipmentItem("vol_25_50", "25 a 50 km"),
                EquipmentItem("vol_mas_50", "Más de 50 km"),
            ),
        ),
    )

    private val pilates = listOf(
        EquipmentGroup(
            "Equipo del estudio",
            listOf(
                EquipmentItem("mat", "Mat"),
                EquipmentItem("reformer", "Reformer"),
                EquipmentItem("cadillac", "Cadillac"),
                EquipmentItem("silla_pilates", "Silla (chair)"),
                EquipmentItem("barril", "Barril"),
            ),
        ),
        EquipmentGroup(
            "Accesorios",
            listOf(
                EquipmentItem("pelota", "Pelota"),
                EquipmentItem("banda_circular", "Banda circular"),
                EquipmentItem("aro_magico", "Aro mágico"),
                EquipmentItem("foam_roller", "Foam roller"),
                EquipmentItem("pesas_ligeras", "Pesas ligeras"),
            ),
        ),
    )

    private val cycling = listOf(
        EquipmentGroup(
            "Tu bici",
            listOf(
                EquipmentItem("bici_ruta", "Ruta"),
                EquipmentItem("bici_montana", "Montaña"),
                EquipmentItem("bici_gravel", "Gravel"),
                EquipmentItem("bici_urbana", "Urbana / híbrida"),
                EquipmentItem("rodillo", "Rodillo / smart trainer"),
            ),
        ),
        EquipmentGroup(
            "Terreno disponible",
            listOf(
                EquipmentItem("ruta", "Carretera"),
                EquipmentItem("ciclovia", "Ciclovía"),
                EquipmentItem("montana", "Montaña"),
                EquipmentItem("interior", "Interior (rodillo)"),
            ),
        ),
        EquipmentGroup(
            "Accesorios",
            listOf(
                EquipmentItem("casco", "Casco"),
                EquipmentItem("ciclocomputadora", "Ciclocomputadora"),
                EquipmentItem("potenciometro", "Potenciómetro"),
                EquipmentItem("zapatillas_cala", "Zapatillas con cala"),
            ),
        ),
    )

    private val swimming = listOf(
        EquipmentGroup(
            "Tu alberca",
            listOf(
                EquipmentItem("alberca_25", "Alberca de 25 m"),
                EquipmentItem("alberca_50", "Alberca de 50 m"),
                EquipmentItem("alberca_corta", "Alberca corta (menos de 25 m)"),
                EquipmentItem("aguas_abiertas", "Aguas abiertas"),
            ),
        ),
        EquipmentGroup(
            "Material",
            listOf(
                EquipmentItem("gafas", "Gafas"),
                EquipmentItem("pull_buoy", "Pull buoy"),
                EquipmentItem("tabla", "Tabla"),
                EquipmentItem("aletas", "Aletas"),
                EquipmentItem("paletas", "Paletas"),
                EquipmentItem("snorkel", "Snorkel frontal"),
            ),
        ),
        EquipmentGroup(
            "Estilos que dominas",
            listOf(
                EquipmentItem("crol", "Crol"),
                EquipmentItem("dorso", "Dorso"),
                EquipmentItem("pecho", "Pecho"),
                EquipmentItem("mariposa", "Mariposa"),
            ),
        ),
    )

    private val crossfit = listOf(
        EquipmentGroup(
            "Peso y barras",
            listOf(
                EquipmentItem("barra_olimpica_cf", "Barra olímpica"),
                EquipmentItem("kettlebell", "Kettlebells"),
                EquipmentItem("mancuernas_cf", "Mancuernas"),
                EquipmentItem("wall_ball", "Wall ball"),
                EquipmentItem("sandbag", "Sandbag"),
            ),
        ),
        EquipmentGroup(
            "Gimnásticos y cardio",
            listOf(
                EquipmentItem("cajon", "Cajón (box)"),
                EquipmentItem("anillas", "Anillas"),
                EquipmentItem("barra_dominadas_cf", "Barra de dominadas"),
                EquipmentItem("cuerda_saltar", "Cuerda de saltar"),
                EquipmentItem("remo_cf", "Remo ergómetro"),
                EquipmentItem("assault_bike", "Assault bike"),
                EquipmentItem("cuerda_trepar", "Cuerda para trepar"),
            ),
        ),
    )

    private val boxing = listOf(
        EquipmentGroup(
            "Equipo personal",
            listOf(
                EquipmentItem("guantes", "Guantes"),
                EquipmentItem("vendas", "Vendas"),
                EquipmentItem("protector_bucal", "Protector bucal"),
                EquipmentItem("careta", "Careta"),
            ),
        ),
        EquipmentGroup(
            "Material del gimnasio",
            listOf(
                EquipmentItem("costal", "Costal / saco"),
                EquipmentItem("pera", "Pera loca"),
                EquipmentItem("manoplas", "Manoplas (con pareja)"),
                EquipmentItem("ring", "Ring para sparring"),
                EquipmentItem("cuerda", "Cuerda de saltar"),
            ),
        ),
    )
}
