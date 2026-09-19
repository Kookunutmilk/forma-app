package com.forma.app.ui.diet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.designsystem.ErrorState
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaCard
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaOutline
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.LoadingState
import com.forma.app.designsystem.MacroCarbs
import com.forma.app.designsystem.MacroFat
import com.forma.app.designsystem.MacroProtein
import com.forma.app.designsystem.ScreenHeader
import com.forma.app.designsystem.TagChip
import com.forma.app.domain.model.MealOptions
import com.forma.app.domain.model.Recipe
import com.forma.app.domain.model.WeekDay
import com.forma.app.ui.BottomBarSpacing

@Composable
fun DietScreen(
    onOpenRecipe: (String, Int, String) -> Unit,
    onOpenIngredients: () -> Unit,
    viewModel: DietViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(FormaBackground)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScreenHeader(
                eyebrow = "Planificación",
                title = "Mi Dieta",
                modifier = Modifier.weight(1f),
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(FormaSurface)
                    .clickable(onClick = onOpenIngredients)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "\uD83E\uDD57", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Ingredientes",
                    style = MaterialTheme.typography.titleSmall,
                    color = FormaLime,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(WeekDay.entries.toList(), key = { it.index }) { day ->
                val selected = day == state.selectedDay
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) FormaLime else FormaSurface)
                        .clickable { viewModel.selectDay(day) }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = day.short,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) FormaBackground else FormaMuted,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val plan = state.plan
        when {
            state.loading -> LoadingState(message = "La IA está armando tu menú…")

            state.error != null -> ErrorState(
                message = state.error.orEmpty(),
                onRetry = viewModel::retry,
            )

            plan == null -> Unit

            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = BottomBarSpacing,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { DayTotals(plan.totalKcal, plan.targetKcal, plan.totalProteinG, plan.totalCarbsG, plan.totalFatG) }

                items(plan.meals, key = { it.slot.id }) { meal ->
                    MealCard(
                        meal = meal,
                        expanded = state.expandedSlot == meal.slot,
                        onToggleOptions = { viewModel.toggleOptions(meal.slot) },
                        onChoose = { recipeId -> viewModel.chooseOption(meal.slot, recipeId) },
                        onOpen = {
                            onOpenRecipe(
                                meal.selected.id,
                                state.selectedDay.index,
                                meal.slot.id,
                            )
                        },
                    )
                }

                item {
                    Text(
                        text = "Las opciones se generan con los ingredientes que marcaste y tu objetivo calórico.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormaMutedSoft,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayTotals(
    kcal: Int,
    targetKcal: Int,
    protein: Int,
    carbs: Int,
    fat: Int,
) {
    FormaCard(contentPadding = PaddingValues(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "TOTAL DEL DÍA",
                    style = MaterialTheme.typography.labelSmall,
                    color = FormaMuted,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$kcal",
                        style = MaterialTheme.typography.displayMedium,
                        color = FormaLime,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormaMuted,
                    )
                }
                Text(
                    text = "Objetivo: $targetKcal kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                MacroColumn("${protein}g", "Proteína", MacroProtein)
                MacroColumn("${carbs}g", "Carbos", MacroCarbs)
                MacroColumn("${fat}g", "Grasa", MacroFat)
            }
        }
    }
}

@Composable
private fun MacroColumn(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = color)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = FormaMutedSoft)
    }
}

@Composable
private fun MealCard(
    meal: MealOptions,
    expanded: Boolean,
    onToggleOptions: () -> Unit,
    onChoose: (String) -> Unit,
    onOpen: () -> Unit,
) {
    val recipe = meal.selected
    FormaCard(contentPadding = PaddingValues(0.dp)) {
        Column(
            modifier = Modifier
                .clickable(onClick = onOpen)
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(text = recipe.emoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "${meal.slot.displayName} · ${meal.slot.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormaMutedSoft,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = FormaOnBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${recipe.kcal} kcal",
                        style = MaterialTheme.typography.titleLarge,
                        color = FormaLime,
                    )
                    Text(
                        text = "${recipe.proteinG}g prot",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormaMutedSoft,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                recipe.tags.take(4).forEach { tag -> TagChip(text = tag) }
            }

            if (meal.plateUri != null) {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoCamera,
                        contentDescription = null,
                        tint = FormaLime,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Ya subiste la foto de tu plato",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormaLime,
                    )
                }
            }
        }

        HorizontalDivider(color = FormaOutline.copy(alpha = 0.6f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleOptions)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (expanded) {
                    "OPCIONES ALTERNATIVAS"
                } else {
                    "VER ${meal.alternatives.size} OPCIONES MÁS"
                },
                style = MaterialTheme.typography.labelSmall,
                color = FormaMuted,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                contentDescription = null,
                tint = FormaMuted,
                modifier = Modifier.size(18.dp),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                meal.alternatives.forEach { option ->
                    AlternativeRow(option = option, onClick = { onChoose(option.id) })
                }
            }
        }
    }
}

@Composable
private fun AlternativeRow(option: Recipe, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(FormaSurfaceHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = option.emoji, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(10.dp))
        Text(
            text = option.title,
            style = MaterialTheme.typography.titleMedium,
            color = FormaOnBackground,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${option.kcal} kcal",
            style = MaterialTheme.typography.titleSmall,
            color = FormaLime,
        )
    }
}
