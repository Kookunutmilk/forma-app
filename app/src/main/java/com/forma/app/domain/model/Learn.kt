package com.forma.app.domain.model

enum class ArticleCategory(val id: String, val displayName: String) {
    TECHNIQUE("technique", "Técnica"),
    NUTRITION("nutrition", "Nutrición"),
    INJURY("injury", "Lesiones"),
    RECOVERY("recovery", "Recuperación"),
    MINDSET("mindset", "Mentalidad");

    companion object {
        fun fromId(id: String?): ArticleCategory =
            entries.firstOrNull { it.id == id } ?: TECHNIQUE
    }
}

data class ArticleSection(
    val heading: String,
    val body: String,
)

data class Article(
    val id: String,
    val title: String,
    val category: ArticleCategory,
    val readMinutes: Int,
    val summary: String,
    val sections: List<ArticleSection>,
    val imageKey: String,
    val featured: Boolean = false,
    val saved: Boolean = false,
)

data class ChatMessage(
    val id: String,
    val text: String,
    val fromUser: Boolean,
    val timestampMillis: Long,
)
