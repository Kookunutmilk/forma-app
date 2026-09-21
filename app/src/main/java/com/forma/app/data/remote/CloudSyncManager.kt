package com.forma.app.data.remote

import com.forma.app.BuildConfig
import com.forma.app.data.local.dao.ChatDao
import com.forma.app.data.local.dao.CommunityDao
import com.forma.app.data.local.dao.LearnDao
import com.forma.app.data.local.dao.NutritionDao
import com.forma.app.data.local.dao.ProfileDao
import com.forma.app.data.local.dao.TrainingDao
import com.forma.app.data.local.entity.AuthorEntity
import com.forma.app.data.repository.ME_AUTHOR_ID
import com.forma.app.data.repository.toEntity
import com.forma.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orquesta el pull inicial (nube → Room) cuando hay sesión y Firebase activo.
 * Los writes van Room-first desde cada repositorio y luego empujan a la nube.
 */
@Singleton
class CloudSyncManager @Inject constructor(
    private val cloud: FormaCloudStore,
    private val auth: AuthRepository,
    private val profileDao: ProfileDao,
    private val trainingDao: TrainingDao,
    private val nutritionDao: NutritionDao,
    private val chatDao: ChatDao,
    private val learnDao: LearnDao,
    private val communityDao: CommunityDao,
) {
    private val mutex = Mutex()
    @Volatile private var lastPulledUid: String? = null

    val enabled: Boolean get() = cloud.isEnabled && BuildConfig.HAS_FIREBASE

    suspend fun currentUid(): String? = auth.currentUser.first()?.uid

    /** Baja los datos del usuario y el feed público a Room. Idempotente por uid. */
    suspend fun pullIfNeeded(force: Boolean = false) {
        mutex.withLock {
            if (!enabled) return@withLock
            val uid = currentUid() ?: return@withLock
            if (!force && lastPulledUid == uid) return@withLock

            runCatching {
                val snapshot = cloud.pullUserData(uid)
                snapshot.profile?.let { profileDao.upsert(it.toEntity()) }

                if (snapshot.exerciseLogs.isNotEmpty()) {
                    trainingDao.clearLogs()
                    trainingDao.upsertLogs(snapshot.exerciseLogs)
                }
                if (snapshot.sessions.isNotEmpty()) {
                    trainingDao.clearSessions()
                    trainingDao.upsertSessions(snapshot.sessions)
                }
                if (snapshot.mealChoices.isNotEmpty()) {
                    nutritionDao.clear()
                    nutritionDao.upsertAll(snapshot.mealChoices)
                }
                if (snapshot.chatMessages.isNotEmpty()) {
                    chatDao.clear()
                    chatDao.insertAll(snapshot.chatMessages)
                }
                if (snapshot.articleStates.isNotEmpty()) {
                    learnDao.clear()
                    learnDao.upsertAll(snapshot.articleStates)
                }

                val community = cloud.pullCommunityFeed(uid)
                if (community.posts.isNotEmpty()) {
                    communityDao.clearRemotePosts()
                    communityDao.upsertPosts(community.posts)
                }
                if (community.authors.isNotEmpty()) {
                    val withFollow = community.authors.map { author ->
                        author.copy(
                            isMe = author.id == uid,
                            following = author.id in snapshot.followingIds,
                        )
                    }
                    communityDao.upsertAuthors(withFollow.filterNot { it.id == ME_AUTHOR_ID })
                    communityDao.author(ME_AUTHOR_ID)?.let { me ->
                        communityDao.upsertAuthor(
                            AuthorEntity(
                                id = uid,
                                name = me.name,
                                handle = me.handle,
                                photoUri = me.photoUri,
                                sportId = me.sportId,
                                isMe = true,
                                following = false,
                            ),
                        )
                    }
                }

                lastPulledUid = uid
            }
        }
    }

    suspend fun pushLocalProfilePhotoIfNeeded(localPath: String?): String? {
        if (!enabled || localPath.isNullOrBlank()) return localPath
        if (localPath.startsWith("http")) return localPath
        val uid = currentUid() ?: return localPath
        return cloud.uploadUserFile(uid, localPath, "profile") ?: localPath
    }

    suspend fun pushLocalFile(localPath: String?, folder: String): String? {
        if (!enabled || localPath.isNullOrBlank()) return localPath
        if (localPath.startsWith("http")) return localPath
        val uid = currentUid() ?: return localPath
        return cloud.uploadUserFile(uid, localPath, folder) ?: localPath
    }
}
