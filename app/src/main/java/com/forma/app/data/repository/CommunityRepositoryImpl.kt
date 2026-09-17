package com.forma.app.data.repository

import com.forma.app.data.catalog.CommunitySeed
import com.forma.app.data.local.dao.CommunityDao
import com.forma.app.data.local.entity.AuthorEntity
import com.forma.app.data.local.entity.PostEntity
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
) : CommunityRepository {

    private val postsWithAuthors: Flow<List<Post>> =
        combine(dao.observePosts(), dao.observeAuthors()) { posts, authors ->
            val byId = authors.associateBy { it.id }
            posts.mapNotNull { post ->
                val author = byId[post.authorId] ?: return@mapNotNull null
                post.toDomain(author)
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
        dao.upsertPost(
            post.copy(
                likedByMe = !post.likedByMe,
                likes = (post.likes + if (post.likedByMe) -1 else 1).coerceAtLeast(0),
            ),
        )
    }

    override suspend fun react(postId: String, reaction: Reaction) {
        val post = dao.post(postId) ?: return
        val counts = post.reactionsJson.toReactionMap().toMutableMap()
        val previous = post.myReaction

        if (previous == reaction.id) {
            counts[reaction.id] = ((counts[reaction.id] ?: 1) - 1).coerceAtLeast(0)
            dao.upsertPost(post.copy(reactionsJson = counts.toJson(), myReaction = null))
            return
        }
        if (previous != null) {
            counts[previous] = ((counts[previous] ?: 1) - 1).coerceAtLeast(0)
        }
        counts[reaction.id] = (counts[reaction.id] ?: 0) + 1
        dao.upsertPost(post.copy(reactionsJson = counts.toJson(), myReaction = reaction.id))
    }

    override suspend fun toggleFollow(authorId: String) {
        val author = dao.author(authorId) ?: return
        dao.upsertAuthor(author.copy(following = !author.following))
    }

    override suspend fun publish(imageUri: String?, caption: String, sportId: String): String {
        val id = "post_${UUID.randomUUID()}"
        dao.upsertPost(
            PostEntity(
                id = id,
                authorId = ME_AUTHOR_ID,
                imageKey = null,
                imageUri = imageUri,
                caption = caption,
                sportId = sportId,
                createdAtMillis = System.currentTimeMillis(),
                likes = 0,
                likedByMe = false,
                reactionsJson = "{}",
                myReaction = null,
            ),
        )
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
    }

    /** Siembra el feed la primera vez para que la comunidad no arranque vacía. */
    suspend fun seedIfEmpty() {
        if (dao.postCount() > 0) return
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
