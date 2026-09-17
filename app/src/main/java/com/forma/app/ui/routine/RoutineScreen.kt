package com.forma.app.ui.routine

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.data.repository.exerciseKey
import com.forma.app.designsystem.EmptyState
import com.forma.app.designsystem.ErrorState
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaCard
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaLimeSoft
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaProgressBar
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.LoadingState
import com.forma.app.designsystem.PrimaryButton
import com.forma.app.designsystem.ScreenHeader
import com.forma.app.designsystem.TagChip
import com.forma.app.domain.model.Exercise
import com.forma.app.domain.model.RoutineSession
import com.forma.app.domain.model.WeekDay
import com.forma.app.ui.BottomBarSpacing
import kotlin.math.roundToInt

@Composable
fun RoutineScreen(
    onFinishWorkout: (Int) -> Unit,
    viewModel: RoutineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        Modifier
            .fillMaxSize()
            .background(FormaBackground),
    ) {
        when (val current = state) {
            RoutineUiState.Loading -> LoadingState(
                message = "Armando tu rutina semanal…",
                modifier = Modifier.align(Alignment.Center),
            )

            is RoutineUiState.Error -> ErrorState(
                message = current.message,
                onRetry = viewModel::retry,
                modifier = Modifier.align(Alignment.Center),
            )

            is RoutineUiState.Content -> {
                RoutineContent(
                    state = current,
                    viewModel = viewModel,
                    onFinishWorkout = onFinishWorkout,
                )
                current.restTimer?.let { timer ->
                    FloatingRestTimer(
                        timer = timer,
                        onStop = viewModel::stopTimer,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 20.dp)
                            .padding(bottom = BottomBarSpacing - 16.dp),
                    )
                }
            }
        }
    }
}

/** Mientras corre el descanso el temporizador flota sobre la lista para no perderlo de vista. */
@Composable
private fun FloatingRestTimer(
    timer: RestTimer,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormaCard(
        modifier = modifier,
        color = FormaLimeSoft,
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = null,
                tint = FormaLime,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Descanso",
                    style = MaterialTheme.typography.titleMedium,
                    color = FormaLime,
                )
                Text(
                    text = formatSeconds(timer.remainingSeconds),
                    style = MaterialTheme.typography.headlineSmall,
                    color = FormaOnBackground,
                )
            }
            TextButton(onClick = onStop) {
                Text("Detener", color = FormaMuted, style = MaterialTheme.typography.titleSmall)
            }
        }
        Spacer(Modifier.height(10.dp))
        FormaProgressBar(progress = timer.progress)
    }
}

@Composable
private fun RoutineContent(
    state: RoutineUiState.Content,
    viewModel: RoutineViewModel,
    onFinishWorkout: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScreenHeader(
                eyebrow = "Tu rutina semanal",
                title = "Entrenamiento",
                modifier = Modifier.weight(1f),
            )
            ProgressBadge(progress = state.progress)
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(WeekDay.entries.toList(), key = { it.index }) { day ->
                val session = state.routine.sessionFor(day)
                DayPill(
                    day = day,
                    short = session?.shortTitle ?: "—",
                    selected = day == state.selectedDay,
                    onClick = { viewModel.selectDay(day) },
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        val session = state.session
        if (session == null || session.isRest) {
            EmptyState(
                title = session?.title ?: "Día libre",
                message = session?.focus
                    ?: "Este día no tiene sesión programada. Camina, estira y duerme bien.",
                actionLabel = "Ver el lunes",
                onAction = { viewModel.selectDay(WeekDay.MONDAY) },
                modifier = Modifier.padding(top = 32.dp),
            )
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                // Deja sitio para el temporizador flotante mientras corre el descanso.
                bottom = BottomBarSpacing + if (state.restTimer != null) 104.dp else 0.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { SessionSummary(session = session, state = state, onReset = viewModel::resetDay) }

            itemsIndexed(session.exercises, key = { _, item -> item.id }) { index, exercise ->
                val done = exerciseKey(state.selectedDay, exercise.id) in state.completedKeys
                ExerciseCard(
                    position = index + 1,
                    exercise = exercise,
                    done = done,
                    onToggle = {
                        viewModel.toggleExercise(exercise.id, !done, exercise.restSeconds)
                    },
                )
            }

            item {
                RestTimerCard(
                    timer = null,
                    defaultSeconds = session.exercises.firstOrNull()?.restSeconds ?: 90,
                    onStart = viewModel::startTimer,
                    onStop = viewModel::stopTimer,
                )
            }

            item {
                AnimatedVisibility(visible = state.allDone) {
                    Column {
                        Spacer(Modifier.height(4.dp))
                        PrimaryButton(
                            text = "Finalizar rutina",
                            onClick = { onFinishWorkout(state.selectedDay.index) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressBadge(progress: Float) {
    val percent = (progress * 100).roundToInt()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (percent > 0) FormaLimeSoft else FormaSurface)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = "$percent% hoy",
            style = MaterialTheme.typography.titleSmall,
            color = if (percent > 0) FormaLime else FormaMuted,
        )
    }
}

@Composable
private fun DayPill(day: WeekDay, short: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) FormaLime else FormaSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day.short,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) FormaBackground.copy(alpha = 0.7f) else FormaMutedSoft,
        )
        Text(
            text = short,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) FormaBackground else FormaOnBackground,
        )
    }
}

@Composable
private fun SessionSummary(
    session: RoutineSession,
    state: RoutineUiState.Content,
    onReset: () -> Unit,
) {
    FormaCard(contentPadding = PaddingValues(18.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "SESIÓN · ${session.focus.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = FormaMuted,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = FormaOnBackground,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.exerciseCount}",
                    style = MaterialTheme.typography.displaySmall,
                    color = FormaLime,
                )
                Text(
                    text = "ejercicios",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMuted,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        FormaProgressBar(progress = state.progress)
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${state.doneCount} de ${session.exerciseCount} · ~${session.estimatedMinutes} min · ${session.estimatedKcal} kcal",
                style = MaterialTheme.typography.bodySmall,
                color = FormaMuted,
            )
            if (state.doneCount > 0) {
                TextButton(onClick = onReset, contentPadding = PaddingValues(horizontal = 6.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        tint = FormaMutedSoft,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Reiniciar",
                        style = MaterialTheme.typography.titleSmall,
                        color = FormaMutedSoft,
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    position: Int,
    exercise: Exercise,
    done: Boolean,
    onToggle: () -> Unit,
) {
    FormaCard(
        onClick = onToggle,
        color = if (done) FormaLimeSoft else FormaSurface,
        border = if (done) BorderStroke(1.dp, FormaLime.copy(alpha = 0.6f)) else null,
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(if (done) FormaLime else FormaSurfaceHigh),
                contentAlignment = Alignment.Center,
            ) {
                if (done) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Completado",
                        tint = FormaBackground,
                        modifier = Modifier.size(17.dp),
                    )
                } else {
                    Text(
                        text = "$position",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormaMuted,
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (done) FormaMuted else FormaOnBackground,
                        textDecoration = if (done) TextDecoration.LineThrough else null,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    TagChip(text = exercise.muscle)
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${exercise.sets} series",
                        style = MaterialTheme.typography.titleSmall,
                        color = FormaLime,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "${exercise.reps} reps",
                        style = MaterialTheme.typography.titleSmall,
                        color = FormaMuted,
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = FormaMutedSoft,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "${exercise.restSeconds}s",
                        style = MaterialTheme.typography.titleSmall,
                        color = FormaMuted,
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "\uD83D\uDCA1 ${exercise.tip}",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = if (done) FormaMutedSoft else FormaMuted,
                )
            }
        }
    }
}

@Composable
private fun RestTimerCard(
    timer: RestTimer?,
    defaultSeconds: Int,
    onStart: (Int) -> Unit,
    onStop: () -> Unit,
) {
    FormaCard(
        color = if (timer != null) FormaLimeSoft else FormaSurface,
        contentPadding = PaddingValues(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = null,
                tint = FormaLime,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Temporizador de descanso",
                    style = MaterialTheme.typography.titleLarge,
                    color = FormaLime,
                )
                Text(
                    text = timer?.let { formatSeconds(it.remainingSeconds) }
                        ?: "Elige cuánto descansar entre series",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FormaMuted,
                )
            }
            if (timer != null) {
                TextButton(onClick = onStop) {
                    Text("Detener", color = FormaMuted, style = MaterialTheme.typography.titleSmall)
                }
            }
        }

        if (timer != null) {
            Spacer(Modifier.height(12.dp))
            FormaProgressBar(progress = timer.progress)
        } else {
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(45, 60, 90, 120, defaultSeconds).distinct().sorted().forEach { seconds ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(FormaSurfaceHigh)
                            .clickable { onStart(seconds) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "${seconds}s",
                            style = MaterialTheme.typography.titleSmall,
                            color = FormaOnBackground,
                        )
                    }
                }
            }
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    val minutes = seconds / 60
    val rest = seconds % 60
    return if (minutes > 0) "%d:%02d restantes".format(minutes, rest) else "$rest s restantes"
}
