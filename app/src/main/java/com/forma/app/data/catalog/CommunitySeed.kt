package com.forma.app.data.catalog

import com.forma.app.data.local.entity.AuthorEntity
import com.forma.app.data.local.entity.PostEntity
import com.forma.app.domain.model.Sport

/** Contenido inicial del feed para que la comunidad nunca se vea vacía al instalar la app. */
object CommunitySeed {

    val authors = listOf(
        AuthorEntity("a_maria", "María G.", "@maria.flow", null, Sport.YOGA.id, false, false),
        AuthorEntity("a_carlos", "Carlos R.", "@carlos.lifts", null, Sport.GYM.id, false, true),
        AuthorEntity("a_ana", "Ana Ruiz", "@ana.corre", null, Sport.RUNNING.id, false, false),
        AuthorEntity("a_dani", "Daniela P.", "@dani.box", null, Sport.BOXING.id, false, false),
        AuthorEntity("a_luis", "Luis Mendoza", "@luis.wod", null, Sport.CROSSFIT.id, false, true),
        AuthorEntity("a_sofia", "Sofía Cruz", "@sofi.nada", null, Sport.SWIMMING.id, false, false),
        AuthorEntity("a_migue", "Miguel Á.", "@migue.ruta", null, Sport.CYCLING.id, false, false),
        AuthorEntity("a_rena", "Renata V.", "@rena.pilates", null, Sport.PILATES.id, false, false),
    )

    private const val HOUR = 3_600_000L

    fun posts(now: Long) = listOf(
        PostEntity(
            id = "p_1",
            authorId = "a_maria",
            imageKey = "feed_yoga",
            imageUri = null,
            caption = "¡Primera vez en crow pose! Meses de trabajo pero lo conseguí \uD83E\uDDD8 #yoga #progreso",
            sportId = Sport.YOGA.id,
            createdAtMillis = now - (HOUR / 5),
            likes = 48,
            likedByMe = false,
            reactionsJson = """{"fire":12,"strong":8,"clap":15}""",
            myReaction = null,
        ),
        PostEntity(
            "p_2", "a_carlos", "feed_gym", null,
            "Día de pierna completo. 4 series de sentadilla a 120 kg, por fin sin dolor de rodilla \uD83D\uDCAA",
            Sport.GYM.id, now - (HOUR / 2), 96, true,
            """{"fire":34,"strong":41,"rocket":9}""", "strong",
        ),
        PostEntity(
            "p_3", "a_ana", "feed_running", null,
            "21 km en 1:52. Medio maratón listo para dentro de tres semanas \uD83C\uDFC3‍♀️ #running",
            Sport.RUNNING.id, now - (2 * HOUR), 132, false,
            """{"fire":52,"clap":38,"rocket":22}""", null,
        ),
        PostEntity(
            "p_4", "a_dani", "feed_boxing", null,
            "Seis rounds de manoplas y todavía respiro. El acondicionamiento está pagando \uD83E\uDD4A",
            Sport.BOXING.id, now - (4 * HOUR), 71, false,
            """{"fire":29,"strong":18}""", null,
        ),
        PostEntity(
            "p_5", "a_luis", "feed_crossfit", null,
            "WOD de hoy: 5 rondas de 400 m + 15 thrusters. 18:42 y las piernas temblando.",
            Sport.CROSSFIT.id, now - (7 * HOUR), 58, false,
            """{"fire":21,"strong":14,"rocket":7}""", null,
        ),
        PostEntity(
            "p_6", "a_sofia", "feed_swimming", null,
            "2 km continuos sin parar. Hace un año no hacía ni 400 m \uD83C\uDFCA‍♀️",
            Sport.SWIMMING.id, now - (11 * HOUR), 88, true,
            """{"clap":33,"fire":19,"heart":12}""", "clap",
        ),
        PostEntity(
            "p_7", "a_migue", "feed_cycling", null,
            "80 km de ruta con 1200 m de desnivel. Amanecer que valió cada pedalada \uD83D\uDEB4",
            Sport.CYCLING.id, now - (26 * HOUR), 103, false,
            """{"fire":41,"heart":27,"rocket":11}""", null,
        ),
        PostEntity(
            "p_8", "a_rena", "feed_pilates", null,
            "Teaser completo por primera vez. El core no se entrena en un mes pero sí en un año.",
            Sport.PILATES.id, now - (30 * HOUR), 64, false,
            """{"clap":25,"strong":16,"heart":9}""", null,
        ),
    )
}
