package com.forma.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val photoUri: String?,
    val age: Int,
    val weightKg: Int,
    val heightCm: Int,
    val sportId: String,
    val levelId: String,
    val goalId: String,
    val equipment: List<String>,
    val likedIngredients: List<String>,
    val onboardingCompleted: Boolean,
    val provider: String = "local",
)

/** Un ejercicio marcado como hecho dentro de la semana en curso. */
@Entity(tableName = "exercise_log", primaryKeys = ["dayIndex", "exerciseId"])
data class ExerciseLogEntity(
    val dayIndex: Int,
    val exerciseId: String,
    val completed: Boolean,
    val updatedAt: Long,
)

@Entity(tableName = "workout_session")
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val dateEpochMillis: Long,
    val sportId: String,
    val title: String,
    val durationMinutes: Int,
    val kcal: Int,
    val photoUri: String?,
    val note: String,
)

/** La opción de comida elegida para un día y un momento del día, con la foto del plato. */
@Entity(tableName = "meal_choice", primaryKeys = ["dayIndex", "slotId"])
data class MealChoiceEntity(
    val dayIndex: Int,
    val slotId: String,
    val recipeId: String,
    val plateUri: String?,
)

@Entity(tableName = "author")
data class AuthorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val handle: String,
    val photoUri: String?,
    val sportId: String,
    val isMe: Boolean,
    val following: Boolean,
)

@Entity(tableName = "post")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val imageKey: String?,
    val imageUri: String?,
    val caption: String,
    val sportId: String,
    val createdAtMillis: Long,
    val likes: Int,
    val likedByMe: Boolean,
    val reactionsJson: String,
    val myReaction: String?,
)

@Entity(tableName = "chat_message")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val text: String,
    val fromUser: Boolean,
    val timestampMillis: Long,
)

@Entity(tableName = "article_state")
data class ArticleStateEntity(
    @PrimaryKey val articleId: String,
    val saved: Boolean,
)
