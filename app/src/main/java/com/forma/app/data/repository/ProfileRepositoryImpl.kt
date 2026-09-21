package com.forma.app.data.repository

import com.forma.app.data.local.dao.ProfileDao
import com.forma.app.data.local.entity.UserProfileEntity
import com.forma.app.data.remote.CloudSyncManager
import com.forma.app.data.remote.FormaCloudStore
import com.forma.app.domain.model.ExperienceLevel
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.Sport
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao,
    private val cloud: FormaCloudStore,
    private val sync: CloudSyncManager,
) : ProfileRepository {

    override val profile: Flow<UserProfile?> = dao.observe().map { it?.toDomain() }

    override suspend fun current(): UserProfile? = dao.current()?.toDomain()

    override suspend fun save(profile: UserProfile) {
        val photo = sync.pushLocalProfilePhotoIfNeeded(profile.photoUri)
        val toSave = if (photo != profile.photoUri) profile.copy(photoUri = photo) else profile
        dao.upsert(toSave.toEntity())
        val uid = sync.currentUid() ?: return
        if (cloud.isEnabled) runCatching { cloud.upsertProfile(uid, toSave) }
    }

    override suspend fun updatePhoto(uri: String?) {
        val uploaded = sync.pushLocalProfilePhotoIfNeeded(uri)
        dao.updatePhoto(uploaded)
        val uid = sync.currentUid() ?: return
        val current = dao.current()?.toDomain() ?: return
        if (cloud.isEnabled) {
            runCatching { cloud.upsertProfile(uid, current.copy(photoUri = uploaded)) }
        }
    }

    override suspend fun clear() = dao.clear()
}

fun UserProfileEntity.toDomain() = UserProfile(
    id = id,
    name = name,
    email = email,
    photoUri = photoUri,
    age = age,
    weightKg = weightKg,
    heightCm = heightCm,
    sport = Sport.fromId(sportId),
    level = ExperienceLevel.fromId(levelId),
    goal = Goal.fromId(goalId),
    equipment = equipment.toSet(),
    likedIngredients = likedIngredients.toSet(),
    onboardingCompleted = onboardingCompleted,
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    name = name,
    email = email,
    photoUri = photoUri,
    age = age,
    weightKg = weightKg,
    heightCm = heightCm,
    sportId = sport.id,
    levelId = level.id,
    goalId = goal.id,
    equipment = equipment.toList(),
    likedIngredients = likedIngredients.toList(),
    onboardingCompleted = onboardingCompleted,
)
