package com.forma.app.data.catalog

import com.forma.app.domain.model.Ingredient
import com.forma.app.domain.model.IngredientCategory as Cat

object IngredientCatalog {

    val all: List<Ingredient> = listOf(
        // Proteína
        Ingredient("pollo", "Pollo", "\uD83D\uDC14", Cat.PROTEIN),
        Ingredient("res", "Res magra", "\uD83E\uDD69", Cat.PROTEIN),
        Ingredient("cerdo", "Lomo de cerdo", "\uD83E\uDD53", Cat.PROTEIN),
        Ingredient("pescado", "Pescado blanco", "\uD83D\uDC1F", Cat.PROTEIN),
        Ingredient("salmon", "Salmón", "\uD83C\uDF63", Cat.PROTEIN),
        Ingredient("atun", "Atún", "\uD83D\uDC20", Cat.PROTEIN),
        Ingredient("huevo", "Huevo", "\uD83E\uDD5A", Cat.PROTEIN),
        Ingredient("claras", "Claras de huevo", "\uD83C\uDF73", Cat.PROTEIN),
        Ingredient("camaron", "Camarón", "\uD83E\uDD90", Cat.PROTEIN),
        Ingredient("tofu", "Tofu", "\uD83E\uDDC8", Cat.PROTEIN),
        Ingredient("proteina_polvo", "Proteína en polvo", "\uD83E\uDD64", Cat.PROTEIN),
        Ingredient("pavo", "Pavo", "\uD83E\uDD83", Cat.PROTEIN),

        // Carbohidratos
        Ingredient("arroz", "Arroz", "\uD83C\uDF5A", Cat.CARBS),
        Ingredient("arroz_integral", "Arroz integral", "\uD83C\uDF5A", Cat.CARBS),
        Ingredient("avena", "Avena", "\uD83E\uDD63", Cat.CARBS),
        Ingredient("pasta", "Pasta integral", "\uD83C\uDF5D", Cat.CARBS),
        Ingredient("papa", "Papa", "\uD83E\uDD54", Cat.CARBS),
        Ingredient("camote", "Camote", "\uD83C\uDF60", Cat.CARBS),
        Ingredient("tortilla", "Tortilla de maíz", "\uD83C\uDF2E", Cat.CARBS),
        Ingredient("pan_integral", "Pan integral", "\uD83C\uDF5E", Cat.CARBS),
        Ingredient("quinoa", "Quinoa", "\uD83C\uDF3E", Cat.CARBS),
        Ingredient("frijoles", "Frijoles", "\uD83E\uDED8", Cat.CARBS),
        Ingredient("lentejas", "Lentejas", "\uD83E\uDED5", Cat.CARBS),
        Ingredient("elote", "Elote", "\uD83C\uDF3D", Cat.CARBS),

        // Fibra y verduras
        Ingredient("brocoli", "Brócoli", "\uD83E\uDD66", Cat.FIBER),
        Ingredient("espinaca", "Espinaca", "\uD83E\uDD6C", Cat.FIBER),
        Ingredient("lechuga", "Lechuga", "\uD83E\uDD57", Cat.FIBER),
        Ingredient("jitomate", "Jitomate", "\uD83C\uDF45", Cat.FIBER),
        Ingredient("calabaza", "Calabaza", "\uD83C\uDF83", Cat.FIBER),
        Ingredient("zanahoria", "Zanahoria", "\uD83E\uDD55", Cat.FIBER),
        Ingredient("nopal", "Nopal", "\uD83C\uDF35", Cat.FIBER),
        Ingredient("pimiento", "Pimiento", "\uD83E\uDED1", Cat.FIBER),
        Ingredient("champinones", "Champiñones", "\uD83C\uDF44", Cat.FIBER),
        Ingredient("pepino", "Pepino", "\uD83E\uDD52", Cat.FIBER),
        Ingredient("ejotes", "Ejotes", "\uD83E\uDED8", Cat.FIBER),

        // Grasas buenas
        Ingredient("aguacate", "Aguacate", "\uD83E\uDD51", Cat.FAT),
        Ingredient("aceite_oliva", "Aceite de oliva", "\uD83E\uDED2", Cat.FAT),
        Ingredient("almendras", "Almendras", "\uD83C\uDF30", Cat.FAT),
        Ingredient("nueces", "Nueces", "\uD83E\uDD5C", Cat.FAT),
        Ingredient("cacahuate", "Crema de cacahuate", "\uD83E\uDD5C", Cat.FAT),
        Ingredient("chia", "Chía", "\u26AB", Cat.FAT),
        Ingredient("linaza", "Linaza", "\uD83D\uDFE4", Cat.FAT),
        Ingredient("semillas_girasol", "Semillas de girasol", "\uD83C\uDF3B", Cat.FAT),

        // Frutas
        Ingredient("platano", "Plátano", "\uD83C\uDF4C", Cat.FRUIT),
        Ingredient("fresa", "Fresas", "\uD83C\uDF53", Cat.FRUIT),
        Ingredient("arandanos", "Arándanos", "\uD83E\uDED0", Cat.FRUIT),
        Ingredient("manzana", "Manzana", "\uD83C\uDF4E", Cat.FRUIT),
        Ingredient("mango", "Mango", "\uD83E\uDD6D", Cat.FRUIT),
        Ingredient("papaya", "Papaya", "\uD83C\uDF48", Cat.FRUIT),
        Ingredient("naranja", "Naranja", "\uD83C\uDF4A", Cat.FRUIT),
        Ingredient("piña", "Piña", "\uD83C\uDF4D", Cat.FRUIT),
        Ingredient("limon", "Limón", "\uD83C\uDF4B", Cat.FRUIT),

        // Lácteos
        Ingredient("yogur_griego", "Yogur griego", "\uD83E\uDD5B", Cat.DAIRY),
        Ingredient("leche", "Leche", "\uD83E\uDD5B", Cat.DAIRY),
        Ingredient("queso_panela", "Queso panela", "\uD83E\uDDC0", Cat.DAIRY),
        Ingredient("cottage", "Queso cottage", "\uD83E\uDDC0", Cat.DAIRY),
        Ingredient("leche_almendra", "Leche de almendra", "\uD83E\uDD5B", Cat.DAIRY),

        // Snacks
        Ingredient("chocolate_amargo", "Chocolate amargo", "\uD83C\uDF6B", Cat.SNACK),
        Ingredient("palomitas", "Palomitas naturales", "\uD83C\uDF7F", Cat.SNACK),
        Ingredient("galletas_avena", "Galletas de avena", "\uD83C\uDF6A", Cat.SNACK),
        Ingredient("hummus", "Hummus", "\uD83E\uDED8", Cat.SNACK),
        Ingredient("edamame", "Edamame", "\uD83E\uDED8", Cat.SNACK),
        Ingredient("jicama", "Jícama con chile", "\uD83C\uDF36\uFE0F", Cat.SNACK),
    )

    val byCategory: Map<Cat, List<Ingredient>> = all.groupBy { it.category }

    fun name(id: String): String = all.firstOrNull { it.id == id }?.name ?: id

    /** Selección inicial razonable para que el plan funcione desde el primer día. */
    val defaultSelection: Set<String> = setOf(
        "pollo", "huevo", "yogur_griego", "atun", "proteina_polvo",
        "arroz", "avena", "tortilla", "papa", "frijoles",
        "brocoli", "espinaca", "jitomate", "lechuga",
        "aguacate", "aceite_oliva", "almendras",
        "platano", "fresa", "manzana", "limon",
        "chocolate_amargo",
    )
}
