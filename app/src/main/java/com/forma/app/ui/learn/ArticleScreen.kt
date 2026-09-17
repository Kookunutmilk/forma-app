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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.designsystem.ErrorState
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaCard
import com.forma.app.designsystem.FormaImage
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaOutline
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.LoadingState
import com.forma.app.domain.model.Article
import com.forma.app.ui.home.BackRow

@Composable
fun ArticleScreen(
    onBack: () -> Unit,
    onOpenArticle: (String) -> Unit = {},
    viewModel: ArticleViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val article = state.article

    Box(
        Modifier
            .fillMaxSize()
            .background(FormaBackground),
    ) {
        when {
            state.loading -> LoadingState(
                message = "Abriendo el artículo…",
                modifier = Modifier.align(Alignment.Center),
            )

            article == null -> ErrorState(
                message = "No encontramos este artículo. Vuelve a la lista e intenta con otro.",
                modifier = Modifier.align(Alignment.Center),
                onRetry = onBack,
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 40.dp),
            ) {
                item {
                    Box {
                        FormaImage(
                            key = article.imageKey,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            emojiSize = 76,
                        )
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .background(
                                    Brush.verticalGradient(
                                        0f to FormaBackground.copy(alpha = 0.85f),
                                        0.4f to Color.Transparent,
                                        1f to FormaBackground,
                                    ),
                                ),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BackRow(label = "Volver", onBack = onBack)
                            Spacer(Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(FormaBackground.copy(alpha = 0.6f))
                                    .clickable { viewModel.toggleSaved() }
                                    .padding(10.dp),
                            ) {
                                Icon(
                                    imageVector = if (article.saved) {
                                        Icons.Rounded.Bookmark
                                    } else {
                                        Icons.Rounded.BookmarkBorder
                                    },
                                    contentDescription = if (article.saved) {
                                        "Quitar de guardados"
                                    } else {
                                        "Guardar"
                                    },
                                    tint = if (article.saved) FormaLime else FormaOnBackground,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }

                item {
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(FormaSurfaceHigh)
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            ) {
                                Text(
                                    text = article.category.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = FormaLime,
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "${article.readMinutes} min de lectura",
                                style = MaterialTheme.typography.bodySmall,
                                color = FormaMutedSoft,
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.displaySmall,
                            color = FormaOnBackground,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = article.summary,
                            style = MaterialTheme.typography.bodyLarge,
                            color = FormaMuted,
                        )
                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider(color = FormaOutline)
                        Spacer(Modifier.height(20.dp))
                    }
                }

                items(article.sections) { section ->
                    Column(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                        Text(
                            text = section.heading.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = FormaLime,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = section.body,
                            style = MaterialTheme.typography.bodyLarge,
                            color = FormaOnBackground.copy(alpha = 0.88f),
                        )
                    }
                }

                if (state.related.isNotEmpty()) {
                    item {
                        Column(Modifier.padding(horizontal = 20.dp)) {
                            Spacer(Modifier.height(22.dp))
                            HorizontalDivider(color = FormaOutline)
                            Spacer(Modifier.height(20.dp))
                            Text(
                                text = "Sigue leyendo",
                                style = MaterialTheme.typography.headlineSmall,
                                color = FormaOnBackground,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                    items(state.related, key = { it.id }) { related ->
                        RelatedRow(
                            article = related,
                            onClick = { onOpenArticle(related.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        )
                    }
                }

                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }
}

@Composable
private fun RelatedRow(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormaCard(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FormaImage(
                key = article.imageKey,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp)),
                emojiSize = 24,
            )
            Spacer(Modifier.width(12.dp))
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = FormaOnBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${article.readMinutes} min · ${article.category.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )
            }
        }
    }
}
