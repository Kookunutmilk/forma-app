package com.forma.app.data.repository

import com.forma.app.data.local.dao.ProfileDao
import com.forma.app.data.local.entity.UserProfileEntity
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
) : ProfileRepository {

    override val profile: Flow<UserProfile?> = dao.observe().map { it?.toDomain() }

    override suspend fun current(): UserProfile? = dao.current()?.toDomain()

    override suspend fun save(profile: UserProfile) = dao.upsert(profile.toEntity())

    override suspend fun updatePhoto(uri: String?) = dao.updatePhoto(uri)

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
