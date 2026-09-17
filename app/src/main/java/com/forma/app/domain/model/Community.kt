package com.forma.app.domain.model

data class Author(
    val id: String,
    val name: String,
    val handle: String,
    val photo: String? = null,
    val sportId: String = Sport.GYM.id,
    val isMe: Boolean = false,
    val following: Boolean = false,
)

/** Reacciones disponibles. No hay comentarios por decisión de producto. */
enum class Reaction(val id: String, val emoji: String, val label: String) {
    FIRE("fire", "\uD83D\uDD25", "Fuego"),
    STRONG("strong", "\uD83D\uDCAA", "Fuerza"),
    CLAP("clap", "\uD83D\uDC4F", "Aplauso"),
    HEART("heart", "\u2764\uFE0F", "Amor"),
    ROCKET("rocket", "\uD83D\uDE80", "Imparable");

    companion object {
        fun fromId(id: String?): Reaction? = entries.firstOrNull { it.id == id }
    }
}

data class Post(
    val id: String,
    val author: Author,
    val imageKey: String?,
    val imageUri: String?,
    val caption: String,
    val sportId: String,
    val createdAtMillis: Long,
    val likes: Int,
    val likedByMe: Boolean,
    val reactions: Map<String, Int>,
    val myReaction: String?,
) {
    val sport: Sport get() = Sport.fromId(sportId)
    val image: Any? get() = imageUri ?: imageKey
}
