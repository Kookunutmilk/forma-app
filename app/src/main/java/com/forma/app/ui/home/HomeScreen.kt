package com.forma.app.ui.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.designsystem.ErrorState
import com.forma.app.designsystem.FormaAqua
import com.forma.app.designsystem.FormaAvatar
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaCard
import com.forma.app.designsystem.FormaImage
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaOrange
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.FormaViolet
import com.forma.app.designsystem.LoadingState
import com.forma.app.designsystem.QuickActionCard
import com.forma.app.designsystem.SectionTitle
import com.forma.app.designsystem.StatTile
import com.forma.app.domain.model.Article
import com.forma.app.domain.model.RoutineSession
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.ui.BottomBarSpacing
import com.forma.app.ui.navigation.Routes

@Composable
fun HomeScreen(
    onOpenTab: (String) -> Unit,
    onOpenArticle: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notifications by viewModel.unreadNotifications.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaBackground),
    ) {
        when (val current = state) {
            HomeUiState.Loading -> LoadingState(
                message = "Preparando tu día…",
                modifier = Modifier.align(Alignment.Center),
            )

            is HomeUiState.Error -> ErrorState(
                message = current.message,
                onRetry = viewModel::retry,
                modifier = Modifier.align(Alignment.Center),
            )

            is HomeUiState.Content -> HomeContent(
                state = current,
                notifications = notifications,
                onOpenTab = onOpenTab,
                onOpenArticle = onOpenArticle,
                onOpenProfile = onOpenProfile,
                onOpenNotifications = onOpenNotifications,
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Content,
    notifications: Int,
    onOpenTab: (String) -> Unit,
    onOpenArticle: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 12.dp,
            bottom = BottomBarSpacing,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            GreetingRow(
                greeting = state.greeting,
                profile = state.profile,
                notifications = notifications,
                onOpenProfile = onOpenProfile,
                onOpenNotifications = onOpenNotifications,
            )
        }

        item {
            WeekStrip(today = state.today, completed = state.stats.completedDays)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    value = "${state.stats.workoutsThisWeek}",
                    label = "entrenamientos",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = "${state.stats.streakDays}",
                    label = "días seguidos",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = "${state.stats.kcalThisWeek}",
                    label = "kcal",
                    modifier = Modifier.weight(1f),
                    accent = FormaLime,
                )
            }
        }

        item {
            TodayCard(
                profile = state.profile,
                session = state.todaySession,
                onOpenRoutine = { onOpenTab(Routes.ROUTINE) },
            )
        }

        item { ProfileCard(profile = state.profile, onClick = onOpenProfile) }

        item { SectionTitle(text = "Acceso rápido") }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(
                        emoji = "\uD83E\uDD57",
                        label = "Planificar dieta",
                        tint = FormaLime,
                        onClick = { onOpenTab(Routes.DIET) },
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionCard(
                        emoji = "\uD83D\uDCF8",
                        label = "Ver comunidad",
                        tint = FormaAqua,
                        onClick = { onOpenTab(Routes.COMMUNITY) },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(
                        emoji = "\uD83E\uDD16",
                        label = "Preguntar a la IA",
                        tint = FormaViolet,
                        onClick = { onOpenTab(Routes.AI) },
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionCard(
                        emoji = "\uD83D\uDCDA",
                        label = "Leer artículos",
                        tint = FormaOrange,
                        onClick = { onOpenTab(Routes.LEARN) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            SectionTitle(
                text = "Para ti hoy",
                actionLabel = "Ver todo",
                onAction = { onOpenTab(Routes.LEARN) },
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.articles, key = { it.id }) { article ->
                    ArticleTeaser(article = article, onClick = { onOpenArticle(article.id) })
                }
            }
        }
    }
}

@Composable
private fun GreetingRow(
    greeting: String,
    profile: UserProfile,
    notifications: Int,
    onOpenProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = FormaMuted,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.displaySmall,
                    color = FormaOnBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(8.dp))
                Text(text = profile.sport.emoji, style = MaterialTheme.typography.headlineSmall)
            }
        }

        Spacer(Modifier.width(12.dp))

        FormaAvatar(
            name = profile.name,
            photo = profile.photoUri,
            size = 44.dp,
            ring = true,
            modifier = Modifier.clickable(onClick = onOpenProfile),
        )
        Spacer(Modifier.width(10.dp))
        Box {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(FormaSurface)
                    .clickable(onClick = onOpenNotifications),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notificaciones",
                    tint = FormaOnBackground,
                    modifier = Modifier.size(20.dp),
                )
            }
            if (notifications > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(FormaLime),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$notifications",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                        color = FormaBackground,
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(today: WeekDay, completed: Set<WeekDay>) {
    FormaCard(contentPadding = PaddingValues(vertical = 14.dp, horizontal = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            WeekDay.entries.forEach { day ->
                val isToday = day == today
                val isDone = day in completed
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day.letter,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isToday) FormaLime else FormaMutedSoft,
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isToday -> FormaLime
                                    isDone -> FormaSurfaceHigh
                                    else -> FormaSurfaceHigh.copy(alpha = 0.45f)
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            isToday -> Box(
                                Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(FormaBackground),
                            )

                            isDone -> Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = FormaLime,
                                modifier = Modifier.size(14.dp),
                            )

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayCard(
    profile: UserProfile,
    session: RoutineSession?,
    onOpenRoutine: () -> Unit,
) {
    val active = session?.takeIf { !it.isRest }
    val resting = active == null
    FormaCard(
        color = if (resting) FormaSurface else FormaLime,
        contentPadding = PaddingValues(20.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "HOY",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (resting) FormaMuted else FormaBackground.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (resting) {
                        "Día de descanso"
                    } else {
                        "Entrenamiento de ${profile.sport.displayName}"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (resting) FormaOnBackground else FormaBackground,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (active == null) {
                        session?.focus ?: "Aprovecha para estirar y dormir bien"
                    } else {
                        "${active.title} · ${profile.level.displayName} · ~${active.estimatedMinutes} min"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (resting) FormaMuted else FormaBackground.copy(alpha = 0.75f),
                )
            }
            Text(
                text = profile.sport.emoji,
                style = MaterialTheme.typography.displayMedium,
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(if (resting) FormaSurfaceHigh else FormaBackground)
                .clickable(onClick = onOpenRoutine)
                .padding(horizontal = 18.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (resting) "Ver la semana" else "Ver rutina",
                style = MaterialTheme.typography.labelLarge,
                color = if (resting) FormaOnBackground else FormaLime,
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = if (resting) FormaOnBackground else FormaLime,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun ProfileCard(profile: UserProfile, onClick: () -> Unit) {
    FormaCard(onClick = onClick, contentPadding = PaddingValues(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Tu perfil",
                style = MaterialTheme.typography.headlineSmall,
                color = FormaOnBackground,
            )
            Text(
                text = "${profile.weightKg} kg · ${profile.heightCm} cm",
                style = MaterialTheme.typography.bodyMedium,
                color = FormaMuted,
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MiniTile(
                modifier = Modifier.weight(1f),
                headline = "${profile.bmi}",
                headlineColor = FormaLime,
                caption = "IMC",
                footer = profile.bmiLabel,
            )
            MiniTile(
                modifier = Modifier.weight(1f),
                headline = profile.sport.emoji,
                headlineColor = FormaOnBackground,
                caption = profile.sport.displayName,
                footer = profile.level.displayName,
            )
        }
    }
}

@Composable
private fun MiniTile(
    modifier: Modifier,
    headline: String,
    headlineColor: androidx.compose.ui.graphics.Color,
    caption: String,
    footer: String,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(FormaSurfaceHigh)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineMedium,
            color = headlineColor,
        )
        Spacer(Modifier.height(4.dp))
        Text(text = caption, style = MaterialTheme.typography.bodySmall, color = FormaMutedSoft)
        Text(
            text = footer,
            style = MaterialTheme.typography.titleSmall,
            color = FormaAqua,
        )
    }
}

@Composable
private fun ArticleTeaser(article: Article, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(212.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(FormaSurface)
            .clickable(onClick = onClick),
    ) {
        FormaImage(
            key = article.imageKey,
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp),
            emojiSize = 40,
        )
        Column(Modifier.padding(12.dp)) {
            Text(
                text = article.category.displayName,
                style = MaterialTheme.typography.titleSmall,
                color = FormaLime,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleLarge,
                color = FormaOnBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${article.readMinutes} min de lectura",
                style = MaterialTheme.typography.bodySmall,
                color = FormaMutedSoft,
            )
        }
    }
}

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val items = listOf(
        Triple("\uD83D\uDD25", "Llevas 3 días seguidos", "No rompas la racha: hoy toca tu sesión."),
        Triple("\uD83E\uDD57", "Tu plan de comidas está listo", "Revisa las 5 opciones de cada comida."),
        Triple("\uD83D\uDC4F", "Carlos R. reaccionó a tu publicación", "Y otras 4 personas más."),
        Triple("\uD83D\uDCDA", "Nuevo artículo en Aprende", "Sobrecarga progresiva sin estancarte."),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(12.dp))
        BackRow(label = "Notificaciones", onBack = onBack)
        Spacer(Modifier.height(20.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { (emoji, title, body) ->
                FormaCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FormaSurfaceHigh),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = emoji, style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                color = FormaOnBackground,
                            )
                            Text(
                                text = body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = FormaMuted,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackRow(label: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onBack)
            .padding(vertical = 6.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "←", style = MaterialTheme.typography.headlineSmall, color = FormaLime)
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = FormaLime,
        )
    }
}