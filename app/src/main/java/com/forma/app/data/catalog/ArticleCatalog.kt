package com.forma.app.data.catalog

import com.forma.app.domain.model.Article
import com.forma.app.domain.model.ArticleCategory
import com.forma.app.domain.model.ArticleSection

object ArticleCatalog {

    val all: List<Article> = listOf(
        Article(
            id = "art_sentadilla",
            title = "Cómo hacer bien la sentadilla profunda",
            category = ArticleCategory.TECHNIQUE,
            readMinutes = 5,
            summary = "La sentadilla no se rompe por bajar, se rompe por bajar mal. Esto es lo que revisa un entrenador antes de subirte el peso.",
            imageKey = "article_squat",
            featured = true,
            sections = listOf(
                ArticleSection(
                    "Primero la movilidad de tobillo",
                    "Si el talón se te despega, el problema casi nunca es la cadera: es el tobillo. Ponte de rodillas frente a una pared, adelanta un pie y trata de tocar la pared con la rodilla sin levantar el talón. Si te faltan más de 8 cm, trabaja movilidad antes de cargar. Mientras tanto, unos tenis con tacón o un disco pequeño bajo los talones te dejan bajar con la espalda recta.",
                ),
                ArticleSection(
                    "La barra va donde puedas empujarla",
                    "Barra alta te obliga a un torso más vertical y carga el cuádriceps. Barra baja permite más peso y reparte hacia cadera e isquios. Ninguna es mejor: elige la que te deje mantener la espalda neutra en todo el rango y quédate ahí varias semanas antes de cambiar.",
                ),
                ArticleSection(
                    "Rodillas hacia afuera, no hacia dentro",
                    "El valgo dinámico (rodilla que se mete) es la señal más común de glúteo medio débil. Antes de las series pesadas haz 2 series de 15 repeticiones de caminata lateral con banda. En la serie, piensa en 'romper el piso' separando los pies sin moverlos.",
                ),
                ArticleSection(
                    "¿Qué tan profundo?",
                    "Baja hasta donde la pelvis no se meta hacia abajo (el famoso butt wink). Ese punto es tu profundidad útil hoy y va a mejorar con movilidad. Forzar más rango con la lumbar redondeada no da más músculo, solo más riesgo.",
                ),
                ArticleSection(
                    "Progresa por semanas, no por días",
                    "Sube peso cuando puedas completar todas las series con una o dos repeticiones en reserva y buena técnica dos entrenamientos seguidos. Si una semana se siente pesada, repite el peso: la constancia gana a la prisa.",
                ),
            ),
        ),
        Article(
            id = "art_hombro",
            title = "Prevención de lesiones de hombro en el gym",
            category = ArticleCategory.INJURY,
            readMinutes = 6,
            summary = "El hombro es la articulación más lesionada en el entrenamiento. Descubre cómo protegerla y mantenerla fuerte.",
            imageKey = "article_shoulder",
            sections = listOf(
                ArticleSection(
                    "Entiende el manguito rotador",
                    "El manguito rotador es un conjunto de 4 músculos que estabilizan el hombro. Ignorarlos es la causa número uno de lesiones en personas que hacen press. Son músculos pequeños: no necesitan peso, necesitan frecuencia.",
                ),
                ArticleSection(
                    "Calentamiento específico",
                    "Siempre calienta con rotaciones externas e internas con banda elástica, 3 series de 15 por lado. Esto activa el manguito antes de cargar y mejora la posición de la cabeza del húmero durante el press.",
                ),
                ArticleSection(
                    "Equilibrio push/pull",
                    "Por cada ejercicio de empuje horizontal (press banca) deberías hacer uno o dos de tirón horizontal (remo). La mayoría de los hombros lesionados vienen de programas con el doble de empuje que de tirón.",
                ),
                ArticleSection(
                    "Señales de alarma",
                    "Dolor en la cara frontal del hombro al bajar la barra, chasquido con dolor, o molestia que dura más de 48 horas: son señales para bajar carga y consultar. Dolor muscular difuso al día siguiente es normal; dolor puntual y agudo no lo es.",
                ),
                ArticleSection(
                    "Qué hacer si ya duele",
                    "No dejes de mover el hombro por completo. Cambia press con barra por press neutro con mancuernas o máquina, reduce rango al punto sin dolor y mantén el trabajo de manguito. El reposo absoluto suele retrasar la recuperación.",
                ),
            ),
        ),
        Article(
            id = "art_ventana",
            title = "Nutrición post-entrenamiento: la ventana anabólica",
            category = ArticleCategory.NUTRITION,
            readMinutes = 4,
            summary = "¿Realmente existe la ventana anabólica? La ciencia actualizada sobre qué comer después de entrenar.",
            imageKey = "article_nutrition",
            sections = listOf(
                ArticleSection(
                    "La ventana no son 30 minutos",
                    "Durante años se dijo que había media hora mágica después de entrenar. La evidencia actual apunta a una ventana de varias horas: lo que más importa es tu proteína total del día, no el cronómetro.",
                ),
                ArticleSection(
                    "Cuánta proteína por comida",
                    "Entre 0.4 y 0.55 g por kilo de peso corporal por comida, repartida en 4 o 5 comidas, maximiza la síntesis proteica. Para 70 kg son entre 28 y 38 g por comida.",
                ),
                ArticleSection(
                    "Los carbohidratos sí importan si entrenas dos veces",
                    "Si entrenas una vez al día, repones glucógeno sin problema con tu alimentación normal. Si entrenas dos veces o compites, sí conviene meter carbohidratos rápidos en las primeras dos horas.",
                ),
                ArticleSection(
                    "Lo que sí es urgente: hidratación",
                    "Pésate antes y después de una sesión larga. Por cada kilo perdido, bebe entre 1.2 y 1.5 litros a lo largo de las siguientes horas, con algo de sodio si sudaste mucho.",
                ),
            ),
        ),
        Article(
            id = "art_sueno",
            title = "El sueño como herramienta de recuperación",
            category = ArticleCategory.RECOVERY,
            readMinutes = 5,
            summary = "Ningún suplemento se acerca a lo que hacen siete u ocho horas de sueño por tu rendimiento.",
            imageKey = "article_sleep",
            sections = listOf(
                ArticleSection(
                    "Qué pasa mientras duermes",
                    "La mayor parte de la hormona de crecimiento se libera en las primeras fases de sueño profundo. Ahí es donde reparas tejido, consolidas aprendizaje motor y regulas el apetito.",
                ),
                ArticleSection(
                    "Dormir poco te hace comer más",
                    "Con menos de 6 horas suben la grelina (hambre) y baja la leptina (saciedad). Personas en déficit calórico que duermen poco pierden más músculo y menos grasa con la misma dieta.",
                ),
                ArticleSection(
                    "Rutina que sí funciona",
                    "Misma hora para levantarte todos los días, luz natural en la primera hora del día, sin cafeína después de las 2 de la tarde y un cuarto fresco y oscuro. Es aburrido, pero es lo que mueve la aguja.",
                ),
                ArticleSection(
                    "Siestas: sí, pero cortas",
                    "Entre 20 y 30 minutos mejoran la alerta sin dejarte atontado. Si necesitas más, prefiere un ciclo completo de 90 minutos y no después de las 4 de la tarde.",
                ),
            ),
        ),
        Article(
            id = "art_progresion",
            title = "Sobrecarga progresiva sin estancarte",
            category = ArticleCategory.TECHNIQUE,
            readMinutes = 5,
            summary = "Subir peso no es la única forma de progresar. Cinco variables que puedes mover cuando el peso ya no sube.",
            imageKey = "article_progress",
            sections = listOf(
                ArticleSection(
                    "El peso es solo una variable",
                    "Puedes progresar subiendo peso, repeticiones, series, mejorando la técnica o reduciendo el descanso. Si llevas semanas atorado en el peso, mueve otra variable y regresa al peso más adelante.",
                ),
                ArticleSection(
                    "Repeticiones en reserva",
                    "Trabajar dejando 1 a 3 repeticiones en reserva da casi el mismo estímulo que llegar al fallo, con mucha menos fatiga acumulada. Reserva el fallo para la última serie de ejercicios de aislamiento.",
                ),
                ArticleSection(
                    "Descargas programadas",
                    "Cada 5 a 8 semanas baja el volumen a la mitad durante una semana. No pierdes músculo: dejas que se recupere el sistema nervioso y casi siempre vuelves más fuerte.",
                ),
                ArticleSection(
                    "Lleva registro",
                    "La memoria es pésima para esto. Anota series, repeticiones y peso. FORMA guarda tu historial de sesiones para que veas si de verdad estás progresando o solo repitiendo.",
                ),
            ),
        ),
        Article(
            id = "art_cardio",
            title = "¿Cardio antes o después de las pesas?",
            category = ArticleCategory.TECHNIQUE,
            readMinutes = 4,
            summary = "El orden importa, pero menos de lo que crees. Depende de cuál sea tu objetivo principal.",
            imageKey = "article_cardio",
            sections = listOf(
                ArticleSection(
                    "Si tu prioridad es fuerza o músculo",
                    "Haz pesas primero. El cardio previo intenso reduce el rendimiento en las series pesadas y ese es tu estímulo principal. Deja el cardio al final o en otra sesión.",
                ),
                ArticleSection(
                    "Si tu prioridad es resistencia",
                    "Invierte el orden: la calidad del trabajo aeróbico manda. Las pesas pasan a ser soporte y pueden ir después o en días separados.",
                ),
                ArticleSection(
                    "El efecto interferencia existe, pero es manejable",
                    "Separar las sesiones al menos 6 horas reduce casi por completo la interferencia. Si solo puedes entrenar una vez al día, prioriza el objetivo que más te importe ahora.",
                ),
                ArticleSection(
                    "Calentar no es hacer cardio",
                    "Cinco a diez minutos suaves para subir la temperatura no cuentan como cardio ni afectan tus series. No te los saltes.",
                ),
            ),
        ),
        Article(
            id = "art_proteina",
            title = "Cuánta proteína necesitas de verdad",
            category = ArticleCategory.NUTRITION,
            readMinutes = 5,
            summary = "Ni los 300 g del influencer ni los 50 g de la recomendación general. Los números que sí tienen respaldo.",
            imageKey = "article_protein",
            sections = listOf(
                ArticleSection(
                    "El rango que funciona",
                    "Entre 1.6 y 2.2 g por kilo de peso corporal al día cubre a prácticamente todas las personas que entrenan fuerza. Más que eso no ha mostrado beneficio adicional para ganar músculo.",
                ),
                ArticleSection(
                    "Sube la proteína si estás en déficit",
                    "Cuando bajas calorías, subir a 2.2-2.6 g por kilo protege masa muscular y ayuda con la saciedad. Es el único escenario donde el extremo alto del rango tiene sentido.",
                ),
                ArticleSection(
                    "Fuentes que valen la pena",
                    "Pollo, huevo, atún, yogur griego, res magra, pescado, tofu y legumbres. Si combinas legumbres con cereales cubres el perfil de aminoácidos sin problema.",
                ),
                ArticleSection(
                    "¿Y la proteína en polvo?",
                    "Es comida, no magia: resuelve el problema logístico de llegar a tu meta. Si ya llegas comiendo, no la necesitas.",
                ),
            ),
        ),
        Article(
            id = "art_agujetas",
            title = "Agujetas: qué son y qué no las quita",
            category = ArticleCategory.RECOVERY,
            readMinutes = 4,
            summary = "No es ácido láctico, no significa que el entrenamiento fue bueno y el agua con azúcar no sirve.",
            imageKey = "article_soreness",
            sections = listOf(
                ArticleSection(
                    "No es ácido láctico",
                    "El lactato se aclara en menos de una hora. Las agujetas (DOMS) son microdaño e inflamación por trabajo excéntrico, y aparecen entre 24 y 72 horas después.",
                ),
                ArticleSection(
                    "No son medida de calidad",
                    "Puedes progresar perfectamente sin agujetas. De hecho, conforme te adaptas a un ejercicio dejas de tenerlas aunque sigas ganando fuerza.",
                ),
                ArticleSection(
                    "Qué sí ayuda",
                    "Movimiento suave, dormir bien, comer suficiente proteína y no repetir el mismo estímulo intenso al día siguiente. El masaje y el foam roller alivian la sensación aunque no aceleran la reparación.",
                ),
                ArticleSection(
                    "Cuándo preocuparte",
                    "Dolor extremo con orina oscura después de un entrenamiento muy duro requiere atención médica inmediata: puede ser rabdomiólisis.",
                ),
            ),
        ),
        Article(
            id = "art_constancia",
            title = "Constancia: el plan que sí puedes sostener",
            category = ArticleCategory.MINDSET,
            readMinutes = 4,
            summary = "Tres entrenamientos a la semana durante un año superan a seis durante un mes. Cómo diseñar para no abandonar.",
            imageKey = "article_mindset",
            sections = listOf(
                ArticleSection(
                    "Elige la frecuencia mínima viable",
                    "Empieza con el número de días que podrías sostener en tu peor semana del mes, no en la mejor. Si esa semana son tres días, tu plan son tres días.",
                ),
                ArticleSection(
                    "La regla de nunca fallar dos veces",
                    "Saltarte un día no rompe nada. Saltarte dos seguidos empieza a construir el hábito contrario. Si pierdes un entrenamiento, el siguiente es prioridad absoluta aunque sea corto.",
                ),
                ArticleSection(
                    "Mide procesos, no solo resultados",
                    "El peso y el espejo se mueven lento y con ruido. Cuenta entrenamientos completados por semana: es lo que de verdad controlas.",
                ),
                ArticleSection(
                    "Comunidad",
                    "Las personas que comparten su progreso con alguien más son bastante más constantes. Publicar tu foto al terminar la rutina en FORMA no es presumir: es una herramienta de adherencia.",
                ),
            ),
        ),
        Article(
            id = "art_core",
            title = "Core fuerte no es hacer mil abdominales",
            category = ArticleCategory.TECHNIQUE,
            readMinutes = 4,
            summary = "El core se entrena resistiendo movimiento, no solo flexionando la columna.",
            imageKey = "article_core",
            sections = listOf(
                ArticleSection(
                    "Las cuatro funciones del core",
                    "Antiextensión (plancha), antiflexión lateral (farmer carry a un lado), antirrotación (pallof press) y antiextensión dinámica (ab wheel). Un buen plan cubre las cuatro.",
                ),
                ArticleSection(
                    "El crunch no es el enemigo",
                    "Flexionar la columna con control tiene su lugar y desarrolla el recto abdominal. El problema es hacer solo eso y con volúmenes absurdos.",
                ),
                ArticleSection(
                    "Carga el core como cargas todo",
                    "Si aguantas la plancha 3 minutos, no necesitas más tiempo: necesitas más dificultad. Agrega peso, reduce la base de apoyo o cambia a una variante más exigente.",
                ),
                ArticleSection(
                    "Respiración",
                    "Aprende a mantener presión intraabdominal: inhala al 70 %, tensa como si fueras a recibir un golpe y sostén durante la repetición. Es lo que protege tu espalda con peso.",
                ),
            ),
        ),
    )

    fun byId(id: String): Article? = all.firstOrNull { it.id == id }
}
