package com.forma.app.ui.community

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.designsystem.EmptyState
import com.forma.app.designsystem.FormaAvatar
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaCard
import com.forma.app.designsystem.FormaDanger
import com.forma.app.designsystem.FormaImage
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaLimeSoft
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.LoadingState
import com.forma.app.designsystem.ScreenHeader
import com.forma.app.domain.model.Post
import com.forma.app.domain.model.Reaction
import com.forma.app.domain.model.Sport
import com.forma.app.ui.BottomBarSpacing
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onOpenProfile: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val following by viewModel.followingCount.collectAsStateWithLifecycle()
    val sports by viewModel.sportsWithPosts.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()

    // Al publicar desde "Finalizar rutina" la lista conserva su posición y la publicación nueva
    // queda arriba del área visible, así que la traemos al frente.
    val newestPostId = state.posts.firstOrNull()?.id
    LaunchedEffect(newestPostId) {
        if (state.posts.firstOrNull()?.author?.isMe == true) listState.animateScrollToItem(0)
    }

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
                eyebrow = "Comunidad",
                title = "Feed",
                modifier = Modifier.weight(1f),
            )
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.clickable(onClick = onOpenProfile),
            ) {
                Text(
                    text = "$following",
                    style = MaterialTheme.typography.headlineSmall,
                    color = FormaLime,
                )
                Text(
                    text = "siguiendo",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            item {
                SportFilter(
                    label = "Todo",
                    emoji = null,
                    selected = state.selectedSport == null,
                    onClick = { viewModel.selectSport(null) },
                )
            }
            items(sports, key = { it.id }) { sport ->
                SportFilter(
                    label = sport.displayName,
                    emoji = sport.emoji,
                    selected = state.selectedSport == sport,
                    onClick = { viewModel.selectSport(sport) },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            state.loading -> LoadingState(message = "Cargando el feed…")

            state.posts.isEmpty() -> EmptyState(
                title = "Todavía no hay publicaciones",
                message = "Termina una rutina y sube tu foto: será la primera del feed.",
            )

            else -> LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = BottomBarSpacing,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLike = { viewModel.toggleLike(post.id) },
                        onReact = { viewModel.openReactions(post.id) },
                        onFollow = { viewModel.toggleFollow(post.author.id) },
                        onShare = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${post.author.name} en FORMA: ${post.caption}",
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(intent, "Compartir publicación"),
                            )
                        },
                    )
                }
            }
        }
    }

    val sheetPost = state.posts.firstOrNull { it.id == state.reactionSheetPostId }
    if (sheetPost != null) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeReactions,
            sheetState = sheetState,
            containerColor = FormaSurface,
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "Reacciona a la publicación",
                    style = MaterialTheme.typography.headlineSmall,
                    color = FormaOnBackground,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "En FORMA no hay comentarios: solo apoyo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FormaMuted,
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Reaction.entries.forEach { reaction ->
                        val active = sheetPost.myReaction == reaction.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (active) FormaLimeSoft else FormaSurfaceHigh)
                                .clickable { viewModel.react(sheetPost.id, reaction) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                        ) {
                            Text(
                                text = reaction.emoji,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = reaction.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (active) FormaLime else FormaMutedSoft,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SportFilter(
    label: String,
    emoji: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) FormaLime else FormaSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (emoji != null) {
            Text(text = emoji, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) FormaBackground else FormaMuted,
        )
    }
}

@Composable
private fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onReact: () -> Unit,
    onFollow: () -> Unit,
    onShare: () -> Unit,
) {
    FormaCard(contentPadding = PaddingValues(0.dp)) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FormaAvatar(name = post.author.name, photo = post.author.photo, size = 40.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = post.author.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = FormaOnBackground,
                )
                Text(
                    text = relativeTime(post.createdAtMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )
            }
            if (!post.author.isMe) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (post.author.following) FormaSurfaceHigh else FormaLimeSoft)
                        .clickable(onClick = onFollow)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = if (post.author.following) "Siguiendo" else "+ Seguir",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (post.author.following) FormaMuted else FormaLime,
                    )
                }
            }
        }

        Box {
            FormaImage(
                key = post.imageKey,
                uri = post.imageUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f),
                emojiSize = 72,
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(FormaBackground.copy(alpha = 0.75f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = post.sport.emoji, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(5.dp))
                Text(
                    text = post.sport.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = FormaLime,
                )
            }
        }

        Column(Modifier.padding(14.dp)) {
            Text(
                text = post.caption,
                style = MaterialTheme.typography.bodyLarge,
                color = FormaOnBackground,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )

            AnimatedVisibility(visible = post.reactions.values.any { it > 0 }) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    post.reactions.filterValues { it > 0 }.entries.take(4).forEach { entry ->
                        val reaction = Reaction.fromId(entry.key) ?: return@forEach
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (post.myReaction == reaction.id) {
                                        FormaLimeSoft
                                    } else {
                                        FormaSurfaceHigh
                                    },
                                )
                                .clickable(onClick = onReact)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = reaction.emoji, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "${entry.value}",
                                style = MaterialTheme.typography.titleSmall,
                                color = FormaMuted,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.clickable(onClick = onLike),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (post.likedByMe) {
                            Icons.Rounded.Favorite
                        } else {
                            Icons.Rounded.FavoriteBorder
                        },
                        contentDescription = "Me gusta",
                        tint = if (post.likedByMe) FormaDanger else FormaMuted,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${post.likes}",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormaMuted,
                    )
                }

                Spacer(Modifier.width(20.dp))

                Row(
                    modifier = Modifier.clickable(onClick = onReact),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "\uD83D\uDE0A", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Reaccionar",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormaMuted,
                    )
                }

                Spacer(Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = "Compartir",
                    tint = FormaMuted,
                    modifier = Modifier
                        .clickable(onClick = onShare)
                        .size(20.dp),
                )
            }
        }
    }
}

fun relativeTime(millis: Long): String {
    val diff = (System.currentTimeMillis() - millis).coerceAtLeast(0)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        minutes < 1 -> "hace un momento"
        minutes < 60 -> "hace $minutes min"
        hours < 24 -> "hace $hours h"
        days == 1L -> "ayer"
        days < 7 -> "hace $days días"
        else -> "hace ${days / 7} semanas"
    }
}
