package com.forma.app.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * Ilustración generada para el contenido de ejemplo (feed, artículos y recetas).
 *
 * Se dibuja en Compose en vez de empaquetar fotos de stock: así el contenido sembrado se ve
 * consistente con la identidad de FORMA, no depende de la red y no pesa en el APK. Cuando el
 * usuario sube una foto propia, [FormaImage] la muestra con Coil.
 */
private data class Artwork(val emoji: String, val start: Color, val end: Color)

private val artworks: Map<String, Artwork> = mapOf(
    // Comunidad
    "feed_yoga" to Artwork("\uD83E\uDDD8", Color(0xFF2A1F4A), Color(0xFF0F1330)),
    "feed_gym" to Artwork("\uD83C\uDFCB\uFE0F", Color(0xFF2C3A10), Color(0xFF12160B)),
    "feed_running" to Artwork("\uD83C\uDFC3", Color(0xFF123A32), Color(0xFF0A1A1D)),
    "feed_boxing" to Artwork("\uD83E\uDD4A", Color(0xFF3B1418), Color(0xFF1A0B0E)),
    "feed_crossfit" to Artwork("\u26A1", Color(0xFF3A2A08), Color(0xFF17120A)),
    "feed_swimming" to Artwork("\uD83C\uDFCA", Color(0xFF0E2E45), Color(0xFF081720)),
    "feed_cycling" to Artwork("\uD83D\uDEB4", Color(0xFF16321C), Color(0xFF0A170F)),
    "feed_pilates" to Artwork("\uD83E\uDD38", Color(0xFF3A1730), Color(0xFF1A0C18)),

    // Aprende
    "article_squat" to Artwork("\uD83E\uDDB5", Color(0xFF2E3A0F), Color(0xFF12160B)),
    "article_shoulder" to Artwork("\uD83D\uDCAA", Color(0xFF3A2410), Color(0xFF19110A)),
    "article_nutrition" to Artwork("\uD83E\uDD57", Color(0xFF123A24), Color(0xFF0A1A12)),
    "article_sleep" to Artwork("\uD83D\uDE34", Color(0xFF1B2247), Color(0xFF0C0F22)),
    "article_progress" to Artwork("\uD83D\uDCC8", Color(0xFF2B3A12), Color(0xFF121709)),
    "article_cardio" to Artwork("\u2764\uFE0F", Color(0xFF3A1226), Color(0xFF190A12)),
    "article_protein" to Artwork("\uD83C\uDF57", Color(0xFF3A2A12), Color(0xFF17120A)),
    "article_soreness" to Artwork("\uD83E\uDDCA", Color(0xFF16304A), Color(0xFF0A1623)),
    "article_mindset" to Artwork("\uD83E\uDDE0", Color(0xFF2E1A3E), Color(0xFF150C1C)),
    "article_core" to Artwork("\uD83E\uDEC0", Color(0xFF3A1A1A), Color(0xFF190C0C)),
)

private val foodEmojis = mapOf(
    "food_oats" to "\uD83E\uDD63", "food_omelette" to "\uD83C\uDF73",
    "food_avocado_toast" to "\uD83E\uDD51", "food_smoothie" to "\uD83E\uDD64",
    "food_pancakes" to "\uD83E\uDD5E", "food_yogurt" to "\uD83E\uDD5B",
    "food_rancheros" to "\uD83C\uDF5B", "food_chilaquiles" to "\uD83C\uDF2E",
    "food_molletes" to "\uD83C\uDF5E", "food_chia" to "\u26AB",
    "food_burrito" to "\uD83C\uDF2F", "food_chicken_bowl" to "\uD83C\uDF57",
    "food_salmon" to "\uD83C\uDF63", "food_fish_tacos" to "\uD83C\uDF2E",
    "food_pasta" to "\uD83C\uDF5D", "food_tuna_salad" to "\uD83E\uDD57",
    "food_steak" to "\uD83E\uDD69", "food_curry" to "\uD83C\uDF5B",
    "food_shrimp" to "\uD83E\uDD90", "food_lentils" to "\uD83E\uDED5",
    "food_tofu" to "\uD83E\uDDC8", "food_stuffed_chicken" to "\uD83D\uDC14",
    "food_turkey" to "\uD83E\uDD83", "food_apple" to "\uD83C\uDF4E",
    "food_nuts" to "\uD83C\uDF30", "food_jicama" to "\uD83C\uDF36\uFE0F",
    "food_hummus" to "\uD83E\uDED8", "food_edamame" to "\uD83E\uDED8",
    "food_popcorn" to "\uD83C\uDF7F", "food_cottage" to "\uD83E\uDDC0",
    "food_cookies" to "\uD83C\uDF6A", "food_turkey_roll" to "\uD83E\uDD83",
    "food_green_smoothie" to "\uD83E\uDD6C", "food_grilled_chicken" to "\uD83E\uDD57",
    "food_soup" to "\uD83C\uDF72", "food_tuna_tacos" to "\uD83C\uDF2E",
    "food_quinoa" to "\uD83C\uDF3E", "food_shrimp_soup" to "\uD83E\uDD90",
    "food_fish" to "\uD83D\uDC1F", "food_beans" to "\uD83C\uDF35",
    "food_roast_chicken" to "\uD83C\uDF60", "food_scramble" to "\uD83E\uDD5A",
)

private val foodPalette = listOf(
    Color(0xFF2C3A10) to Color(0xFF12160B),
    Color(0xFF123A24) to Color(0xFF0A1A12),
    Color(0xFF3A2A12) to Color(0xFF17120A),
    Color(0xFF16304A) to Color(0xFF0A1623),
    Color(0xFF3A1A2A) to Color(0xFF190C14),
)

private fun artworkFor(key: String?): Artwork {
    if (key == null) return Artwork("\uD83D\uDCAA", FormaSurfaceHigh, FormaSurfaceLow)
    artworks[key]?.let { return it }
    val palette = foodPalette[(key.hashCode().let { if (it < 0) -it else it }) % foodPalette.size]
    return Artwork(foodEmojis[key] ?: "\uD83C\uDF7D\uFE0F", palette.first, palette.second)
}

/**
 * Muestra una foto del usuario cuando existe (`uri`) y, si no, la ilustración asociada a `key`.
 */
@Composable
fun FormaImage(
    key: String?,
    modifier: Modifier = Modifier,
    uri: String? = null,
    emojiSize: Int = 56,
) {
    if (uri != null) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
        return
    }

    val art = artworkFor(key)
    Box(
        modifier = modifier.background(
            Brush.linearGradient(listOf(art.start, art.end)),
        ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val step = size.minDimension / 5f
            var x = -size.height
            while (x < size.width) {
                drawLine(
                    color = Color.White.copy(alpha = 0.035f),
                    start = Offset(x, size.height),
                    end = Offset(x + size.height, 0f),
                    strokeWidth = step / 6f,
                    cap = StrokeCap.Round,
                )
                x += step
            }
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(FormaLime.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(size.width * 0.78f, size.height * 0.2f),
                    radius = size.minDimension * 0.7f,
                ),
                radius = size.minDimension * 0.7f,
                center = Offset(size.width * 0.78f, size.height * 0.2f),
            )
        }
        Text(
            text = art.emoji,
            style = TextStyle(fontSize = emojiSize.sp),
        )
    }
}