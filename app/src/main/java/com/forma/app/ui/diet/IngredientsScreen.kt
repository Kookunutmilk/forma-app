package com.forma.app.ui.diet

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.data.catalog.IngredientCatalog
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.LoadingState
import com.forma.app.designsystem.PrimaryButton
import com.forma.app.domain.model.IngredientCategory
import com.forma.app.ui.home.BackRow

@Composable
fun IngredientsScreen(
    onBack: () -> Unit,
    viewModel: IngredientsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(FormaBackground)
            .statusBarsPadding(),
    ) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(12.dp))
            BackRow(label = "Mi dieta", onBack = onBack)
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Tus ingredientes",
                style = MaterialTheme.typography.displayMedium,
                color = FormaOnBackground,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Marca lo que te gusta comer. Tu menú se arma solo con estos ingredientes.",
                style = MaterialTheme.typography.bodyLarge,
                color = FormaMuted,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${state.selected.size} seleccionados",
                style = MaterialTheme.typography.titleSmall,
                color = FormaLime,
            )
        }

        if (state.loading) {
            LoadingState(message = "Cargando tus ingredientes…")
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(IngredientCategory.entries.toList(), key = { it.id }) { category ->
                Column {
                    Text(
                        text = "${category.emoji}  ${category.displayName.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FormaMuted,
                    )
                    Spacer(Modifier.height(12.dp))
                    IngredientCatalog.byCategory[category].orEmpty().chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            row.forEach { ingredient ->
                                val selected = ingredient.id in state.selected
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (selected) FormaLime else FormaSurface)
                                        .clickable { viewModel.toggle(ingredient.id) }
                                        .padding(horizontal = 14.dp, vertical = 13.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = FormaBackground,
                                            modifier = Modifier.size(15.dp),
                                        )
                                        Spacer(Modifier.width(6.dp))
                                    } else {
                                        Text(
                                            text = ingredient.emoji,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Spacer(Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = ingredient.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (selected) FormaBackground else FormaMuted,
                                        maxLines = 2,
                                    )
                                }
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Necesitas al menos 3 ingredientes para poder generar el menú.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            PrimaryButton(
                text = "Guardar y regenerar menú",
                onClick = { viewModel.save(onBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.selected.size >= 3,
                loading = state.saving,
            )
        }
    }
}
