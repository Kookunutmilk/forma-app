package com.forma.app.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaDanger
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMuted
import com.forma.app.designsystem.FormaMutedSoft
import com.forma.app.designsystem.FormaOnBackground
import com.forma.app.designsystem.FormaOutline
import com.forma.app.designsystem.FormaSurfaceHigh
import com.forma.app.designsystem.FormaTextField
import com.forma.app.designsystem.PrimaryButton
import com.forma.app.designsystem.SecondaryButton

@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(24.dp))
            LogoMark()
            Spacer(Modifier.height(22.dp))

            Text(
                text = "FORMA",
                style = MaterialTheme.typography.displayLarge,
                color = FormaOnBackground,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tu entrenamiento, tu comida y tu comunidad en un solo lugar.",
                style = MaterialTheme.typography.bodyLarge,
                color = FormaMuted,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(36.dp))

            AnimatedVisibility(visible = state.isRegister) {
                Column {
                    FormaTextField(
                        value = state.name,
                        onValueChange = viewModel::onName,
                        placeholder = "¿Cómo te llamas?",
                        label = "Tu nombre",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            FormaTextField(
                value = state.email,
                onValueChange = viewModel::onEmail,
                placeholder = "tucorreo@ejemplo.com",
                label = "Correo electrónico",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )
            Spacer(Modifier.height(16.dp))
            FormaTextField(
                value = state.password,
                onValueChange = viewModel::onPassword,
                placeholder = "Mínimo 6 caracteres",
                label = "Contraseña",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                visualTransformation = PasswordVisualTransformation(),
            )

            AnimatedVisibility(visible = state.error != null) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = state.error.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = FormaDanger,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = if (state.isRegister) "Crear cuenta" else "Entrar",
                onClick = viewModel::submit,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.canSubmit,
                loading = state.loading,
            )

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = FormaOutline)
                Text(
                    text = "  o  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormaMutedSoft,
                )
                HorizontalDivider(Modifier.weight(1f), color = FormaOutline)
            }

            Spacer(Modifier.height(20.dp))

            SecondaryButton(
                text = if (state.googleLoading) "Conectando…" else "Continuar con Google",
                onClick = viewModel::signInWithGoogle,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.googleLoading,
            )

            Spacer(Modifier.height(20.dp))

            TextButton(onClick = viewModel::toggleMode) {
                Text(
                    text = if (state.isRegister) {
                        "¿Ya tienes cuenta? Inicia sesión"
                    } else {
                        "¿Primera vez aquí? Crea tu cuenta"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = FormaLime,
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Sesión gestionada por ${state.providerName}",
                style = MaterialTheme.typography.bodySmall,
                color = FormaMutedSoft,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LogoMark() {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(FormaLime),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "F",
            style = MaterialTheme.typography.displayLarge,
            color = FormaBackground,
            fontWeight = FontWeight.Black,
        )
    }
    Spacer(Modifier.width(1.dp))
    Box(
        modifier = Modifier
            .padding(top = 12.dp)
            .height(4.dp)
            .width(48.dp)
            .clip(RoundedCornerShape(50))
            .background(FormaSurfaceHigh),
    )
}
