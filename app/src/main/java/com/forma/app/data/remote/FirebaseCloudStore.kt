package com.forma.app.data.remote

import android.net.Uri
import com.forma.app.data.local.entity.ArticleStateEntity
import com.forma.app.data.local.entity.AuthorEntity
import com.forma.app.data.local.entity.ChatMessageEntity
import com.forma.app.data.local.entity.ExerciseLogEntity
import com.forma.app.data.local.entity.MealChoiceEntity
import com.forma.app.data.local.entity.PostEntity
import com.forma.app.data.local.entity.WorkoutSessionEntity
import com.forma.app.data.repository.toDomain
import com.forma.app.data.repository.toEntity
import com.forma.app.domain.model.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persistencia en Cloud Firestore + Storage.
 *
 * Esquema:
 * - `users/{uid}` perfil
 * - `users/{uid}/exercise_logs/{day_exercise}`
 * - `users/{uid}/workout_sessions/{id}`
 * - `users/{uid}/meal_choices/{day_slot}`
 * - `users/{uid}/chat_messages/{id}`
 * - `users/{uid}/article_states/{articleId}`
 * - `users/{uid}/following/{authorId}`
 * - `posts/{postId}` feed público
 * - `authors/{authorId}` perfiles públicos de comunidad
 * - Storage: `users/{uid}/{folder}/{file}`
 */
@Singleton
class FirebaseCloudStore @Inject constructor() : FormaCloudStore {

    override val isEnabled: Boolean = true

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private fun userDoc(uid: String) = db.collection(USERS).document(uid)

    override suspend fun pullUserData(uid: String): UserCloudSnapshot {
        val profileSnap = userDoc(uid).get().await()
        val profile = profileSnap.takeIf { it.exists() }?.data?.toUserProfile(uid)

        val logs = userDoc(uid).collection(EXERCISE_LOGS).get().await().documents.mapNotNull {
            it.data?.toExerciseLog()
        }
        val sessions = userDoc(uid).collection(WORKOUT_SESSIONS).get().await().documents.mapNotNull {
            it.data?.toWorkoutSession(it.id)
        }
        val meals = userDoc(uid).collection(MEAL_CHOICES).get().await().documents.mapNotNull {
            it.data?.toMealChoice()
        }
        val chat = userDoc(uid).collection(CHAT_MESSAGES).get().await().documents.mapNotNull {
            it.data?.toChatMessage(it.id)
        }
        val articles = userDoc(uid).collection(ARTICLE_STATES).get().await().documents.mapNotNull {
            it.data?.toArticleState(it.id)
        }
        val following = userDoc(uid).collection(FOLLOWING).get().await().documents
            .map { it.id }
            .toSet()

        return UserCloudSnapshot(
            profile = profile,
            exerciseLogs = logs,
            sessions = sessions,
            mealChoices = meals,
            chatMessages = chat,
            articleStates = articles,
            followingIds = following,
        )
    }

    override suspend fun upsertProfile(uid: String, profile: UserProfile) {
        val entity = profile.toEntity()
        userDoc(uid).set(
            mapOf(
                "id" to entity.id,
                "name" to entity.name,
                "email" to entity.email,
                "photoUri" to entity.photoUri,
                "age" to entity.age,
                "weightKg" to entity.weightKg,
                "heightCm" to entity.heightCm,
                "sportId" to entity.sportId,
                "levelId" to entity.levelId,
                "goalId" to entity.goalId,
                "equipment" to entity.equipment,
                "likedIngredients" to entity.likedIngredients,
                "onboardingCompleted" to entity.onboardingCompleted,
                "provider" to entity.provider,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        ).await()

        // Espejo público para la comunidad.
        db.collection(AUTHORS).document(uid).set(
            mapOf(
                "id" to uid,
                "name" to entity.name,
                "handle" to ("@" + entity.name.lowercase()
                    .replace(" ", ".")
                    .filter { it.isLetterOrDigit() || it == '.' }
                    .ifBlank { "atleta" }),
                "photoUri" to entity.photoUri,
                "sportId" to entity.sportId,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        ).await()
    }

    override suspend fun upsertExerciseLog(uid: String, log: ExerciseLogEntity) {
        userDoc(uid).collection(EXERCISE_LOGS)
            .document("${log.dayIndex}_${log.exerciseId}")
            .set(
                mapOf(
                    "dayIndex" to log.dayIndex,
                    "exerciseId" to log.exerciseId,
                    "completed" to log.completed,
                    "updatedAt" to log.updatedAt,
                ),
            ).await()
    }

    override suspend fun clearExerciseDay(uid: String, dayIndex: Int) {
        val snaps = userDoc(uid).collection(EXERCISE_LOGS)
            .whereEqualTo("dayIndex", dayIndex)
            .get()
            .await()
        for (doc in snaps.documents) {
            doc.reference.delete().await()
        }
    }

    override suspend fun upsertWorkoutSession(uid: String, session: WorkoutSessionEntity) {
        userDoc(uid).collection(WORKOUT_SESSIONS).document(session.id).set(
            mapOf(
                "dateEpochMillis" to session.dateEpochMillis,
                "sportId" to session.sportId,
                "title" to session.title,
                "durationMinutes" to session.durationMinutes,
                "kcal" to session.kcal,
                "photoUri" to session.photoUri,
                "note" to session.note,
            ),
        ).await()
    }

    override suspend fun upsertMealChoice(uid: String, choice: MealChoiceEntity) {
        userDoc(uid).collection(MEAL_CHOICES)
            .document("${choice.dayIndex}_${choice.slotId}")
            .set(
                mapOf(
                    "dayIndex" to choice.dayIndex,
                    "slotId" to choice.slotId,
                    "recipeId" to choice.recipeId,
                    "plateUri" to choice.plateUri,
                ),
            ).await()
    }

    override suspend fun upsertChatMessage(uid: String, message: ChatMessageEntity) {
        userDoc(uid).collection(CHAT_MESSAGES).document(message.id).set(
            mapOf(
                "text" to message.text,
                "fromUser" to message.fromUser,
                "timestampMillis" to message.timestampMillis,
            ),
        ).await()
    }

    override suspend fun clearChat(uid: String) {
        val snaps = userDoc(uid).collection(CHAT_MESSAGES).get().await()
        for (doc in snaps.documents) doc.reference.delete().await()
    }

    override suspend fun upsertArticleState(uid: String, state: ArticleStateEntity) {
        userDoc(uid).collection(ARTICLE_STATES).document(state.articleId).set(
            mapOf("saved" to state.saved),
        ).await()
    }

    override suspend fun publishPost(uid: String, post: PostEntity, author: AuthorEntity) {
        db.collection(POSTS).document(post.id).set(
            mapOf(
                "authorId" to uid,
                "authorName" to author.name,
                "authorHandle" to author.handle,
                "authorPhotoUri" to author.photoUri,
                "authorSportId" to author.sportId,
                "imageKey" to post.imageKey,
                "imageUri" to post.imageUri,
                "caption" to post.caption,
                "sportId" to post.sportId,
                "createdAtMillis" to post.createdAtMillis,
                "likes" to post.likes,
                "likedBy" to emptyList<String>(),
                "reactions" to emptyMap<String, Int>(),
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
        upsertAuthorPublic(author.copy(id = uid, isMe = false))
    }

    override suspend fun updatePost(post: PostEntity) {
        db.collection(POSTS).document(post.id).set(
            mapOf(
                "likes" to post.likes,
                "reactions" to post.reactionsJson.toReactionMap(),
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        ).await()
    }

    /** Guarda el like/reacción del usuario autenticado. */
    override suspend fun upsertEngagement(
        uid: String,
        postId: String,
        liked: Boolean,
        reaction: String?,
    ) {
        userDoc(uid).collection(ENGAGEMENT).document(postId).set(
            mapOf(
                "liked" to liked,
                "reaction" to reaction,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
        val ref = db.collection(POSTS).document(postId)
        db.runTransaction { tx ->
            val snap = tx.get(ref)
            if (!snap.exists()) return@runTransaction null
            val likedBy = (snap.get("likedBy") as? List<*>)?.mapNotNull { it as? String }.orEmpty()
                .toMutableSet()
            if (liked) likedBy.add(uid) else likedBy.remove(uid)
            tx.update(
                ref,
                mapOf(
                    "likedBy" to likedBy.toList(),
                    "likes" to likedBy.size,
                ),
            )
        }.await()
    }

    override suspend fun upsertAuthorPublic(author: AuthorEntity) {
        db.collection(AUTHORS).document(author.id).set(
            mapOf(
                "id" to author.id,
                "name" to author.name,
                "handle" to author.handle,
                "photoUri" to author.photoUri,
                "sportId" to author.sportId,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        ).await()
    }

    override suspend fun setFollowing(uid: String, authorId: String, following: Boolean) {
        val ref = userDoc(uid).collection(FOLLOWING).document(authorId)
        if (following) {
            ref.set(mapOf("since" to FieldValue.serverTimestamp())).await()
        } else {
            ref.delete().await()
        }
    }

    override suspend fun pullCommunityFeed(uid: String): CommunityCloudSnapshot {
        val postDocs = db.collection(POSTS)
            .orderBy("createdAtMillis", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .await()
            .documents

        val engagement = userDoc(uid).collection(ENGAGEMENT).get().await().documents
            .associate { it.id to it.data.orEmpty() }

        val posts = postDocs.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            val eng = engagement[doc.id].orEmpty()
            val likedBy = (data["likedBy"] as? List<*>)?.mapNotNull { it as? String }.orEmpty()
            PostEntity(
                id = doc.id,
                authorId = data.string("authorId") ?: return@mapNotNull null,
                imageKey = data.string("imageKey"),
                imageUri = data.string("imageUri"),
                caption = data.string("caption").orEmpty(),
                sportId = data.string("sportId").orEmpty(),
                createdAtMillis = data.long("createdAtMillis") ?: 0L,
                likes = data.int("likes") ?: likedBy.size,
                likedByMe = likedBy.contains(uid) || eng["liked"] == true,
                reactionsJson = (data["reactions"] as? Map<*, *>)
                    ?.mapKeys { it.key.toString() }
                    ?.mapValues { (it.value as? Number)?.toInt() ?: 0 }
                    .orEmpty()
                    .toJson(),
                myReaction = eng["reaction"] as? String,
            )
        }

        val authorIds = posts.map { it.authorId }.toSet()
        val authors = authorIds.mapNotNull { id ->
            val embedded = postDocs.firstOrNull { it.id == posts.firstOrNull { p -> p.authorId == id }?.id }
            val data = embedded?.data
            if (data != null && data.string("authorId") == id) {
                AuthorEntity(
                    id = id,
                    name = data.string("authorName").orEmpty(),
                    handle = data.string("authorHandle").orEmpty(),
                    photoUri = data.string("authorPhotoUri"),
                    sportId = data.string("authorSportId").orEmpty(),
                    isMe = id == uid,
                    following = false,
                )
            } else {
                val snap = db.collection(AUTHORS).document(id).get().await()
                snap.data?.let {
                    AuthorEntity(
                        id = id,
                        name = it.string("name").orEmpty(),
                        handle = it.string("handle").orEmpty(),
                        photoUri = it.string("photoUri"),
                        sportId = it.string("sportId").orEmpty(),
                        isMe = id == uid,
                        following = false,
                    )
                }
            }
        }

        return CommunityCloudSnapshot(posts = posts, authors = authors)
    }

    override suspend fun uploadUserFile(uid: String, localPath: String, folder: String): String? {
        val file = File(localPath)
        if (!file.exists()) return null
        return runCatching {
            val ref = storage.reference.child("users/$uid/$folder/${file.name}")
            ref.putFile(Uri.fromFile(file)).await()
            ref.downloadUrl.await().toString()
        }.getOrNull()
    }

    companion object {
        const val USERS = "users"
        const val EXERCISE_LOGS = "exercise_logs"
        const val WORKOUT_SESSIONS = "workout_sessions"
        const val MEAL_CHOICES = "meal_choices"
        const val CHAT_MESSAGES = "chat_messages"
        const val ARTICLE_STATES = "article_states"
        const val FOLLOWING = "following"
        const val ENGAGEMENT = "engagement"
        const val POSTS = "posts"
        const val AUTHORS = "authors"
    }
}

private fun Map<String, Any?>.string(key: String): String? = this[key] as? String
private fun Map<String, Any?>.int(key: String): Int? = (this[key] as? Number)?.toInt()
private fun Map<String, Any?>.long(key: String): Long? = (this[key] as? Number)?.toLong()
private fun Map<String, Any?>.bool(key: String): Boolean? = this[key] as? Boolean

private fun Map<String, Any>.toUserProfile(uid: String): UserProfile? = runCatching {
    com.forma.app.data.local.entity.UserProfileEntity(
        id = string("id") ?: uid,
        name = string("name").orEmpty(),
        email = string("email").orEmpty(),
        photoUri = string("photoUri"),
        age = int("age") ?: 25,
        weightKg = int("weightKg") ?: 70,
        heightCm = int("heightCm") ?: 170,
        sportId = string("sportId") ?: "gym",
        levelId = string("levelId") ?: "beginner",
        goalId = string("goalId") ?: "health",
        equipment = (this["equipment"] as? List<*>)?.mapNotNull { it as? String }.orEmpty(),
        likedIngredients = (this["likedIngredients"] as? List<*>)?.mapNotNull { it as? String }.orEmpty(),
        onboardingCompleted = bool("onboardingCompleted") ?: false,
        provider = string("provider") ?: "firebase",
    ).toDomain()
}.getOrNull()

private fun Map<String, Any>.toExerciseLog(): ExerciseLogEntity? = runCatching {
    ExerciseLogEntity(
        dayIndex = int("dayIndex") ?: return null,
        exerciseId = string("exerciseId") ?: return null,
        completed = bool("completed") ?: false,
        updatedAt = long("updatedAt") ?: 0L,
    )
}.getOrNull()

private fun Map<String, Any>.toWorkoutSession(id: String): WorkoutSessionEntity? = runCatching {
    WorkoutSessionEntity(
        id = id,
        dateEpochMillis = long("dateEpochMillis") ?: 0L,
        sportId = string("sportId").orEmpty(),
        title = string("title").orEmpty(),
        durationMinutes = int("durationMinutes") ?: 0,
        kcal = int("kcal") ?: 0,
        photoUri = string("photoUri"),
        note = string("note").orEmpty(),
    )
}.getOrNull()

private fun Map<String, Any>.toMealChoice(): MealChoiceEntity? = runCatching {
    MealChoiceEntity(
        dayIndex = int("dayIndex") ?: return null,
        slotId = string("slotId") ?: return null,
        recipeId = string("recipeId") ?: return null,
        plateUri = string("plateUri"),
    )
}.getOrNull()

private fun Map<String, Any>.toChatMessage(id: String): ChatMessageEntity? = runCatching {
    ChatMessageEntity(
        id = id,
        text = string("text").orEmpty(),
        fromUser = bool("fromUser") ?: false,
        timestampMillis = long("timestampMillis") ?: 0L,
    )
}.getOrNull()

private fun Map<String, Any>.toArticleState(id: String): ArticleStateEntity? = runCatching {
    ArticleStateEntity(
        articleId = id,
        saved = bool("saved") ?: false,
    )
}.getOrNull()

private fun String.toReactionMap(): Map<String, Int> = runCatching {
    val json = org.json.JSONObject(this)
    json.keys().asSequence().associateWith { json.optInt(it, 0) }
}.getOrDefault(emptyMap())

private fun Map<String, Int>.toJson(): String =
    org.json.JSONObject(filterValues { it > 0 }.mapValues { it.value as Any }).toString()
