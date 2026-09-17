package com.forma.app.domain.repository

import com.forma.app.domain.model.Article
import com.forma.app.domain.model.ChatMessage
import com.forma.app.domain.model.DayMealPlan
import com.forma.app.domain.model.Ingredient
import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.Post
import com.forma.app.domain.model.Reaction
import com.forma.app.domain.model.TrainingStats
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.model.WeeklyRoutine
import com.forma.app.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val provider: String = "local",
)

/**
 * Autenticación con correo o Google. La app trae una implementación local persistida para
 * poder correr sin `google-services.json`; con credenciales reales se cambia el binding de Hilt
 * a la implementación de Firebase sin tocar la UI.
 */
interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    val providerName: String
    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signUpWithEmail(name: String, email: String, password: String): Result<AuthUser>
    suspend fun signInWithGoogle(): Result<AuthUser>
    suspend fun signOut()
}

interface ProfileRepository {
    val profile: Flow<UserProfile?>
    suspend fun current(): UserProfile?
    suspend fun save(profile: UserProfile)
    suspend fun updatePhoto(uri: String?)
    suspend fun clear()
}

interface TrainingRepository {
    suspend fun weeklyRoutine(profile: UserProfile): WeeklyRoutine
    fun completedExerciseKeys(): Flow<Set<String>>
    suspend fun setExerciseCompleted(day: WeekDay, exerciseId: String, completed: Boolean)
    suspend fun resetDay(day: WeekDay)
    fun sessions(): Flow<List<WorkoutSession>>
    fun stats(): Flow<TrainingStats>
    suspend fun finishWorkout(session: WorkoutSession)
}

interface NutritionRepository {
    suspend fun dayPlan(profile: UserProfile, day: WeekDay): DayMealPlan
    suspend fun chooseOption(day: WeekDay, slot: MealSlot, recipeId: String)
    suspend fun setPlatePhoto(day: WeekDay, slot: MealSlot, recipeId: String, uri: String?)
    fun ingredientCatalog(): List<Ingredient>
    fun choicesChanged(): Flow<Long>
}

interface CommunityRepository {
    fun feed(sportId: String?): Flow<List<Post>>
    fun myPosts(): Flow<List<Post>>
    fun followingCount(): Flow<Int>
    suspend fun toggleLike(postId: String)
    suspend fun react(postId: String, reaction: Reaction)
    suspend fun toggleFollow(authorId: String)
    suspend fun publish(imageUri: String?, caption: String, sportId: String): String
    suspend fun syncMeWithProfile(profile: UserProfile)
}

interface LearnRepository {
    fun articles(category: com.forma.app.domain.model.ArticleCategory?): Flow<List<Article>>
    fun article(id: String): Flow<Article?>
    fun recommended(): Flow<List<Article>>
    suspend fun toggleSaved(id: String)
}

interface ChatRepository {
    fun messages(): Flow<List<ChatMessage>>
    suspend fun send(text: String)
    suspend fun clear()
    val suggestedQuestions: List<String>
}

/**
 * Motor de IA de FORMA: genera la rutina semanal, el plan de comidas y responde el chat.
 * `LocalAiRepository` es determinista y funciona sin API key; `RemoteAiRepository` conecta un
 * proveedor real y cae de vuelta al local si no hay credenciales o la llamada falla.
 */
interface AiRepository {
    val providerName: String
    suspend fun generateWeeklyRoutine(profile: UserProfile): WeeklyRoutine
    suspend fun generateDayMeals(
        profile: UserProfile,
        day: WeekDay,
    ): Map<MealSlot, List<com.forma.app.domain.model.Recipe>>
    suspend fun answer(
        profile: UserProfile?,
        history: List<ChatMessage>,
        question: String,
    ): String
}
