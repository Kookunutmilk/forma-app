package com.forma.app.data.remote

import com.forma.app.data.local.entity.ArticleStateEntity
import com.forma.app.data.local.entity.AuthorEntity
import com.forma.app.data.local.entity.ChatMessageEntity
import com.forma.app.data.local.entity.ExerciseLogEntity
import com.forma.app.data.local.entity.MealChoiceEntity
import com.forma.app.data.local.entity.PostEntity
import com.forma.app.data.local.entity.WorkoutSessionEntity
import com.forma.app.domain.model.UserProfile

/**
 * Capa remota de FORMA. Sin `google-services.json` se usa [NoOpCloudStore] y todo sigue
 * viviendo solo en Room. Con Firebase, [FirebaseCloudStore] sincroniza perfil, rutina,
 * nutrición, chat, guardados, comunidad y fotos.
 */
interface FormaCloudStore {
    val isEnabled: Boolean

    suspend fun pullUserData(uid: String): UserCloudSnapshot

    suspend fun upsertProfile(uid: String, profile: UserProfile)

    suspend fun upsertExerciseLog(uid: String, log: ExerciseLogEntity)

    suspend fun clearExerciseDay(uid: String, dayIndex: Int)

    suspend fun upsertWorkoutSession(uid: String, session: WorkoutSessionEntity)

    suspend fun upsertMealChoice(uid: String, choice: MealChoiceEntity)

    suspend fun upsertChatMessage(uid: String, message: ChatMessageEntity)

    suspend fun clearChat(uid: String)

    suspend fun upsertArticleState(uid: String, state: ArticleStateEntity)

    suspend fun publishPost(uid: String, post: PostEntity, author: AuthorEntity)

    suspend fun updatePost(post: PostEntity)

    suspend fun upsertEngagement(uid: String, postId: String, liked: Boolean, reaction: String?)

    suspend fun upsertAuthorPublic(author: AuthorEntity)

    suspend fun setFollowing(uid: String, authorId: String, following: Boolean)

    suspend fun pullCommunityFeed(uid: String): CommunityCloudSnapshot

    /** Sube un archivo local y devuelve la URL pública, o null si no hay nube / falla. */
    suspend fun uploadUserFile(uid: String, localPath: String, folder: String): String?
}

data class UserCloudSnapshot(
    val profile: UserProfile? = null,
    val exerciseLogs: List<ExerciseLogEntity> = emptyList(),
    val sessions: List<WorkoutSessionEntity> = emptyList(),
    val mealChoices: List<MealChoiceEntity> = emptyList(),
    val chatMessages: List<ChatMessageEntity> = emptyList(),
    val articleStates: List<ArticleStateEntity> = emptyList(),
    val followingIds: Set<String> = emptySet(),
)

data class CommunityCloudSnapshot(
    val posts: List<PostEntity> = emptyList(),
    val authors: List<AuthorEntity> = emptyList(),
)

class NoOpCloudStore : FormaCloudStore {
    override val isEnabled: Boolean = false
    override suspend fun pullUserData(uid: String) = UserCloudSnapshot()
    override suspend fun upsertProfile(uid: String, profile: UserProfile) = Unit
    override suspend fun upsertExerciseLog(uid: String, log: ExerciseLogEntity) = Unit
    override suspend fun clearExerciseDay(uid: String, dayIndex: Int) = Unit
    override suspend fun upsertWorkoutSession(uid: String, session: WorkoutSessionEntity) = Unit
    override suspend fun upsertMealChoice(uid: String, choice: MealChoiceEntity) = Unit
    override suspend fun upsertChatMessage(uid: String, message: ChatMessageEntity) = Unit
    override suspend fun clearChat(uid: String) = Unit
    override suspend fun upsertArticleState(uid: String, state: ArticleStateEntity) = Unit
    override suspend fun publishPost(uid: String, post: PostEntity, author: AuthorEntity) = Unit
    override suspend fun updatePost(post: PostEntity) = Unit
    override suspend fun upsertEngagement(
        uid: String,
        postId: String,
        liked: Boolean,
        reaction: String?,
    ) = Unit
    override suspend fun upsertAuthorPublic(author: AuthorEntity) = Unit
    override suspend fun setFollowing(uid: String, authorId: String, following: Boolean) = Unit
    override suspend fun pullCommunityFeed(uid: String) = CommunityCloudSnapshot()
    override suspend fun uploadUserFile(uid: String, localPath: String, folder: String): String? = null
}
