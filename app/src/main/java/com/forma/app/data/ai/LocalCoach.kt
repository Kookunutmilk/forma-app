package com.forma.app.data.ai

import com.forma.app.data.catalog.ArticleCatalog
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.UserProfile
import kotlin.math.roundToInt

/**
 * Asistente local: reconoce la intención de la pregunta y responde usando los datos reales del
 * perfil (peso, deporte, nivel, objetivo, equipo). Es determinista y funciona sin API key.
 */
object LocalCoach {

    val suggestedQuestions = listOf(
        "¿Cuántas proteínas necesito al día?",
        "¿Cómo mejorar mi técnica en la sentadilla?",
        "¿Qué como antes de entrenar?",
        "¿Con qué frecuencia descansar?",
        "¿Cómo evitar lesiones en hombros?",
        "¿Cardio antes o después de pesas?",
    )

    private data class Intent(
        val keywords: List<String>,
        val answer: (UserProfile?) -> String,
    )

    fun answer(profile: UserProfile?, question: String): String {
        val normalized = question.lowercase().normalize()
        val intent = intents.maxByOrNull { intent ->
            intent.keywords.count { normalized.contains(it) }
        }
        val score = intent?.keywords?.count { normalized.contains(it) } ?: 0
        return if (intent != null && score > 0) intent.answer(profile) else fallback(profile, question)
    }

    private fun String.normalize(): String = this
        .replace('á', 'a').replace('é', 'e').replace('í', 'i')
        .replace('ó', 'o').replace('ú', 'u').replace('ñ', 'n')

    private fun weightOf(profile: UserProfile?): Int = profile?.weightKg ?: 70

    private val intents = listOf(
        Intent(listOf("proteina", "proteinas", "protein")) { profile ->
            val w = weightOf(profile)
            val low = (w * 1.6).roundToInt()
            val high = (w * 2.2).roundToInt()
            buildString {
                append("Para ganancias musculares se recomienda entre 1.6 y 2.2 g de proteína por kg de peso corporal al día. ")
                append("Con tus $w kg, eso son entre $low g y $high g diarios. ")
                append("Distribúyelos en 4-5 comidas de unos ${(high / 4.5).roundToInt()} g para maximizar la síntesis proteica. ")
                append("Fuentes ideales: pollo, huevos, atún, yogur griego y legumbres.")
                if (profile?.goal == Goal.LOSE_FAT) {
                    append("\n\nComo tu objetivo es bajar grasa, quédate en la parte alta del rango: protege músculo y te mantiene más saciada.")
                }
            }
        },
        Intent(listOf("sentadilla", "squat", "tecnica de sentadilla")) {
            "Tres cosas que revisar en la sentadilla:\n\n" +
                "1. Tobillo. Si se te despega el talón, te falta movilidad. Prueba con un disco pequeño bajo los talones mientras la trabajas.\n" +
                "2. Rodillas. Empújalas hacia afuera durante todo el recorrido; si se meten hacia dentro, activa glúteo medio con banda antes de las series.\n" +
                "3. Profundidad. Baja hasta donde la pelvis no se meta hacia abajo. Ese es tu rango útil de hoy y va a mejorar.\n\n" +
                "Tienes el artículo completo en la sección Aprende: \"Cómo hacer bien la sentadilla profunda\"."
        },
        Intent(listOf("antes de entrenar", "pre entreno", "preentreno", "que como antes", "antes del entrenamiento")) { profile ->
            val w = weightOf(profile)
            "Entre 1 y 2 horas antes: carbohidrato de fácil digestión más algo de proteína, bajo en grasa y fibra.\n\n" +
                "Para ti serían unos ${(w * 1.0).roundToInt()} g de carbohidrato y ${(w * 0.3).roundToInt()} g de proteína. " +
                "Ejemplos: avena con plátano y un scoop de proteína, o pan integral con pavo y fruta.\n\n" +
                "Si entrenas muy temprano y no te da tiempo, con un plátano y café 20 minutos antes basta. Lo que no puedes saltarte es la hidratación."
        },
        Intent(listOf("descansar", "descanso", "cuantos dias", "frecuencia", "recuperar")) { profile ->
            val level = profile?.level?.displayName?.lowercase() ?: "intermedio"
            "Con un nivel $level, entre 1 y 2 días de descanso completo por semana es lo razonable, más descansos activos (caminar, movilidad).\n\n" +
                "Señales de que necesitas descansar aunque no toque: rendimiento que baja dos sesiones seguidas, sueño malo, articulaciones molestas o ganas cero de entrenar durante varios días.\n\n" +
                "Cada 5 a 8 semanas mete una semana de descarga con la mitad del volumen. No pierdes músculo y casi siempre vuelves más fuerte."
        },
        Intent(listOf("hombro", "hombros", "manguito", "rotador")) {
            "El manguito rotador es un conjunto de 4 músculos que estabilizan el hombro. Ignorarlos es la causa número uno de lesiones en personas que hacen press.\n\n" +
                "CALENTAMIENTO: rotaciones externas e internas con banda elástica, 3 series de 15 por lado antes de cargar.\n\n" +
                "EQUILIBRIO PUSH/PULL: por cada ejercicio de empuje horizontal, haz uno o dos de tirón horizontal (remo).\n\n" +
                "SEÑAL DE ALARMA: dolor puntual y agudo al bajar la barra. El dolor muscular difuso al día siguiente es normal; ese no."
        },
        Intent(listOf("cardio", "correr antes", "pesas o cardio")) { profile ->
            val goal = profile?.goal ?: Goal.MAINTAIN
            when (goal) {
                Goal.GAIN_MUSCLE -> "Con tu objetivo de ganar músculo: pesas primero, cardio al final o en otra sesión. El cardio intenso previo baja tu rendimiento en las series pesadas, que es tu estímulo principal.\n\nSi puedes separarlas al menos 6 horas, mejor todavía."
                Goal.LOSE_FAT -> "Para bajar grasa el orden importa poco: lo que manda es tu déficit calórico. Aun así, haz pesas primero para conservar masa muscular, y deja 15-20 minutos de cardio al final.\n\nLa caminata diaria fuera del gimnasio suma más de lo que crees."
                Goal.ENDURANCE -> "Con objetivo de resistencia, invierte el orden: el trabajo aeróbico de calidad va primero y las pesas después, como soporte.\n\nSepara las sesiones duras de cardio y de fuerza al menos un día."
                Goal.MAINTAIN -> "Calienta 5-10 minutos, haz pesas y cierra con 15-20 minutos de cardio suave. Es el orden que mejor equilibra fuerza y salud cardiovascular."
            }
        },
        Intent(listOf("imc", "peso ideal", "cuanto deberia pesar")) { profile ->
            if (profile == null) {
                "Completa tu perfil y te calculo el IMC con tus datos reales."
            } else {
                "Tu IMC es ${profile.bmi} (${profile.bmiLabel.lowercase()}), con ${profile.weightKg} kg y ${profile.heightCm} cm.\n\n" +
                    "Ojo: el IMC no distingue músculo de grasa. Si entrenas fuerza puede salirte alto sin que sea un problema. " +
                    "Fíjate más en cómo te queda la ropa, en tu fuerza en los básicos y en medidas de cintura."
            }
        },
        Intent(listOf("calorias", "caloria", "deficit", "superavit", "cuanto comer")) { profile ->
            if (profile == null) {
                "Completa tu perfil (edad, peso, estatura y nivel) y te calculo tu objetivo calórico."
            } else {
                "Con tus datos (${profile.age} años, ${profile.weightKg} kg, ${profile.heightCm} cm, nivel ${profile.level.displayName.lowercase()}) " +
                    "tu objetivo está en ${profile.dailyCalories} kcal al día para \"${profile.goal.displayName.lowercase()}\".\n\n" +
                    "Reparte así: ${profile.proteinTargetG} g de proteína, el resto entre carbohidratos y grasas según lo que te siente mejor.\n\n" +
                    "Tu plan de la pestaña Dieta ya está armado con ese número."
            }
        },
        Intent(listOf("agua", "hidratacion", "hidratar", "tomar agua")) { profile ->
            val w = weightOf(profile)
            val liters = (w * 35 / 1000.0)
            "Como base, unos ${"%.1f".format(liters)} litros al día (35 ml por kg de peso). " +
                "Suma entre 500 y 800 ml extra por cada hora de entrenamiento, más si sudas mucho o hace calor.\n\n" +
                "Truco rápido: si tu orina es amarillo claro vas bien; si es oscura, te falta agua."
        },
        Intent(listOf("creatina", "suplemento", "suplementos")) {
            "De todo lo que se vende, solo tres cosas tienen evidencia sólida:\n\n" +
                "CREATINA MONOHIDRATO: 3-5 g al día, a cualquier hora, todos los días. Es el suplemento más estudiado y más barato.\n" +
                "PROTEÍNA EN POLVO: no es magia, es comida práctica para llegar a tu meta diaria.\n" +
                "CAFEÍNA: 3 mg por kg, 45 minutos antes de entrenar.\n\n" +
                "Todo lo demás es opcional y va después de tener dieta, sueño y entrenamiento en orden."
        },
        Intent(listOf("dormir", "sueno", "insomnio", "descansar mejor")) {
            "El sueño es el recuperador más potente que existe y ningún suplemento se le acerca.\n\n" +
                "Apunta a 7-9 horas. Con menos de 6 sube la grelina (hambre) y baja la leptina (saciedad): comes más y rindes menos.\n\n" +
                "Lo que sí funciona: misma hora para levantarte todos los días, luz natural en la primera hora, sin cafeína después de las 2 de la tarde y cuarto fresco y oscuro."
        },
        Intent(listOf("agujetas", "dolor muscular", "doms", "adolorid")) {
            "Las agujetas no son ácido láctico: son microdaño e inflamación por trabajo excéntrico, y aparecen entre 24 y 72 horas después.\n\n" +
                "No son medida de calidad del entrenamiento: puedes progresar perfectamente sin tenerlas.\n\n" +
                "Lo que ayuda: movimiento suave, dormir bien, proteína suficiente y no repetir el mismo estímulo intenso al día siguiente."
        },
        Intent(listOf("rutina", "mi rutina", "que entreno hoy", "entrenamiento de hoy")) { profile ->
            if (profile == null) {
                "Completa el onboarding y te armo la rutina semanal."
            } else {
                "Tu rutina está armada para ${profile.sport.displayName.lowercase()}, nivel ${profile.level.displayName.lowercase()}, " +
                    "con objetivo \"${profile.goal.displayName.lowercase()}\".\n\n" +
                    (if (profile.sport.id == "gym") {
                        "Solo uso las ${profile.equipment.size} máquinas que marcaste como disponibles en tu gym, así que no te va a salir ningún ejercicio que no puedas hacer.\n\n"
                    } else {
                        "Está adaptada a lo que marcaste en la pantalla de equipo.\n\n"
                    }) +
                    "Ábrela en la pestaña Rutina: ahí puedes marcar cada ejercicio y usar el temporizador de descanso."
            }
        },
        Intent(listOf("bajar de peso", "perder grasa", "adelgazar", "definir")) { profile ->
            val kcal = profile?.dailyCalories ?: 2000
            "Perder grasa es déficit calórico sostenido, no un ejercicio ni un alimento mágico.\n\n" +
                "1. Come alrededor de ${kcal - 400} kcal si quieres bajar entre 0.4 y 0.7 kg por semana.\n" +
                "2. Mantén la proteína alta para no perder músculo.\n" +
                "3. Sigue entrenando fuerza: el cardio quema, la fuerza conserva.\n" +
                "4. Sube tus pasos diarios; es la variable que más gente ignora.\n\n" +
                "Si en 3 semanas el peso no se mueve nada, ajusta 150-200 kcal, no más."
        },
        Intent(listOf("ganar musculo", "masa muscular", "volumen", "subir de peso")) { profile ->
            val kcal = profile?.dailyCalories ?: 2400
            "Para ganar músculo necesitas tres cosas a la vez:\n\n" +
                "1. Superávit pequeño: unas ${kcal + 200} kcal. Más que eso solo agrega grasa.\n" +
                "2. Sobrecarga progresiva: sube repeticiones o peso semana a semana y llévalo registrado.\n" +
                "3. Proteína suficiente y sueño.\n\n" +
                "Espera entre 0.25 y 0.5 kg de ganancia al mes si ya llevas tiempo entrenando. Más rápido que eso casi siempre es grasa y agua."
        },
        Intent(listOf("rodilla", "espalda baja", "lumbar", "me duele")) {
            "Dolor con entrenamiento merece una regla simple: si el dolor es puntual, agudo o dura más de 48 horas, no lo empujes.\n\n" +
                "Qué hacer mientras tanto:\n" +
                "1. No pares por completo: reduce rango y carga al punto sin dolor.\n" +
                "2. Cambia la variante (por ejemplo, prensa en vez de sentadilla profunda).\n" +
                "3. Fortalece lo que rodea la articulación.\n\n" +
                "Si el dolor sigue más de dos semanas o hay inflamación, consulta con un fisioterapeuta. Esto es orientación general, no diagnóstico."
        },
    )

    private fun fallback(profile: UserProfile?, question: String): String {
        val name = profile?.firstName ?: "atleta"
        val sport = profile?.sport?.displayName?.lowercase() ?: "tu deporte"
        val article = ArticleCatalog.all.random(kotlin.random.Random(question.hashCode()))
        return "Buena pregunta, $name. Con tus datos ($sport, nivel " +
            "${profile?.level?.displayName?.lowercase() ?: "intermedio"}, objetivo " +
            "\"${profile?.goal?.displayName?.lowercase() ?: "mantenerte"}\") lo que más te va a mover la aguja " +
            "es la constancia y la sobrecarga progresiva.\n\n" +
            "Puedo ayudarte mejor si me preguntas sobre: proteína y calorías, técnica de un ejercicio, " +
            "descanso y recuperación, prevención de lesiones, o qué comer antes y después de entrenar.\n\n" +
            "Mientras tanto te dejo algo relacionado en Aprende: \"${article.title}\"."
    }
}
