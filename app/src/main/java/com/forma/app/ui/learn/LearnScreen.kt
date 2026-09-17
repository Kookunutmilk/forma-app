package com.forma.app.ui.learn

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.designsystem.EmptyState
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaCard
import com.forma.app.designsystem.FormaImage
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.FormaTextField
import com.forma.app.designsystem.LoadingState
import com.forma.app.designsystem.ScreenHeader
import com.forma.app.domain.model.Article
import com.forma.app.domain.model.ArticleCategory
import com.forma.app.ui.BottomBarSpacing

@Composable
fun LearnScreen(
    onOpenArticle: (String) -> Unit,
    viewModel: LearnViewModel = hiltViewModel(),
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
            ScreenHeader(
                eyebrow = "Conocimiento",
                title = "Aprende",
                trailing = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (state.onlySaved) FormaLime else FormaSurfaceHigh,
                            )
                            .clickable { viewModel.toggleOnlySaved() }
                            .padding(12.dp),
                    ) {
                        Icon(
                            imageVector = if (state.onlySaved) {
                                Icons.Rounded.Bookmark
                            } else {
                                Icons.Rounded.BookmarkBorder
                            },
                            contentDescription = "Guardados",
                            tint = if (state.onlySaved) FormaBackground else FormaMuted,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
            )
            Spacer(Modifier.height(16.dp))
            FormaTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                placeholder = "Busca por tema o palabra clave",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = FormaMutedSoft,
                    )
                },
            )
        }

        Spacer(Modifier.height(14.dp))

        CategoryRow(
            selected = state.category,
            onSelect = viewModel::selectCategory,
        )

        Spacer(Modifier.height(16.dp))

        when {
            state.loading -> LoadingState(message = "Cargando artículos…")

            state.featured == null && state.articles.isEmpty() -> EmptyState(
                title = if (state.onlySaved) {
                    "Aún no guardas artículos"
                } else {
                    "Sin resultados"
                },
                message = if (state.onlySaved) {
                    "Toca el marcador en cualquier artículo para leerlo después."
                } else {
                    "Prueba con otra palabra o quita los filtros para ver todo el catálogo."
                },
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = BottomBarSpacing,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                state.featured?.let { featured ->
                    item(key = featured.id) {
                        FeaturedArticleCard(
                            article = featured,
                            onClick = { onOpenArticle(featured.id) },
                        )
                    }
                }
                items(state.articles, key = { it.id }) { article ->
                    ArticleRow(
                        article = article,
                        onClick = { onOpenArticle(article.id) },
                        onToggleSaved = { viewModel.toggleSaved(article.id) },
                    )
                }
                item {
                    Text(
                        text = "Contenido revisado por el equipo de FORMA.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormaMutedSoft,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    selected: ArticleCategory?,
    onSelect: (ArticleCategory?) -> Unit,
) {
    androidx.compose.foundation.lazy.LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        item {
            CategoryChip(
                label = "Todos",
                selected = selected == null,
                onClick = { onSelect(null) },
            )
        }
        items(ArticleCategory.entries, key = { it.id }) { category ->
            CategoryChip(
                label = category.displayName,
                selected = selected == category,
                onClick = { onSelect(category) },
            )
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) FormaLime else FormaSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) FormaBackground else FormaMuted,
        )
    }
}

@Composable
private fun FeaturedArticleCard(article: Article, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
    ) {
        FormaImage(
            key = article.imageKey,
            modifier = Modifier.fillMaxSize(),
            emojiSize = 64,
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.45f to FormaBackground.copy(alpha = 0.55f),
                        1f to FormaBackground.copy(alpha = 0.95f),
                    ),
                ),
        )
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(FormaLime)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "DESTACADO",
                    style = MaterialTheme.typography.labelSmall,
                    color = FormaBackground,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineMedium,
                color = FormaOnBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${article.readMinutes} min de lectura",
                style = MaterialTheme.typography.bodyMedium,
                color = FormaMuted,
            )
        }
    }
}

@Composable
private fun ArticleRow(
    article: Article,
    onClick: () -> Unit,
    onToggleSaved: () -> Unit,
) {
    FormaCard(onClick = onClick, contentPadding = PaddingValues(12.dp)) {
        Row {
            FormaImage(
                key = article.imageKey,
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(16.dp)),
                emojiSize = 34,
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = article.category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = FormaLime,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = FormaOnBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${article.readMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )
            }
            Icon(
                imageVector = if (article.saved) {
                    Icons.Rounded.Bookmark
                } else {
                    Icons.Rounded.BookmarkBorder
                },
                contentDescription = if (article.saved) "Quitar de guardados" else "Guardar",
                tint = if (article.saved) FormaLime else FormaMutedSoft,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onToggleSaved)
                    .padding(6.dp)
                    .size(20.dp),
            )
        }
    }
}
