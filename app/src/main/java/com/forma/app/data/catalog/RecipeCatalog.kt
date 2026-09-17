package com.forma.app.data.catalog

import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.Recipe
import com.forma.app.domain.model.RecipeIngredient

private fun ing(id: String, amount: String) =
    RecipeIngredient(name = IngredientCatalog.name(id), amount = amount, ingredientId = id)

private fun recipe(
    id: String,
    title: String,
    emoji: String,
    slot: MealSlot,
    kcal: Int,
    p: Int,
    c: Int,
    f: Int,
    minutes: Int,
    imageKey: String,
    ingredients: List<RecipeIngredient>,
    steps: List<String>,
) = Recipe(id, title, emoji, slot, kcal, p, c, f, minutes, ingredients, steps, imageKey)

/**
 * Recetario base de FORMA. El generador de dieta arma las 5 opciones por comida puntuando
 * cada receta contra los ingredientes que marcó el usuario y contra sus calorías objetivo.
 */
object RecipeCatalog {

    private val breakfasts = listOf(
        recipe(
            "b_avena_frutas", "Bowl de avena con frutas", "\uD83E\uDD63", MealSlot.BREAKFAST,
            380, 14, 58, 10, 10, "food_oats",
            listOf(
                ing("avena", "60 g"),
                ing("platano", "1 pieza"),
                ing("arandanos", "80 g"),
                ing("almendras", "15 g"),
                ing("leche", "200 ml"),
            ),
            listOf(
                "Calienta la leche y agrega la avena. Cocina 4 minutos a fuego bajo moviendo.",
                "Retira del fuego y deja reposar 2 minutos para que espese.",
                "Sirve y acomoda encima el plátano en rodajas y los arándanos.",
                "Termina con las almendras picadas y, si quieres, canela al gusto.",
            ),
        ),
        recipe(
            "b_claras_espinaca", "Tortilla de claras con espinacas", "\uD83C\uDF73", MealSlot.BREAKFAST,
            310, 28, 12, 16, 12, "food_omelette",
            listOf(
                ing("claras", "5 piezas"),
                ing("espinaca", "80 g"),
                ing("queso_panela", "40 g"),
                ing("aceite_oliva", "1 cdita"),
                ing("jitomate", "1 pieza"),
            ),
            listOf(
                "Saltea la espinaca con el aceite hasta que reduzca.",
                "Bate las claras con sal y pimienta y viértelas en el sartén.",
                "Cuando cuaje la base agrega el panela en cubos y dobla la tortilla.",
                "Sirve con el jitomate en rodajas.",
            ),
        ),
        recipe(
            "b_tostadas_aguacate", "Tostadas de aguacate con huevo", "\uD83E\uDD51", MealSlot.BREAKFAST,
            420, 20, 34, 24, 10, "food_avocado_toast",
            listOf(
                ing("pan_integral", "2 rebanadas"),
                ing("aguacate", "1/2 pieza"),
                ing("huevo", "2 piezas"),
                ing("limon", "1/2 pieza"),
                ing("chia", "1 cdita"),
            ),
            listOf(
                "Tuesta el pan hasta que quede firme.",
                "Machaca el aguacate con limón, sal y pimienta.",
                "Cocina los huevos estrellados o pochados a tu gusto.",
                "Unta el aguacate, coloca el huevo encima y espolvorea la chía.",
            ),
        ),
        recipe(
            "b_smoothie_tropical", "Smoothie proteico tropical", "\uD83E\uDD64", MealSlot.BREAKFAST,
            340, 30, 42, 6, 5, "food_smoothie",
            listOf(
                ing("proteina_polvo", "1 scoop"),
                ing("mango", "150 g"),
                ing("piña", "100 g"),
                ing("leche_almendra", "250 ml"),
                ing("linaza", "1 cda"),
            ),
            listOf(
                "Congela la fruta la noche anterior para que quede espeso.",
                "Licúa todo junto durante 45 segundos.",
                "Si queda muy espeso agrega un chorrito más de leche.",
                "Sirve de inmediato para no perder textura.",
            ),
        ),
        recipe(
            "b_hotcakes_avena", "Hot cakes de avena y plátano", "\uD83E\uDD5E", MealSlot.BREAKFAST,
            450, 24, 62, 12, 15, "food_pancakes",
            listOf(
                ing("avena", "70 g"),
                ing("platano", "1 pieza"),
                ing("huevo", "2 piezas"),
                ing("proteina_polvo", "1/2 scoop"),
                ing("cacahuate", "1 cda"),
            ),
            listOf(
                "Licúa la avena, el plátano, los huevos y la proteína hasta formar una mezcla espesa.",
                "Deja reposar 5 minutos para que la avena absorba.",
                "Cocina porciones pequeñas en sartén antiadherente a fuego medio-bajo.",
                "Sirve con la crema de cacahuate derretida por encima.",
            ),
        ),
        recipe(
            "b_yogur_granola", "Yogur griego con granola casera", "\uD83E\uDD5B", MealSlot.BREAKFAST,
            360, 24, 40, 12, 8, "food_yogurt",
            listOf(
                ing("yogur_griego", "200 g"),
                ing("avena", "40 g"),
                ing("nueces", "15 g"),
                ing("fresa", "100 g"),
                ing("chia", "1 cdita"),
            ),
            listOf(
                "Tuesta la avena con las nueces en sartén seco durante 5 minutos.",
                "Deja enfriar para que quede crujiente.",
                "Sirve el yogur en un bowl y agrega la granola encima.",
                "Corona con las fresas y la chía.",
            ),
        ),
        recipe(
            "b_huevos_rancheros", "Huevos rancheros ligeros", "\uD83C\uDF5B", MealSlot.BREAKFAST,
            400, 24, 38, 16, 15, "food_rancheros",
            listOf(
                ing("huevo", "2 piezas"),
                ing("tortilla", "2 piezas"),
                ing("jitomate", "2 piezas"),
                ing("frijoles", "80 g"),
                ing("aguacate", "1/4 pieza"),
            ),
            listOf(
                "Asa los jitomates y licúalos con un trozo de cebolla y chile al gusto.",
                "Calienta la salsa 5 minutos y sazona.",
                "Calienta las tortillas y coloca encima los huevos estrellados.",
                "Baña con la salsa y acompaña con frijoles y aguacate.",
            ),
        ),
        recipe(
            "b_chilaquiles_pollo", "Chilaquiles verdes con pollo", "\uD83C\uDF2E", MealSlot.BREAKFAST,
            480, 34, 46, 18, 20, "food_chilaquiles",
            listOf(
                ing("tortilla", "3 piezas"),
                ing("pollo", "100 g"),
                ing("queso_panela", "30 g"),
                ing("jitomate", "salsa verde, 150 g"),
                ing("aceite_oliva", "1 cdita"),
            ),
            listOf(
                "Corta las tortillas en triángulos y hornéalas 12 minutos a 200 °C.",
                "Calienta la salsa verde en un sartén amplio.",
                "Agrega los totopos y mueve solo unos segundos para que no se aguaden.",
                "Sirve con el pollo deshebrado y el panela rallado.",
            ),
        ),
        recipe(
            "b_molletes", "Molletes integrales de frijol", "\uD83C\uDF5E", MealSlot.BREAKFAST,
            390, 18, 52, 12, 12, "food_molletes",
            listOf(
                ing("pan_integral", "2 rebanadas"),
                ing("frijoles", "120 g"),
                ing("queso_panela", "40 g"),
                ing("jitomate", "1 pieza"),
                ing("aguacate", "1/4 pieza"),
            ),
            listOf(
                "Calienta los frijoles refritos sin aceite hasta que espesen.",
                "Unta el pan y cubre con el queso.",
                "Gratina 6 minutos en horno o air fryer.",
                "Sirve con pico de gallo de jitomate y aguacate.",
            ),
        ),
        recipe(
            "b_omelette_champinones", "Omelette de champiñones y panela", "\uD83C\uDF44", MealSlot.BREAKFAST,
            350, 30, 10, 22, 12, "food_omelette",
            listOf(
                ing("huevo", "3 piezas"),
                ing("champinones", "100 g"),
                ing("queso_panela", "40 g"),
                ing("espinaca", "50 g"),
                ing("aceite_oliva", "1 cdita"),
            ),
            listOf(
                "Saltea los champiñones hasta que suelten el agua y se doren.",
                "Agrega la espinaca y deja que reduzca.",
                "Vierte el huevo batido y cocina a fuego bajo.",
                "Agrega el queso, dobla y sirve.",
            ),
        ),
        recipe(
            "b_pudin_chia", "Pudín de chía con fresas", "\u26AB", MealSlot.BREAKFAST,
            320, 14, 34, 16, 5, "food_chia",
            listOf(
                ing("chia", "3 cdas"),
                ing("leche_almendra", "250 ml"),
                ing("fresa", "120 g"),
                ing("almendras", "10 g"),
                ing("yogur_griego", "80 g"),
            ),
            listOf(
                "Mezcla la chía con la leche y deja reposar 10 minutos.",
                "Vuelve a mover para deshacer grumos y refrigera toda la noche.",
                "En la mañana agrega el yogur encima.",
                "Corona con fresas y almendras picadas.",
            ),
        ),
        recipe(
            "b_burrito_desayuno", "Burrito de desayuno", "\uD83C\uDF2F", MealSlot.BREAKFAST,
            470, 26, 52, 18, 15, "food_burrito",
            listOf(
                ing("tortilla", "1 grande"),
                ing("huevo", "2 piezas"),
                ing("frijoles", "80 g"),
                ing("pimiento", "1/2 pieza"),
                ing("aguacate", "1/4 pieza"),
            ),
            listOf(
                "Saltea el pimiento en tiras con un poco de aceite.",
                "Agrega el huevo y revuelve hasta que cuaje.",
                "Calienta la tortilla y unta los frijoles.",
                "Rellena con el huevo y el aguacate, enrolla y sella en el sartén.",
            ),
        ),
    )

    private val lunches = listOf(
        recipe(
            "l_pollo_arroz_brocoli", "Bowl de pollo con arroz y brócoli", "\uD83C\uDF57", MealSlot.LUNCH,
            520, 42, 58, 12, 25, "food_chicken_bowl",
            listOf(
                ing("pollo", "180 g"),
                ing("arroz", "150 g cocido"),
                ing("brocoli", "150 g"),
                ing("aceite_oliva", "1 cda"),
                ing("limon", "1 pieza"),
            ),
            listOf(
                "Marina el pollo con limón, ajo, sal y pimienta 10 minutos.",
                "Cocínalo a la plancha 5 minutos por lado hasta dorar.",
                "Cuece el brócoli al vapor 4 minutos para que quede firme.",
                "Arma el bowl con el arroz de base y baña con el jugo del sartén.",
            ),
        ),
        recipe(
            "l_salmon_quinoa", "Salmón al limón con quinoa", "\uD83C\uDF63", MealSlot.LUNCH,
            610, 40, 48, 26, 30, "food_salmon",
            listOf(
                ing("salmon", "170 g"),
                ing("quinoa", "140 g cocida"),
                ing("espinaca", "80 g"),
                ing("limon", "1 pieza"),
                ing("aceite_oliva", "1 cda"),
            ),
            listOf(
                "Seca bien el salmón y sazona con sal, pimienta y ralladura de limón.",
                "Sella con la piel hacia abajo 4 minutos sin moverlo.",
                "Voltea y cocina 2 minutos más; debe quedar jugoso al centro.",
                "Sirve sobre la quinoa con la espinaca salteada.",
            ),
        ),
        recipe(
            "l_tacos_pescado", "Tacos de pescado con ensalada de col", "\uD83C\uDF2E", MealSlot.LUNCH,
            480, 36, 44, 16, 25, "food_fish_tacos",
            listOf(
                ing("pescado", "180 g"),
                ing("tortilla", "3 piezas"),
                ing("lechuga", "col morada, 80 g"),
                ing("limon", "2 piezas"),
                ing("yogur_griego", "3 cdas"),
            ),
            listOf(
                "Sazona el pescado con paprika, comino y limón.",
                "Cocínalo a la plancha 3 minutos por lado y desmenuza.",
                "Mezcla el yogur con limón y sal para la salsa.",
                "Arma los tacos con la col y baña con la salsa.",
            ),
        ),
        recipe(
            "l_pasta_pollo", "Pasta integral con pollo y jitomate", "\uD83C\uDF5D", MealSlot.LUNCH,
            560, 38, 66, 14, 25, "food_pasta",
            listOf(
                ing("pasta", "90 g en seco"),
                ing("pollo", "150 g"),
                ing("jitomate", "3 piezas"),
                ing("aceite_oliva", "1 cda"),
                ing("espinaca", "60 g"),
            ),
            listOf(
                "Cuece la pasta al dente y reserva media taza del agua de cocción.",
                "Dora el pollo en cubos y retíralo del sartén.",
                "Sofríe el jitomate picado con ajo hasta formar una salsa.",
                "Integra pasta, pollo y espinaca con el agua reservada.",
            ),
        ),
        recipe(
            "l_ensalada_atun", "Ensalada de atún con aguacate", "\uD83E\uDD57", MealSlot.LUNCH,
            450, 34, 24, 24, 12, "food_tuna_salad",
            listOf(
                ing("atun", "2 latas en agua"),
                ing("aguacate", "1/2 pieza"),
                ing("lechuga", "120 g"),
                ing("pepino", "1 pieza"),
                ing("limon", "1 pieza"),
            ),
            listOf(
                "Escurre bien el atún para que la ensalada no quede aguada.",
                "Pica la lechuga y el pepino en trozos grandes.",
                "Machaca el aguacate con limón y úsalo como aderezo cremoso.",
                "Mezcla todo y ajusta de sal y pimienta.",
            ),
        ),
        recipe(
            "l_bistec_nopales", "Bistec con nopales y frijoles", "\uD83E\uDD69", MealSlot.LUNCH,
            540, 44, 38, 20, 25, "food_steak",
            listOf(
                ing("res", "180 g"),
                ing("nopal", "150 g"),
                ing("frijoles", "120 g"),
                ing("jitomate", "1 pieza"),
                ing("tortilla", "2 piezas"),
            ),
            listOf(
                "Asa los nopales hasta que cambien de color y suelten la baba.",
                "Cocina el bistec a fuego alto 2 minutos por lado.",
                "Prepara un pico de gallo con jitomate, cebolla y cilantro.",
                "Sirve todo junto con los frijoles de olla y las tortillas.",
            ),
        ),
        recipe(
            "l_pollo_curry", "Pollo al curry con arroz integral", "\uD83C\uDF5B", MealSlot.LUNCH,
            580, 40, 62, 16, 30, "food_curry",
            listOf(
                ing("pollo", "170 g"),
                ing("arroz_integral", "150 g cocido"),
                ing("zanahoria", "1 pieza"),
                ing("leche_almendra", "150 ml"),
                ing("pimiento", "1 pieza"),
            ),
            listOf(
                "Sofríe el pollo en cubos hasta sellarlo.",
                "Agrega la zanahoria y el pimiento en tiras.",
                "Incorpora curry en polvo y la leche; cocina 10 minutos a fuego bajo.",
                "Sirve sobre el arroz integral con cilantro fresco.",
            ),
        ),
        recipe(
            "l_camarones_bowl", "Bowl de camarones con pimientos", "\uD83E\uDD90", MealSlot.LUNCH,
            500, 38, 56, 12, 20, "food_shrimp",
            listOf(
                ing("camaron", "180 g"),
                ing("arroz", "150 g cocido"),
                ing("pimiento", "1 pieza"),
                ing("limon", "1 pieza"),
                ing("aceite_oliva", "1 cda"),
            ),
            listOf(
                "Saltea los camarones 2 minutos por lado; se cocinan muy rápido.",
                "Retíralos y en el mismo sartén saltea los pimientos.",
                "Regresa los camarones y agrega ajo y limón.",
                "Sirve sobre el arroz con cilantro picado.",
            ),
        ),
        recipe(
            "l_lentejas", "Lentejas guisadas con verduras", "\uD83E\uDED5", MealSlot.LUNCH,
            470, 26, 62, 12, 35, "food_lentils",
            listOf(
                ing("lentejas", "150 g cocidas"),
                ing("zanahoria", "1 pieza"),
                ing("jitomate", "2 piezas"),
                ing("papa", "1 pieza"),
                ing("aceite_oliva", "1 cda"),
            ),
            listOf(
                "Sofríe cebolla, ajo y zanahoria en cubos pequeños.",
                "Agrega el jitomate licuado y deja sazonar 5 minutos.",
                "Incorpora las lentejas y la papa con agua o caldo.",
                "Cocina 20 minutos hasta que espese.",
            ),
        ),
        recipe(
            "l_tofu_salteado", "Tofu salteado con verduras", "\uD83E\uDDC8", MealSlot.LUNCH,
            490, 26, 60, 16, 20, "food_tofu",
            listOf(
                ing("tofu", "200 g"),
                ing("arroz", "150 g cocido"),
                ing("brocoli", "120 g"),
                ing("pimiento", "1 pieza"),
                ing("aceite_oliva", "1 cda"),
            ),
            listOf(
                "Prensa el tofu 10 minutos para quitarle el exceso de agua.",
                "Córtalo en cubos y dóralo por todos lados.",
                "Saltea las verduras a fuego alto para que queden crujientes.",
                "Integra con salsa de soya baja en sodio y sirve sobre el arroz.",
            ),
        ),
        recipe(
            "l_pechuga_rellena", "Pechuga rellena de panela y espinaca", "\uD83D\uDC14", MealSlot.LUNCH,
            520, 48, 20, 26, 30, "food_stuffed_chicken",
            listOf(
                ing("pollo", "200 g"),
                ing("queso_panela", "60 g"),
                ing("espinaca", "80 g"),
                ing("jitomate", "1 pieza"),
                ing("aceite_oliva", "1 cda"),
            ),
            listOf(
                "Abre la pechuga tipo libro y golpéala ligeramente.",
                "Rellena con espinaca salteada y queso panela.",
                "Cierra con palillos y sella en sartén por ambos lados.",
                "Termina 12 minutos en horno a 190 °C.",
            ),
        ),
        recipe(
            "l_milanesa_pavo", "Milanesa de pavo al horno con camote", "\uD83E\uDD83", MealSlot.LUNCH,
            550, 42, 52, 18, 30, "food_turkey",
            listOf(
                ing("pavo", "180 g"),
                ing("camote", "200 g"),
                ing("huevo", "1 pieza"),
                ing("avena", "40 g molida"),
                ing("lechuga", "80 g"),
            ),
            listOf(
                "Muele la avena para usarla como empanizado.",
                "Pasa el pavo por huevo batido y luego por la avena.",
                "Hornea 20 minutos a 200 °C volteando a la mitad.",
                "Acompaña con camote al horno y ensalada verde.",
            ),
        ),
    )

    private val snacks = listOf(
        recipe(
            "s_yogur_arandanos", "Yogur griego con arándanos", "\uD83E\uDED0", MealSlot.SNACK,
            160, 14, 18, 3, 3, "food_yogurt",
            listOf(ing("yogur_griego", "150 g"), ing("arandanos", "80 g"), ing("chia", "1 cdita")),
            listOf(
                "Sirve el yogur en un vaso o frasco.",
                "Agrega los arándanos encima.",
                "Espolvorea la chía y deja reposar 5 minutos.",
            ),
        ),
        recipe(
            "s_manzana_cacahuate", "Manzana con crema de cacahuate", "\uD83C\uDF4E", MealSlot.SNACK,
            220, 7, 28, 10, 3, "food_apple",
            listOf(ing("manzana", "1 pieza"), ing("cacahuate", "1 cda"), ing("chia", "1 cdita")),
            listOf(
                "Corta la manzana en gajos.",
                "Sirve la crema de cacahuate como dip.",
                "Espolvorea canela o chía al gusto.",
            ),
        ),
        recipe(
            "s_almendras_chocolate", "Almendras con chocolate amargo", "\uD83C\uDF6B", MealSlot.SNACK,
            240, 8, 16, 18, 1, "food_nuts",
            listOf(ing("almendras", "25 g"), ing("chocolate_amargo", "20 g 70%"), ing("nueces", "10 g")),
            listOf(
                "Pesa las porciones antes de empezar: es el snack más fácil de pasarse.",
                "Mézclalas en un recipiente pequeño.",
                "Guarda porciones individuales para la semana.",
            ),
        ),
        recipe(
            "s_licuado_proteina", "Licuado de proteína con plátano", "\uD83E\uDD64", MealSlot.SNACK,
            260, 28, 30, 4, 4, "food_smoothie",
            listOf(ing("proteina_polvo", "1 scoop"), ing("platano", "1 pieza"), ing("leche_almendra", "250 ml")),
            listOf(
                "Licúa todo con hielo 30 segundos.",
                "Prueba y ajusta con canela o cacao.",
                "Tómalo dentro de la hora posterior al entrenamiento.",
            ),
        ),
        recipe(
            "s_jicama", "Jícama con limón y chile", "\uD83C\uDF36\uFE0F", MealSlot.SNACK,
            90, 2, 20, 0, 5, "food_jicama",
            listOf(ing("jicama", "200 g"), ing("limon", "1 pieza"), ing("pepino", "1/2 pieza")),
            listOf(
                "Pela y corta la jícama en bastones.",
                "Agrega limón y chile en polvo.",
                "Acompaña con pepino para más volumen.",
            ),
        ),
        recipe(
            "s_hummus_zanahoria", "Hummus con bastones de zanahoria", "\uD83E\uDED8", MealSlot.SNACK,
            190, 7, 22, 9, 5, "food_hummus",
            listOf(ing("hummus", "80 g"), ing("zanahoria", "2 piezas"), ing("pepino", "1/2 pieza")),
            listOf(
                "Corta las verduras en bastones del mismo tamaño.",
                "Sirve el hummus en un bowl pequeño.",
                "Guárdalo en un táper si lo llevas al trabajo.",
            ),
        ),
        recipe(
            "s_edamame", "Edamame con sal de mar", "\uD83E\uDED8", MealSlot.SNACK,
            180, 16, 14, 7, 8, "food_edamame",
            listOf(ing("edamame", "150 g"), ing("limon", "1/2 pieza")),
            listOf(
                "Hierve el edamame 5 minutos en agua con sal.",
                "Escurre y enfría ligeramente.",
                "Sirve con limón y sal de mar.",
            ),
        ),
        recipe(
            "s_palomitas", "Palomitas naturales", "\uD83C\uDF7F", MealSlot.SNACK,
            130, 4, 24, 3, 5, "food_popcorn",
            listOf(ing("palomitas", "30 g de maíz"), ing("aceite_oliva", "1 cdita")),
            listOf(
                "Calienta una olla con tapa y el aceite.",
                "Agrega el maíz y tapa hasta que dejen de reventar.",
                "Sazona con sal o paprika en lugar de mantequilla.",
            ),
        ),
        recipe(
            "s_cottage_fresas", "Queso cottage con fresas", "\uD83E\uDDC0", MealSlot.SNACK,
            170, 18, 16, 3, 3, "food_cottage",
            listOf(ing("cottage", "150 g"), ing("fresa", "120 g"), ing("linaza", "1 cdita")),
            listOf(
                "Sirve el cottage en un bowl.",
                "Corta las fresas en cuartos y agrégalas.",
                "Termina con linaza molida.",
            ),
        ),
        recipe(
            "s_galletas_avena", "Galletas de avena caseras", "\uD83C\uDF6A", MealSlot.SNACK,
            210, 6, 30, 8, 20, "food_cookies",
            listOf(ing("galletas_avena", "2 piezas"), ing("avena", "50 g"), ing("platano", "1 pieza")),
            listOf(
                "Machaca el plátano y mézclalo con la avena.",
                "Forma discos y hornea 15 minutos a 180 °C.",
                "Deja enfriar antes de guardarlas.",
            ),
        ),
        recipe(
            "s_rollitos_pavo", "Rollitos de pavo con panela", "\uD83E\uDD83", MealSlot.SNACK,
            200, 22, 4, 11, 5, "food_turkey_roll",
            listOf(ing("pavo", "100 g en rebanadas"), ing("queso_panela", "50 g"), ing("pimiento", "1/2 pieza")),
            listOf(
                "Corta el panela y el pimiento en bastones.",
                "Envuélvelos con las rebanadas de pavo.",
                "Sujeta con un palillo y sirve frío.",
            ),
        ),
        recipe(
            "s_smoothie_verde", "Smoothie verde de espinaca y piña", "\uD83E\uDD6C", MealSlot.SNACK,
            180, 6, 34, 3, 5, "food_green_smoothie",
            listOf(ing("espinaca", "60 g"), ing("piña", "150 g"), ing("limon", "1/2 pieza"), ing("chia", "1 cdita")),
            listOf(
                "Licúa la espinaca con agua primero para evitar grumos.",
                "Agrega la piña y el limón.",
                "Sirve con la chía encima.",
            ),
        ),
    )

    private val dinners = listOf(
        recipe(
            "d_pechuga_ensalada", "Pechuga a la plancha con ensalada", "\uD83E\uDD57", MealSlot.DINNER,
            380, 45, 16, 14, 20, "food_grilled_chicken",
            listOf(
                ing("pollo", "180 g"),
                ing("espinaca", "80 g"),
                ing("jitomate", "1 pieza"),
                ing("aguacate", "1/4 pieza"),
                ing("limon", "1 pieza"),
            ),
            listOf(
                "Aplana ligeramente la pechuga para que cocine parejo.",
                "Sella 4 minutos por lado a fuego medio-alto.",
                "Deja reposar 3 minutos antes de cortar.",
                "Sirve sobre la ensalada aliñada con limón y aceite.",
            ),
        ),
        recipe(
            "d_salmon_calabaza", "Salmón al horno con calabazas", "\uD83D\uDC1F", MealSlot.DINNER,
            460, 38, 14, 28, 25, "food_salmon",
            listOf(
                ing("salmon", "170 g"),
                ing("calabaza", "200 g"),
                ing("aceite_oliva", "1 cda"),
                ing("limon", "1 pieza"),
                ing("espinaca", "60 g"),
            ),
            listOf(
                "Precalienta el horno a 200 °C.",
                "Coloca el salmón y las calabazas en rodajas en una charola.",
                "Hornea 15 minutos; el salmón debe quedar rosado al centro.",
                "Termina con limón y espinaca fresca.",
            ),
        ),
        recipe(
            "d_sopa_pollo", "Sopa de verduras con pollo", "\uD83C\uDF72", MealSlot.DINNER,
            320, 32, 26, 8, 30, "food_soup",
            listOf(
                ing("pollo", "150 g"),
                ing("zanahoria", "1 pieza"),
                ing("calabaza", "1 pieza"),
                ing("ejotes", "80 g"),
                ing("jitomate", "1 pieza"),
            ),
            listOf(
                "Cuece el pollo con cebolla y ajo; guarda el caldo.",
                "Agrega las verduras en cubos al caldo.",
                "Cocina 15 minutos hasta que estén suaves pero firmes.",
                "Deshebra el pollo y regrésalo a la sopa.",
            ),
        ),
        recipe(
            "d_claras_champinones", "Omelette de claras con champiñones", "\uD83C\uDF44", MealSlot.DINNER,
            300, 30, 8, 16, 12, "food_omelette",
            listOf(
                ing("claras", "5 piezas"),
                ing("champinones", "120 g"),
                ing("queso_panela", "30 g"),
                ing("espinaca", "50 g"),
                ing("aceite_oliva", "1 cdita"),
            ),
            listOf(
                "Dora los champiñones sin sal para que no suelten agua.",
                "Agrega sal al final junto con la espinaca.",
                "Vierte las claras y cocina a fuego bajo.",
                "Rellena con el queso y dobla.",
            ),
        ),
        recipe(
            "d_tacos_atun", "Tacos de atún con pepino", "\uD83C\uDF2E", MealSlot.DINNER,
            350, 34, 28, 10, 12, "food_tuna_tacos",
            listOf(
                ing("atun", "2 latas en agua"),
                ing("tortilla", "2 piezas"),
                ing("pepino", "1 pieza"),
                ing("yogur_griego", "2 cdas"),
                ing("limon", "1 pieza"),
            ),
            listOf(
                "Mezcla el atún escurrido con yogur, limón y cebolla morada.",
                "Calienta las tortillas en comal.",
                "Rellena y agrega el pepino en cubitos.",
                "Termina con chile y cilantro al gusto.",
            ),
        ),
        recipe(
            "d_tofu_brocoli", "Tofu al horno con brócoli", "\uD83E\uDDC8", MealSlot.DINNER,
            380, 24, 30, 18, 25, "food_tofu",
            listOf(
                ing("tofu", "200 g"),
                ing("brocoli", "180 g"),
                ing("aceite_oliva", "1 cda"),
                ing("limon", "1 pieza"),
                ing("semillas_girasol", "10 g"),
            ),
            listOf(
                "Corta el tofu en cubos y sécalo bien.",
                "Mézclalo con aceite, soya y paprika.",
                "Hornea 20 minutos a 200 °C junto con el brócoli.",
                "Sirve con limón y semillas de girasol.",
            ),
        ),
        recipe(
            "d_quinoa_pavo", "Ensalada templada de quinoa y pavo", "\uD83C\uDF3E", MealSlot.DINNER,
            430, 36, 40, 14, 20, "food_quinoa",
            listOf(
                ing("pavo", "150 g"),
                ing("quinoa", "120 g cocida"),
                ing("pimiento", "1 pieza"),
                ing("espinaca", "60 g"),
                ing("aceite_oliva", "1 cda"),
            ),
            listOf(
                "Saltea el pavo en tiras con ajo.",
                "Agrega el pimiento hasta que quede crujiente.",
                "Integra la quinoa tibia y la espinaca.",
                "Aliña con aceite de oliva, limón y pimienta.",
            ),
        ),
        recipe(
            "d_caldo_camaron", "Caldo de camarón con verduras", "\uD83E\uDD90", MealSlot.DINNER,
            330, 34, 24, 8, 25, "food_shrimp_soup",
            listOf(
                ing("camaron", "180 g"),
                ing("zanahoria", "1 pieza"),
                ing("calabaza", "1 pieza"),
                ing("jitomate", "2 piezas"),
                ing("limon", "1 pieza"),
            ),
            listOf(
                "Prepara un caldo con jitomate, ajo y cebolla licuados y colados.",
                "Agrega las verduras y cocina 10 minutos.",
                "Añade los camarones al final: 3 minutos bastan.",
                "Sirve con limón y cilantro.",
            ),
        ),
        recipe(
            "d_pescado_ejotes", "Filete de pescado con ejotes", "\uD83D\uDC1F", MealSlot.DINNER,
            360, 40, 16, 14, 20, "food_fish",
            listOf(
                ing("pescado", "200 g"),
                ing("ejotes", "150 g"),
                ing("aceite_oliva", "1 cda"),
                ing("limon", "1 pieza"),
                ing("jitomate", "1 pieza"),
            ),
            listOf(
                "Sazona el filete y sella 3 minutos por lado.",
                "Blanquea los ejotes 4 minutos y pásalos a agua con hielo.",
                "Saltéalos con ajo y jitomate.",
                "Sirve con limón abundante.",
            ),
        ),
        recipe(
            "d_frijol_nopal", "Bowl de frijoles con nopal y panela", "\uD83C\uDF35", MealSlot.DINNER,
            400, 24, 42, 16, 20, "food_beans",
            listOf(
                ing("frijoles", "180 g"),
                ing("nopal", "150 g"),
                ing("queso_panela", "50 g"),
                ing("jitomate", "1 pieza"),
                ing("aguacate", "1/4 pieza"),
            ),
            listOf(
                "Asa los nopales en comal hasta que cambien de color.",
                "Calienta los frijoles de olla con su caldo.",
                "Sirve en bowl y agrega nopal, panela y pico de gallo.",
                "Termina con aguacate y cilantro.",
            ),
        ),
        recipe(
            "d_pollo_camote", "Pollo al horno con camote", "\uD83C\uDF60", MealSlot.DINNER,
            470, 42, 44, 12, 35, "food_roast_chicken",
            listOf(
                ing("pollo", "180 g"),
                ing("camote", "200 g"),
                ing("aceite_oliva", "1 cda"),
                ing("pimiento", "1 pieza"),
                ing("limon", "1 pieza"),
            ),
            listOf(
                "Marina el pollo con limón, ajo, orégano y paprika.",
                "Corta el camote en cubos y mézclalo con aceite.",
                "Hornea todo junto 30 minutos a 200 °C.",
                "Voltea a la mitad para que dore parejo.",
            ),
        ),
        recipe(
            "d_revuelto_aguacate", "Revuelto de huevo con espinaca y aguacate", "\uD83E\uDD5A", MealSlot.DINNER,
            390, 26, 12, 27, 10, "food_scramble",
            listOf(
                ing("huevo", "3 piezas"),
                ing("espinaca", "80 g"),
                ing("aguacate", "1/2 pieza"),
                ing("jitomate", "1 pieza"),
                ing("aceite_oliva", "1 cdita"),
            ),
            listOf(
                "Saltea la espinaca hasta que reduzca.",
                "Agrega el huevo batido y mueve a fuego bajo.",
                "Retira cuando aún se vea cremoso.",
                "Sirve con aguacate y jitomate en cubos.",
            ),
        ),
    )

    val all: List<Recipe> = breakfasts + lunches + snacks + dinners

    val bySlot: Map<MealSlot, List<Recipe>> = all.groupBy { it.slot }

    fun byId(id: String): Recipe? = all.firstOrNull { it.id == id }
}
