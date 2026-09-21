package com.forma.app.data.repository

import com.forma.app.BuildConfig
import com.forma.app.data.catalog.CommunitySeed
import com.forma.app.data.local.dao.CommunityDao
import com.forma.app.data.local.entity.AuthorEntity
import com.forma.app.data.local.entity.PostEntity
import com.forma.app.data.remote.CloudSyncManager
import com.forma.app.data.remote.FormaCloudStore
import com.forma.app.domain.model.Author
import com.forma.app.domain.model.Post
import com.forma.app.domain.model.Reaction
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

const val ME_AUTHOR_ID = "me"

@Singleton
class CommunityRepositoryImpl @Inject constructor(
    private val dao: CommunityDao,
    private val cloud: FormaCloudStore,
    private val sync: CloudSyncManager,
) : CommunityRepository {

    private val postsWithAuthors: Flow<List<Post>> =
        combine(dao.observePosts(), dao.observeAuthors()) { posts, authors ->
            val byId = authors.associateBy { it.id }
            val me = byId[ME_AUTHOR_ID]
            posts.mapNotNull { post ->
                val author = byId[post.authorId]
                    ?: me?.takeIf { post.authorId == ME_AUTHOR_ID }
                    ?: return@mapNotNull null
                post.toDomain(
                    author.copy(isMe = author.isMe || author.id == ME_AUTHOR_ID || me?.let { author.id == it.id } == true),
                )
            }
        }

    override fun feed(sportId: String?): Flow<List<Post>> = postsWithAuthors.map { posts ->
        if (sportId == null) posts else posts.filter { it.sportId == sportId }
    }

    override fun myPosts(): Flow<List<Post>> =
        postsWithAuthors.map { posts -> posts.filter { it.author.isMe } }

    override fun followingCount(): Flow<Int> =
        dao.observeAuthors().map { authors -> authors.count { it.following } }

    override suspend fun toggleLike(postId: String) {
        val post = dao.post(postId) ?: return
        val updated = post.copy(
            likedByMe = !post.likedByMe,
            likes = (post.likes + if (post.likedByMe) -1 else 1).coerceAtLeast(0),
        )
        dao.upsertPost(updated)
        val uid = sync.currentUid()
        if (cloud.isEnabled && uid != null) {
            runCatching {
                cloud.updatePost(updated)
                cloud.upsertEngagement(uid, postId, updated.likedByMe, updated.myReaction)
            }
        }
    }

    override suspend fun react(postId: String, reaction: Reaction) {
        val post = dao.post(postId) ?: return
        val counts = post.reactionsJson.toReactionMap().toMutableMap()
        val previous = post.myReaction

        val updated = if (previous == reaction.id) {
            counts[reaction.id] = ((counts[reaction.id] ?: 1) - 1).coerceAtLeast(0)
            post.copy(reactionsJson = counts.toJson(), myReaction = null)
        } else {
            if (previous != null) {
                counts[previous] = ((counts[previous] ?: 1) - 1).coerceAtLeast(0)
            }
            counts[reaction.id] = (counts[reaction.id] ?: 0) + 1
            post.copy(reactionsJson = counts.toJson(), myReaction = reaction.id)
        }
        dao.upsertPost(updated)
        val uid = sync.currentUid()
        if (cloud.isEnabled && uid != null) {
            runCatching {
                cloud.updatePost(updated)
                cloud.upsertEngagement(uid, postId, updated.likedByMe, updated.myReaction)
            }
        }
    }

    override suspend fun toggleFollow(authorId: String) {
        val author = dao.author(authorId) ?: return
        val updated = author.copy(following = !author.following)
        dao.upsertAuthor(updated)
        val uid = sync.currentUid() ?: return
        if (cloud.isEnabled) {
            runCatching { cloud.setFollowing(uid, authorId, updated.following) }
        }
    }

    override suspend fun publish(imageUri: String?, caption: String, sportId: String): String {
        val id = "post_${UUID.randomUUID()}"
        val uploaded = sync.pushLocalFile(imageUri, "posts")
        val uid = sync.currentUid()
        val authorId = if (cloud.isEnabled && uid != null) uid else ME_AUTHOR_ID
        val post = PostEntity(
            id = id,
            authorId = authorId,
            imageKey = null,
            imageUri = uploaded,
            caption = caption,
            sportId = sportId,
            createdAtMillis = System.currentTimeMillis(),
            likes = 0,
            likedByMe = false,
            reactionsJson = "{}",
            myReaction = null,
        )
        dao.upsertPost(post)

        val me = dao.author(ME_AUTHOR_ID)
        if (uid != null && me != null) {
            dao.upsertAuthor(me.copy(id = uid, isMe = true))
        }
        if (cloud.isEnabled && uid != null && me != null) {
            runCatching {
                cloud.publishPost(
                    uid = uid,
                    post = post,
                    author = me.copy(id = uid, isMe = false),
                )
            }
        }
        return id
    }

    override suspend fun syncMeWithProfile(profile: UserProfile) {
        dao.upsertAuthor(
            AuthorEntity(
                id = ME_AUTHOR_ID,
                name = profile.name,
                handle = "@" + profile.name.lowercase()
                    .replace(" ", ".")
                    .filter { it.isLetterOrDigit() || it == '.' }
                    .ifBlank { "atleta" },
                photoUri = profile.photoUri,
                sportId = profile.sport.id,
                isMe = true,
                following = false,
            ),
        )
        val uid = sync.currentUid()
        if (cloud.isEnabled && uid != null) {
            val me = dao.author(ME_AUTHOR_ID) ?: return
            runCatching { cloud.upsertAuthorPublic(me.copy(id = uid, isMe = false)) }
        }
    }

    /**
     * Siembra el feed local la primera vez. Con Firebase activo el feed viene de la nube;
     * solo sembramos si aún no hay posts (p. ej. primera apertura offline).
     */
    suspend fun seedIfEmpty() {
        if (dao.postCount() > 0) return
        if (BuildConfig.HAS_FIREBASE && cloud.isEnabled) {
            // Intenta tirar del feed remoto antes de caer al seed.
            runCatching { sync.pullIfNeeded(force = true) }
            if (dao.postCount() > 0) return
        }
        dao.upsertAuthors(CommunitySeed.authors)
        dao.upsertPosts(CommunitySeed.posts(System.currentTimeMillis()))
    }
}

private fun String.toReactionMap(): Map<String, Int> = runCatching {
    val json = JSONObject(this)
    json.keys().asSequence().associateWith { json.optInt(it, 0) }
}.getOrDefault(emptyMap())

private fun Map<String, Int>.toJson(): String =
    JSONObject(filterValues { it > 0 }.mapValues { it.value as Any }).toString()

private fun PostEntity.toDomain(author: AuthorEntity) = Post(
    id = id,
    author = Author(
        id = author.id,
        name = author.name,
        handle = author.handle,
        photo = author.photoUri,
        sportId = author.sportId,
        isMe = author.isMe,
        following = author.following,
    ),
    imageKey = imageKey,
    imageUri = imageUri,
    caption = caption,
    sportId = sportId,
    createdAtMillis = createdAtMillis,
    likes = likes,
    likedByMe = likedByMe,
    reactions = reactionsJson.toReactionMap(),
    myReaction = myReaction,
)
