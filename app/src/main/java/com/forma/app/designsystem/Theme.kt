package com.forma.app.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val FormaColorScheme = darkColorScheme(
    primary = FormaLime,
    onPrimary = FormaBackground,
    primaryContainer = FormaLimeSoft,
    onPrimaryContainer = FormaLime,
    secondary = FormaAqua,
    onSecondary = FormaBackground,
    tertiary = FormaOrange,
    onTertiary = FormaBackground,
    background = FormaBackground,
    onBackground = FormaOnBackground,
    surface = FormaSurface,
    onSurface = FormaOnBackground,
    surfaceVariant = FormaSurfaceHigh,
    onSurfaceVariant = FormaMuted,
    surfaceContainer = FormaSurface,
    surfaceContainerHigh = FormaSurfaceHigh,
    surfaceContainerLow = FormaSurfaceLow,
    outline = FormaOutline,
    outlineVariant = FormaOutline,
    error = FormaDanger,
    onError = FormaBackground,
)

val FormaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun FormaTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    // FORMA es una app de tema oscuro por diseño: no seguimos el ajuste claro del sistema.
    MaterialTheme(
        colorScheme = FormaColorScheme,
        typography = FormaTypography,
        shapes = FormaShapes,
        content = content,
    )
}
