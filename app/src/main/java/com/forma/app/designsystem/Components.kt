package com.forma.app.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ScreenHeader(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = eyebrow.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = FormaMuted,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                color = FormaOnBackground,
            )
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}

@Composable
fun FormaCard(
    modifier: Modifier = Modifier,
    color: Color = FormaSurface,
    border: BorderStroke? = null,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = modifier
        .clip(shape)
        .background(color)
        .then(if (border != null) Modifier.border(border, shape) else Modifier)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    Column(modifier = base.padding(contentPadding), content = content)
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = FormaOnBackground,
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(horizontal = 6.dp)) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = FormaLime,
                )
            }
        }
    }
}

@Composable
fun FormaPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: String? = null,
    subLabel: String? = null,
) {
    val background by animateColorAsState(
        targetValue = if (selected) FormaLime else FormaSurfaceHigh,
        label = "pill-bg",
    )
    val contentColor = if (selected) FormaBackground else FormaMuted
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leading != null) {
                Text(text = leading, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (subLabel != null) {
            Text(
                text = subLabel,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.85f),
                maxLines = 1,
            )
        }
    }
}

@Composable
fun <T> FormaPillRow(
    items: List<T>,
    selected: (T) -> Boolean,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    leading: (T) -> String? = { null },
    subLabel: (T) -> String? = { null },
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = contentPadding,
    ) {
        items(items) { item ->
            FormaPill(
                label = label(item),
                selected = selected(item),
                onClick = { onSelect(item) },
                leading = leading(item),
                subLabel = subLabel(item),
            )
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FormaLime,
            contentColor = FormaBackground,
            disabledContainerColor = FormaSurfaceHigh,
            disabledContentColor = FormaMutedSoft,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = FormaBackground,
                strokeWidth = 2.dp,
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FormaSurfaceHigh,
            contentColor = FormaOnBackground,
            disabledContainerColor = FormaSurfaceLow,
            disabledContentColor = FormaMutedSoft,
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: Color = FormaOnBackground,
) {
    FormaCard(modifier = modifier, contentPadding = PaddingValues(14.dp)) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = accent,
            maxLines = 1,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = FormaMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun FormaAvatar(
    name: String,
    photo: Any?,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 44.dp,
    ring: Boolean = false,
) {
    val initials = name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "F" }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(FormaLime.copy(alpha = 0.35f), FormaAqua.copy(alpha = 0.25f)),
                ),
            )
            .then(
                if (ring) Modifier.border(2.dp, FormaLime, CircleShape) else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (photo != null) {
            AsyncImage(
                model = photo,
                contentDescription = "Foto de $name",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                color = FormaOnBackground,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
fun FormaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None,
) {
    Column(modifier) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = FormaMuted,
            )
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FormaMutedSoft,
                )
            },
            singleLine = singleLine,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = keyboardOptions,
            leadingIcon = leadingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            supportingText = supportingText?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isError) FormaDanger else FormaMuted,
                    )
                }
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FormaSurfaceHigh,
                unfocusedContainerColor = FormaSurfaceHigh,
                errorContainerColor = FormaSurfaceHigh,
                focusedBorderColor = FormaLime,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = FormaDanger,
                focusedTextColor = FormaOnBackground,
                unfocusedTextColor = FormaOnBackground,
                cursorColor = FormaLime,
            ),
        )
    }
}

@Composable
fun FormaProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = FormaLime,
) {
    val animated by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "progress")
    LinearProgressIndicator(
        progress = { animated },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50)),
        color = color,
        trackColor = FormaSurfaceHigh,
        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {},
    )
}

@Composable
fun LoadingState(
    message: String = "Preparando tu plan…",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = FormaLime, strokeWidth = 3.dp)
        Spacer(Modifier.height(18.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = FormaMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(FormaDanger.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.CloudOff,
                contentDescription = null,
                tint = FormaDanger,
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Algo salió mal",
            style = MaterialTheme.typography.headlineSmall,
            color = FormaOnBackground,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = FormaMuted,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            Spacer(Modifier.height(20.dp))
            PrimaryButton(text = "Reintentar", onClick = onRetry)
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Inbox,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(FormaSurfaceHigh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = FormaLime)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = FormaOnBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = FormaMuted,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            PrimaryButton(text = actionLabel, onClick = onAction)
        }
    }
}

@Composable
fun QuickActionCard(
    emoji: String,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(tint.copy(alpha = 0.14f))
            .border(1.dp, tint.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = FormaOnBackground,
        )
    }
}

@Composable
fun ListRowChevron(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Rounded.ChevronRight,
        contentDescription = null,
        tint = FormaMutedSoft,
        modifier = modifier,
    )
}

@Composable
fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = FormaMuted,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = FormaSurfaceHigh,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            maxLines = 1,
        )
    }
}
