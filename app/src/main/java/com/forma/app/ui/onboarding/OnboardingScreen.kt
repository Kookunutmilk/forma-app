package com.forma.app.ui.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.forma.app.core.ImageStore
import com.forma.app.data.catalog.EquipmentCatalog
import com.forma.app.data.catalog.IngredientCatalog
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.FormaTextField
import com.forma.app.designsystem.PrimaryButton
import com.forma.app.designsystem.SecondaryButton
import com.forma.app.domain.model.ExperienceLevel
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.IngredientCategory
import com.forma.app.domain.model.Sport
import com.forma.app.domain.repository.AuthUser

@Composable
fun OnboardingScreen(
    user: AuthUser,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(user.uid) {
        viewModel.prefill(user.displayName, user.photoUrl)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaBackground)
            .statusBarsPadding(),
    ) {
        StepProgress(step = state.step, total = state.totalSteps)

        Column(Modifier.padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(18.dp))
            Text(
                text = if (state.step == 0) {
                    "BIENVENID@"
                } else {
                    "PASO ${state.step + 1} DE ${state.totalSteps}"
                },
                style = MaterialTheme.typography.labelSmall,
                color = FormaLime,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stepTitle(state),
                style = MaterialTheme.typography.displayMedium,
                color = FormaOnBackground,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stepSubtitle(state),
                style = MaterialTheme.typography.bodyLarge,
                color = FormaMuted,
            )
        }

        AnimatedContent(
            targetState = state.step,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                val forward = targetState > initialState
                val offset = if (forward) 1 else -1
                (slideInHorizontally(tween(260)) { it * offset } + fadeIn(tween(260)))
                    .togetherWith(
                        slideOutHorizontally(tween(260)) { -it * offset } + fadeOut(tween(160)),
                    )
            },
            label = "onboarding-step",
        ) { step ->
            when (step) {
                0 -> ProfileStep(state, viewModel)
                1 -> BodyStep(state, viewModel)
                2 -> SportStep(state, viewModel)
                3 -> EquipmentStep(state, viewModel)
                else -> IngredientsStep(state, viewModel)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.step > 0) {
                SecondaryButton(
                    text = "Atrás",
                    onClick = viewModel::back,
                    modifier = Modifier.width(110.dp),
                )
            }
            PrimaryButton(
                text = if (state.step == state.totalSteps - 1) "¡Comenzar!" else "Continuar",
                onClick = { viewModel.next(user.uid, user.email) {} },
                modifier = Modifier.weight(1f),
                enabled = state.canContinue,
                loading = state.saving,
            )
        }
    }
}

private fun stepTitle(state: OnboardingUiState) = when (state.step) {
    0 -> "Crea tu perfil"
    1 -> "Tu cuerpo"
    2 -> "Tu experiencia"
    3 -> state.sport.equipmentTitle
    else -> "Tus alimentos"
}

private fun stepSubtitle(state: OnboardingUiState) = when (state.step) {
    0 -> "Tu nombre real y una foto tuya"
    1 -> "Necesitamos esto para personalizar tus rutinas"
    2 -> "Así ajustamos intensidad y estructura"
    3 -> state.sport.equipmentSubtitle
    else -> "Marca lo que te gusta y armamos tu menú solo con eso"
}

@Composable
private fun StepProgress(step: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (index <= step) FormaLime else FormaSurfaceHigh),
            )
        }
    }
}

// ------------------------------------------------------------------ Paso 1

@Composable
private fun ProfileStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) viewModel.onPhoto(ImageStore.persist(context, uri))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(136.dp)
                .clip(CircleShape)
                .background(FormaSurfaceHigh)
                .border(2.dp, if (state.photoUri != null) FormaLime else FormaSurface, CircleShape)
                .clickable {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            if (state.photoUri != null) {
                AsyncImage(
                    model = state.photoUri,
                    contentDescription = "Tu foto de perfil",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.AddAPhoto,
                        contentDescription = null,
                        tint = FormaLime,
                        modifier = Modifier.size(30.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Subir foto",
                        style = MaterialTheme.typography.titleSmall,
                        color = FormaMuted,
                    )
                }
            }
        }

        if (state.photoUri != null) {
            TextButton(onClick = {
                picker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }) {
                Text("Cambiar foto", color = FormaLime, style = MaterialTheme.typography.titleSmall)
            }
        } else {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Toca el círculo para elegir una foto de tu galería",
                style = MaterialTheme.typography.bodySmall,
                color = FormaMutedSoft,
            )
        }

        Spacer(Modifier.height(28.dp))

        FormaTextField(
            value = state.name,
            onValueChange = viewModel::onName,
            placeholder = "Nombre y apellido",
            label = "Tu nombre",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
    }
}

// ------------------------------------------------------------------ Paso 2

@Composable
private fun BodyStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        MeasureSlider(
            label = "Edad",
            value = state.age,
            unit = "años",
            range = 10f..80f,
            onChange = viewModel::onAge,
        )
        Spacer(Modifier.height(22.dp))
        MeasureSlider(
            label = "Peso",
            value = state.weightKg,
            unit = "kg",
            range = 30f..200f,
            onChange = viewModel::onWeight,
        )
        Spacer(Modifier.height(22.dp))
        MeasureSlider(
            label = "Altura",
            value = state.heightCm,
            unit = "cm",
            range = 120f..220f,
            onChange = viewModel::onHeight,
        )

        Spacer(Modifier.height(30.dp))
        Text(
            text = "TU OBJETIVO",
            style = MaterialTheme.typography.labelSmall,
            color = FormaMuted,
        )
        Spacer(Modifier.height(12.dp))
        ChoiceGrid(
            items = Goal.entries,
            isSelected = { it == state.goal },
            label = { "${it.emoji}  ${it.displayName}" },
            onClick = viewModel::onGoal,
        )
    }
}

@Composable
private fun MeasureSlider(
    label: String,
    value: Int,
    unit: String,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Int) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = FormaMuted,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.headlineMedium,
                    color = FormaLime,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.titleMedium,
                    color = FormaLime,
                )
            }
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = FormaOnBackground,
                activeTrackColor = FormaOnBackground,
                inactiveTrackColor = FormaSurfaceHigh,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${range.start.toInt()} $unit",
                style = MaterialTheme.typography.bodySmall,
                color = FormaMutedSoft,
            )
            Text(
                text = "${range.endInclusive.toInt()} $unit",
                style = MaterialTheme.typography.bodySmall,
                color = FormaMutedSoft,
            )
        }
    }
}

// ------------------------------------------------------------------ Paso 3

@Composable
private fun SportStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Text(
            text = "NIVEL DE EXPERIENCIA",
            style = MaterialTheme.typography.labelSmall,
            color = FormaMuted,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ExperienceLevel.entries.forEach { level ->
                SelectableChip(
                    text = level.displayName,
                    selected = level == state.level,
                    onClick = { viewModel.onLevel(level) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = state.level.description,
            style = MaterialTheme.typography.bodySmall,
            color = FormaMutedSoft,
        )

        Spacer(Modifier.height(26.dp))
        Text(
            text = "TU DEPORTE PRINCIPAL",
            style = MaterialTheme.typography.labelSmall,
            color = FormaMuted,
        )
        Spacer(Modifier.height(12.dp))
        ChoiceGrid(
            items = Sport.entries,
            isSelected = { it == state.sport },
            label = { "${it.emoji}  ${it.displayName}" },
            onClick = viewModel::onSport,
        )
    }
}

// ------------------------------------------------------------------ Paso 4

@Composable
private fun EquipmentStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val groups = EquipmentCatalog.groupsFor(state.sport)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${state.equipment.size} seleccionados",
                    style = MaterialTheme.typography.titleSmall,
                    color = FormaLime,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = viewModel::selectAllEquipment) {
                    Text("Todo", color = FormaMuted, style = MaterialTheme.typography.titleSmall)
                }
                TextButton(onClick = viewModel::clearEquipment) {
                    Text("Ninguno", color = FormaMuted, style = MaterialTheme.typography.titleSmall)
                }
            }
        }

        groups.forEach { group ->
            item(key = group.title) {
                Column {
                    Text(
                        text = group.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = FormaMuted,
                    )
                    Spacer(Modifier.height(12.dp))
                    ChoiceGrid(
                        items = group.items,
                        isSelected = { it.id in state.equipment },
                        label = { it.label },
                        onClick = { viewModel.toggleEquipment(it.id) },
                        showCheck = true,
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------------ Paso 5

@Composable
private fun IngredientsStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Text(
                text = "${state.ingredients.size} ingredientes marcados",
                style = MaterialTheme.typography.titleSmall,
                color = FormaLime,
            )
        }
        items(IngredientCategory.entries.toList(), key = { it.id }) { category ->
            val ingredients = IngredientCatalog.byCategory[category].orEmpty()
            Column {
                Text(
                    text = "${category.emoji}  ${category.displayName.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = FormaMuted,
                )
                Spacer(Modifier.height(12.dp))
                ChoiceGrid(
                    items = ingredients,
                    isSelected = { it.id in state.ingredients },
                    label = { "${it.emoji}  ${it.name}" },
                    onClick = { viewModel.toggleIngredient(it.id) },
                    showCheck = true,
                )
            }
        }
    }
}

// ------------------------------------------------------------------ Común

@Composable
private fun <T> ChoiceGrid(
    items: List<T>,
    isSelected: (T) -> Boolean,
    label: (T) -> String,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    showCheck: Boolean = false,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { item ->
                    SelectableChip(
                        text = label(item),
                        selected = isSelected(item),
                        onClick = { onClick(item) },
                        modifier = Modifier.weight(1f),
                        showCheck = showCheck,
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showCheck: Boolean = false,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) FormaLime else FormaSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showCheck && selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = FormaBackground,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) FormaBackground else FormaMuted,
            maxLines = 2,
        )
    }
}
