package com.forma.app.domain.model

enum class IngredientCategory(val id: String, val displayName: String, val emoji: String) {
    PROTEIN("protein", "Proteína", "\uD83C\uDF57"),
    CARBS("carbs", "Carbohidratos", "\uD83C\uDF5A"),
    FIBER("fiber", "Fibra y verduras", "\uD83E\uDD66"),
    FAT("fat", "Grasas buenas", "\uD83E\uDD51"),
    FRUIT("fruit", "Frutas", "\uD83C\uDF53"),
    DAIRY("dairy", "Lácteos", "\uD83E\uDDC0"),
    SNACK("snack", "Snacks", "\uD83C\uDF6B");

    companion object {
        fun fromId(id: String?): IngredientCategory =
            entries.firstOrNull { it.id == id } ?: PROTEIN
    }
}

data class Ingredient(
    val id: String,
    val name: String,
    val emoji: String,
    val category: IngredientCategory,
)

enum class MealSlot(val id: String, val displayName: String, val time: String, val share: Double) {
    BREAKFAST("breakfast", "Desayuno", "7:00 AM", 0.26),
    LUNCH("lunch", "Almuerzo", "1:00 PM", 0.36),
    SNACK("snack", "Snack", "4:00 PM", 0.12),
    DINNER("dinner", "Cena", "7:30 PM", 0.26);

    companion object {
        fun fromId(id: String?): MealSlot = entries.firstOrNull { it.id == id } ?: BREAKFAST
    }
}

data class RecipeIngredient(
    val name: String,
    val amount: String,
    val ingredientId: String? = null,
)

data class Recipe(
    val id: String,
    val title: String,
    val emoji: String,
    val slot: MealSlot,
    val kcal: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val minutes: Int,
    val ingredients: List<RecipeIngredient>,
    val steps: List<String>,
    val imageKey: String? = null,
) {
    val tags: List<String> get() = ingredients.take(4).map { it.name }
}

data class MealOptions(
    val slot: MealSlot,
    val options: List<Recipe>,
    val selectedId: String,
    val plateUri: String? = null,
) {
    val selected: Recipe
        get() = options.firstOrNull { it.id == selectedId } ?: options.first()

    val alternatives: List<Recipe>
        get() = options.filter { it.id != selectedId }
}

data class DayMealPlan(
    val day: WeekDay,
    val meals: List<MealOptions>,
    val targetKcal: Int,
) {
    val totalKcal: Int get() = meals.sumOf { it.selected.kcal }
    val totalProteinG: Int get() = meals.sumOf { it.selected.proteinG }
    val totalCarbsG: Int get() = meals.sumOf { it.selected.carbsG }
    val totalFatG: Int get() = meals.sumOf { it.selected.fatG }
}
