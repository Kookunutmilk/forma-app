package com.forma.app.ui.routine

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.forma.app.core.ImageStore
import com.forma.app.designsystem.ErrorState
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaCard
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.FormaTextField
import com.forma.app.designsystem.LoadingState
import com.forma.app.designsystem.PrimaryButton
import com.forma.app.designsystem.StatTile
import com.forma.app.ui.home.BackRow

@Composable
fun FinishWorkoutScreen(
    onBack: () -> Unit,
    onPublished: () -> Unit,
    viewModel: FinishWorkoutViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) viewModel.onPhoto(ImageStore.persist(context, uri))
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(FormaBackground),
    ) {
        when {
            state.loading -> LoadingState(
                message = "Cerrando tu sesión…",
                modifier = Modifier.align(Alignment.Center),
            )

            state.error != null -> ErrorState(
                message = state.error.orEmpty(),
                onRetry = onBack,
                modifier = Modifier.align(Alignment.Center),
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .imePadding()
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(12.dp))
                BackRow(label = "Volver", onBack = onBack)
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "¡RUTINA COMPLETADA!",
                    style = MaterialTheme.typography.labelSmall,
                    color = FormaLime,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = state.session?.title ?: "Entrenamiento",
                    style = MaterialTheme.typography.displayMedium,
                    color = FormaOnBackground,
                )
                Spacer(Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        value = "${state.session?.exerciseCount ?: 0}",
                        label = "ejercicios",
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "${state.session?.estimatedMinutes ?: 0}",
                        label = "minutos",
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "${state.session?.estimatedKcal ?: 0}",
                        label = "kcal",
                        modifier = Modifier.weight(1f),
                        accent = FormaLime,
                    )
                }

                Spacer(Modifier.height(22.dp))

                Text(
                    text = "Sube tu foto de hoy",
                    style = MaterialTheme.typography.headlineSmall,
                    color = FormaOnBackground,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Aparecerá en tu perfil y, si quieres, en el feed de la comunidad.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FormaMuted,
                )
                Spacer(Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.35f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(FormaSurface)
                        .border(
                            1.dp,
                            if (state.photoUri != null) FormaLime else FormaSurfaceHigh,
                            RoundedCornerShape(20.dp),
                        )
                        .clickable {
                            picker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly,
                                ),
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    val photo = state.photoUri
                    if (photo != null) {
                        AsyncImage(
                            model = photo,
                            contentDescription = "Tu foto del entrenamiento",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Rounded.AddAPhoto,
                                contentDescription = null,
                                tint = FormaLime,
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "Toca para elegir una foto",
                                style = MaterialTheme.typography.titleMedium,
                                color = FormaMuted,
                            )
                            Text(
                                text = "Opcional",
                                style = MaterialTheme.typography.bodySmall,
                                color = FormaMutedSoft,
                            )
                        }
                    }
                }

                if (state.photoUri != null) {
                    TextButton(onClick = { viewModel.onPhoto(null) }) {
                        Text(
                            text = "Quitar foto",
                            color = FormaMutedSoft,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                FormaTextField(
                    value = state.caption,
                    onValueChange = viewModel::onCaption,
                    placeholder = "¿Cómo te fue hoy?",
                    label = "Descripción",
                    singleLine = false,
                )

                Spacer(Modifier.height(16.dp))

                ShareToggle(
                    checked = state.shareToCommunity,
                    enabled = state.photoUri != null,
                    onToggle = viewModel::toggleShare,
                )

                Spacer(Modifier.height(22.dp))

                PrimaryButton(
                    text = if (state.shareToCommunity && state.photoUri != null) {
                        "Guardar y publicar"
                    } else {
                        "Guardar entrenamiento"
                    },
                    onClick = {
                        viewModel.save { published -> if (published) onPublished() else onBack() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    loading = state.saving,
                )

                Spacer(Modifier.height(24.dp))
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun ShareToggle(checked: Boolean, enabled: Boolean, onToggle: () -> Unit) {
    val active = checked && enabled
    FormaCard(
        onClick = { if (enabled) onToggle() },
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (active) FormaLime else FormaSurfaceHigh),
                contentAlignment = Alignment.Center,
            ) {
                if (active) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = FormaBackground,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Compartir en la comunidad",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (enabled) FormaOnBackground else FormaMutedSoft,
                )
                Text(
                    text = if (enabled) {
                        "Solo reacciones, likes y compartir. Sin comentarios."
                    } else {
                        "Necesitas subir una foto para publicar"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMuted,
                )
            }
        }
    }
}
