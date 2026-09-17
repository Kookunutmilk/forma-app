package com.forma.app.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.core.ImageStore
import com.forma.app.designsystem.ConfirmationDialog
import com.forma.app.designsystem.FormaAvatar
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaCard
import com.forma.app.designsystem.FormaImage
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaProgressBar
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.LoadingState
import com.forma.app.designsystem.SecondaryButton
import com.forma.app.designsystem.SectionTitle
import com.forma.app.designsystem.StatTile
import com.forma.app.designsystem.TagChip
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.UserProfile
import com.forma.app.ui.home.BackRow

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var editingBody by remember { mutableStateOf(false) }
    var confirmSignOut by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) viewModel.updatePhoto(ImageStore.persist(context, uri))
    }

    val profile = state.profile

    Box(
        Modifier
            .fillMaxSize()
            .background(FormaBackground),
    ) {
        if (state.loading || profile == null) {
            LoadingState(
                message = "Cargando tu perfil…",
                modifier = Modifier.align(Alignment.Center),
            )
            return@Box
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding(),
        ) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(12.dp))
                BackRow(label = "Volver", onBack = onBack)
                Spacer(Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        FormaAvatar(
                            name = profile.name,
                            photo = profile.photoUri,
                            size = 84.dp,
                            ring = true,
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(FormaLime)
                                .clickable {
                                    picker.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                                        ),
                                    )
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = FormaBackground,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                    Spacer(Modifier.width(18.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = FormaOnBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = profile.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FormaMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TagChip(
                                text = "${profile.sport.emoji} ${profile.sport.displayName}",
                                color = FormaLime,
                            )
                            TagChip(text = profile.level.displayName)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        value = "${state.totalWorkouts}",
                        label = "entrenos",
                        modifier = Modifier.weight(1f),
                        accent = FormaLime,
                    )
                    StatTile(
                        value = "${state.stats?.streakDays ?: 0}",
                        label = "días de racha",
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "${state.stats?.kcalThisWeek ?: 0}",
                        label = "kcal semana",
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(16.dp))

                BodyCard(profile = profile, onEdit = { editingBody = true })

                Spacer(Modifier.height(16.dp))

                SectionTitle(text = "Tu objetivo")
                Spacer(Modifier.height(10.dp))
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
            ) {
                items(Goal.entries) { goal ->
                    val selected = profile.goal == goal
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) FormaLime else FormaSurface)
                            .clickable { viewModel.updateGoal(goal) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = goal.emoji, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = goal.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selected) FormaBackground else FormaMuted,
                        )
                    }
                }
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Meta diaria: ${profile.dailyCalories} kcal · " +
                        "${profile.proteinTargetG} g de proteína",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )

                Spacer(Modifier.height(24.dp))
                SectionTitle(text = "Tus publicaciones")
                Spacer(Modifier.height(12.dp))
            }

            if (state.posts.isEmpty()) {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    FormaCard {
                        Text(
                            text = "Todavía no publicas nada",
                            style = MaterialTheme.typography.titleLarge,
                            color = FormaOnBackground,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Al terminar una rutina puedes subir tu foto y compartirla " +
                                "con la comunidad.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FormaMuted,
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                ) {
                    items(state.posts, key = { it.id }) { post ->
                        Column(
                            Modifier
                                .width(150.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(FormaSurface),
                        ) {
                            FormaImage(
                                key = post.imageKey,
                                uri = post.imageUri,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                emojiSize = 40,
                            )
                            Text(
                                text = post.caption,
                                style = MaterialTheme.typography.bodySmall,
                                color = FormaMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(10.dp),
                            )
                        }
                    }
                }
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(24.dp))
                SectionTitle(text = "Ajustes")
                Spacer(Modifier.height(12.dp))

                FormaCard {
                    Text(
                        text = "Descanso entre series",
                        style = MaterialTheme.typography.titleLarge,
                        color = FormaOnBackground,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Es el tiempo que arranca el cronómetro al marcar un ejercicio.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormaMuted,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        viewModel.restOptions.forEach { seconds ->
                            val selected = state.restSeconds == seconds
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) FormaLime else FormaSurfaceHigh)
                                    .clickable { viewModel.setRestSeconds(seconds) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "${seconds}s",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (selected) FormaBackground else FormaMuted,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                FormaCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "Recordatorios de entreno",
                                style = MaterialTheme.typography.titleLarge,
                                color = FormaOnBackground,
                            )
                            Text(
                                text = "Avisos para no romper tu racha.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FormaMuted,
                            )
                        }
                        Switch(
                            checked = state.remindersEnabled,
                            onCheckedChange = viewModel::setReminders,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FormaBackground,
                                checkedTrackColor = FormaLime,
                                uncheckedThumbColor = FormaMuted,
                                uncheckedTrackColor = FormaSurfaceHigh,
                                uncheckedBorderColor = FormaSurfaceHigh,
                            ),
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                FormaCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "Sesión",
                                style = MaterialTheme.typography.titleLarge,
                                color = FormaOnBackground,
                            )
                            Text(
                                text = "Autenticación: ${state.provider}",
                                style = MaterialTheme.typography.bodySmall,
                                color = FormaMuted,
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    SecondaryButton(
                        text = "Cerrar sesión",
                        onClick = { confirmSignOut = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }

    if (editingBody && profile != null) {
        BodyEditorDialog(
            profile = profile,
            onDismiss = { editingBody = false },
            onSave = { weight, height, age ->
                viewModel.updateBody(weight, height, age)
                editingBody = false
            },
        )
    }

    if (confirmSignOut) {
        ConfirmationDialog(
            title = "¿Cerrar sesión?",
            message = "Volverás a la pantalla de acceso. Tus datos quedan guardados en este " +
                "dispositivo.",
            confirmLabel = "Cerrar sesión",
            onConfirm = {
                confirmSignOut = false
                viewModel.signOut()
            },
            onDismiss = { confirmSignOut = false },
        )
    }
}

@Composable
private fun BodyCard(profile: UserProfile, onEdit: () -> Unit) {
    FormaCard(onClick = onEdit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Tu cuerpo",
                    style = MaterialTheme.typography.titleLarge,
                    color = FormaOnBackground,
                )
                Text(
                    text = "${profile.weightKg} kg · ${profile.heightCm} cm · " +
                        "${profile.age} años",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FormaMuted,
                )
            }
            Text(
                text = "Editar",
                style = MaterialTheme.typography.labelLarge,
                color = FormaLime,
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "IMC ${profile.bmi}",
                style = MaterialTheme.typography.headlineSmall,
                color = FormaOnBackground,
            )
            Spacer(Modifier.width(10.dp))
            TagChip(text = profile.bmiLabel, color = FormaLime)
        }
        Spacer(Modifier.height(10.dp))
        FormaProgressBar(progress = ((profile.bmi - 15) / 20).toFloat())
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Referencia: 18.5 a 24.9 se considera rango normal.",
            style = MaterialTheme.typography.bodySmall,
            color = FormaMutedSoft,
        )
    }
}

@Composable
private fun BodyEditorDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit,
) {
    var weight by remember { mutableIntStateOf(profile.weightKg) }
    var height by remember { mutableIntStateOf(profile.heightCm) }
    var age by remember { mutableIntStateOf(profile.age) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FormaSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Tus medidas",
                style = MaterialTheme.typography.headlineSmall,
                color = FormaOnBackground,
            )
        },
        text = {
            Column {
                Stepper(
                    label = "Peso",
                    value = "$weight kg",
                    onMinus = { weight = (weight - 1).coerceAtLeast(35) },
                    onPlus = { weight = (weight + 1).coerceAtMost(200) },
                )
                Spacer(Modifier.height(12.dp))
                Stepper(
                    label = "Estatura",
                    value = "$height cm",
                    onMinus = { height = (height - 1).coerceAtLeast(130) },
                    onPlus = { height = (height + 1).coerceAtMost(220) },
                )
                Spacer(Modifier.height(12.dp))
                Stepper(
                    label = "Edad",
                    value = "$age años",
                    onMinus = { age = (age - 1).coerceAtLeast(14) },
                    onPlus = { age = (age + 1).coerceAtMost(90) },
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = { onSave(weight, height, age) }) {
                Text(
                    text = "Guardar",
                    style = MaterialTheme.typography.labelLarge,
                    color = FormaLime,
                )
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.labelLarge,
                    color = FormaMuted,
                )
            }
        },
    )
}

@Composable
private fun Stepper(
    label: String,
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = FormaMuted,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = FormaOnBackground,
            )
        }
        StepperButton(symbol = "−", onClick = onMinus)
        Spacer(Modifier.width(10.dp))
        StepperButton(symbol = "+", onClick = onPlus)
    }
}

@Composable
private fun StepperButton(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(FormaSurfaceHigh)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.headlineSmall,
            color = FormaLime,
        )
    }
}
