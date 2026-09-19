package com.forma.app.ui.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.designsystem.ConfirmationDialog
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaLimeSoft
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaSurface
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.domain.model.ChatMessage
import com.forma.app.ui.BottomBarSpacing

@Composable
fun AiScreen(viewModel: AiViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var confirmClear by remember { mutableStateOf(false) }

    LaunchedEffect(state.messages.size, state.thinking) {
        val target = state.messages.size + if (state.thinking) 1 else 0
        if (target > 0) listState.animateScrollToItem(target)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(FormaBackground)
            .statusBarsPadding()
            .imePadding(),
    ) {
        AssistantHeader(
            canClear = state.messages.isNotEmpty(),
            provider = state.providerName,
            onClear = { confirmClear = true },
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                MessageBubble(
                    message = ChatMessage(
                        id = "welcome",
                        text = welcomeText(state.userName),
                        fromUser = false,
                        timestampMillis = 0L,
                    ),
                )
            }

            items(state.messages, key = { it.id }) { message ->
                MessageBubble(message = message)
            }

            if (state.thinking) {
                item { TypingBubble() }
            }

            state.error?.let { error ->
                item {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.forma.app.designsystem.FormaDanger,
                        modifier = Modifier.padding(start = 46.dp),
                    )
                }
            }
        }

        AnimatedVisibility(visible = state.messages.isEmpty()) {
            SuggestedQuestions(
                questions = viewModel.suggestedQuestions,
                onPick = viewModel::send,
            )
        }

        ChatInput(
            value = state.input,
            enabled = !state.thinking,
            onValueChange = viewModel::onInput,
            onSend = { viewModel.send() },
        )

        Spacer(Modifier.height(BottomBarSpacing - 16.dp))
    }

    if (confirmClear) {
        ConfirmationDialog(
            title = "¿Borrar la conversación?",
            message = "Se eliminarán los mensajes guardados en este dispositivo.",
            confirmLabel = "Borrar",
            onConfirm = {
                viewModel.clear()
                confirmClear = false
            },
            onDismiss = { confirmClear = false },
        )
    }
}

private fun welcomeText(userName: String): String {
    val greeting = if (userName.isBlank()) "¡Hola!" else "¡Hola, $userName!"
    return "$greeting Soy tu asistente fitness con IA. Puedo ayudarte con preguntas sobre " +
        "entrenamiento, nutrición, recuperación y más. ¿Qué quieres saber hoy?"
}

@Composable
private fun AssistantHeader(canClear: Boolean, provider: String, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AssistantAvatar(size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = "Asistente IA",
                style = MaterialTheme.typography.headlineSmall,
                color = FormaOnBackground,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(FormaLime),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Siempre disponible · $provider",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )
            }
        }
        if (canClear) {
            Icon(
                imageVector = Icons.Rounded.DeleteSweep,
                contentDescription = "Borrar conversación",
                tint = FormaMuted,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onClear)
                    .padding(8.dp)
                    .size(22.dp),
            )
        }
    }
}

@Composable
private fun AssistantAvatar(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(FormaLimeSoft),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "\uD83E\uDD16", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    if (message.fromUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(RoundedCornerShape(20.dp, 20.dp, 6.dp, 20.dp))
                    .background(FormaLime)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FormaBackground,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth()) {
            AssistantAvatar(size = 34.dp)
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(RoundedCornerShape(6.dp, 20.dp, 20.dp, 20.dp))
                    .background(FormaSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FormaOnBackground,
                )
            }
        }
    }
}

@Composable
private fun TypingBubble() {
    val transition = rememberInfiniteTransition(label = "typing")
    Row(verticalAlignment = Alignment.CenterVertically) {
        AssistantAvatar(size = 34.dp)
        Spacer(Modifier.width(10.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp, 20.dp, 20.dp, 20.dp))
                .background(FormaSurface)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(3) { index ->
                val alpha by transition.animateFloat(
                    initialValue = 0.25f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = index * 160),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "dot-$index",
                )
                Box(
                    Modifier
                        .size(7.dp)
                        .alpha(alpha)
                        .clip(CircleShape)
                        .background(FormaLime),
                )
            }
        }
    }
}

@Composable
private fun SuggestedQuestions(questions: List<String>, onPick: (String) -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "PREGUNTAS FRECUENTES",
            style = MaterialTheme.typography.labelSmall,
            color = FormaMuted,
        )
        Spacer(Modifier.height(10.dp))
        questions.take(6).forEach { question ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FormaSurface)
                    .clickable { onPick(question) }
                    .padding(horizontal = 16.dp, vertical = 13.dp),
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FormaOnBackground,
                )
            }
        }
    }
}

@Composable
private fun ChatInput(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(26.dp))
                .background(FormaSurfaceHigh)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "Pregunta sobre fitness, nutrición…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FormaMutedSoft,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = FormaOnBackground),
                cursorBrush = SolidColor(FormaLime),
                maxLines = 4,
            )
        }
        Spacer(Modifier.width(12.dp))
        val sendEnabled = enabled && value.isNotBlank()
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (sendEnabled) FormaLime else FormaSurfaceHigh)
                .clickable(enabled = sendEnabled, onClick = onSend),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = "Enviar",
                tint = if (sendEnabled) FormaBackground else FormaMutedSoft,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
