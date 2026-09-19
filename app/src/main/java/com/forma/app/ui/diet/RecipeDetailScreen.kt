package com.forma.app.ui.diet

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.forma.app.designsystem.FormaImage
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
import com.forma.app.ui.home.BackRow

@Composable
fun RecipeDetailScreen(
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) viewModel.onPlatePhoto(ImageStore.persist(context, uri))
    }

    val recipe = state.recipe

    Box(
        Modifier
            .fillMaxSize()
            .background(FormaBackground),
    ) {
        when {
            state.loading -> LoadingState(
                message = "Abriendo la receta…",
                modifier = Modifier.align(Alignment.Center),
            )

            recipe == null -> ErrorState(
                message = "No encontramos esta receta.",
                onRetry = onBack,
                modifier = Modifier.align(Alignment.Center),
            )

            else -> Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Box {
                    FormaImage(
                        key = recipe.imageKey,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        emojiSize = 76,
                    )
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(16.dp),
                    ) {
                        BackRow(label = "Volver", onBack = onBack)
                    }
                }

                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "${state.slot.displayName.uppercase()} · ${state.slot.time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FormaLime,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.displaySmall,
                        color = FormaOnBackground,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = null,
                            tint = FormaMutedSoft,
                            modifier = Modifier.size(15.dp),
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "${recipe.minutes} min de preparación",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FormaMuted,
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MacroTile("${recipe.kcal}", "kcal", FormaLime, Modifier.weight(1f))
                        MacroTile("${recipe.proteinG}g", "proteína", MacroProtein, Modifier.weight(1f))
                        MacroTile("${recipe.carbsG}g", "carbos", MacroCarbs, Modifier.weight(1f))
                        MacroTile("${recipe.fatG}g", "grasa", MacroFat, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(26.dp))

                    Text(
                        text = "Ingredientes",
                        style = MaterialTheme.typography.headlineSmall,
                        color = FormaOnBackground,
                    )
                    Spacer(Modifier.height(12.dp))
                    FormaCard(contentPadding = PaddingValues(4.dp)) {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 13.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = ingredient.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = FormaOnBackground,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = ingredient.amount,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = FormaLime,
                                )
                            }
                            if (index != recipe.ingredients.lastIndex) {
                                HorizontalDivider(
                                    color = FormaOutline.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(26.dp))

                    Text(
                        text = "Preparación",
                        style = MaterialTheme.typography.headlineSmall,
                        color = FormaOnBackground,
                    )
                    Spacer(Modifier.height(12.dp))
                    recipe.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.padding(bottom = 14.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(FormaSurfaceHigh),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = FormaLime,
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyLarge,
                                color = FormaMuted,
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    PlatePhotoSection(
                        photoUri = state.plateUri,
                        onPick = {
                            picker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly,
                                ),
                            )
                        },
                        onRemove = { viewModel.onPlatePhoto(null) },
                    )

                    Spacer(Modifier.height(32.dp))
                    Spacer(Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

@Composable
private fun MacroTile(value: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(FormaSurface)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = color)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = FormaMutedSoft)
    }
}

@Composable
private fun PlatePhotoSection(
    photoUri: String?,
    onPick: () -> Unit,
    onRemove: () -> Unit,
) {
    Column {
        Text(
            text = "Tu plato",
            style = MaterialTheme.typography.headlineSmall,
            color = FormaOnBackground,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Sube la foto de cómo te quedó. Se guarda solo para ti, en tu historial de comidas.",
            style = MaterialTheme.typography.bodyMedium,
            color = FormaMuted,
        )
        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .clip(RoundedCornerShape(18.dp))
                .background(FormaSurface)
                .border(
                    1.dp,
                    if (photoUri != null) FormaLime else FormaSurfaceHigh,
                    RoundedCornerShape(18.dp),
                )
                .clickable(onClick = onPick),
            contentAlignment = Alignment.Center,
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Foto de tu plato",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.AddAPhoto,
                        contentDescription = null,
                        tint = FormaLime,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Subir foto de tu plato",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormaMuted,
                    )
                }
            }
        }

        if (photoUri != null) {
            TextButton(onClick = onRemove) {
                Text(
                    text = "Quitar foto",
                    color = FormaMutedSoft,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}
