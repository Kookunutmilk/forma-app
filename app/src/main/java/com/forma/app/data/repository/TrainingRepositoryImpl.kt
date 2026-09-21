package com.forma.app.data.repository

import com.forma.app.data.local.dao.TrainingDao
import com.forma.app.data.local.entity.ExerciseLogEntity
import com.forma.app.data.local.entity.WorkoutSessionEntity
import com.forma.app.data.remote.CloudSyncManager
import com.forma.app.data.remote.FormaCloudStore
import com.forma.app.domain.model.TrainingStats
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.model.WeeklyRoutine
import com.forma.app.domain.model.WorkoutSession
import com.forma.app.domain.repository.AiRepository
import com.forma.app.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Clave estable para marcar un ejercicio dentro de un día concreto de la semana. */
fun exerciseKey(day: WeekDay, exerciseId: String) = "${day.index}|$exerciseId"

@Singleton
class TrainingRepositoryImpl @Inject constructor(
    private val dao: TrainingDao,
    private val ai: AiRepository,
    private val cloud: FormaCloudStore,
    private val sync: CloudSyncManager,
) : TrainingRepository {

    override suspend fun weeklyRoutine(profile: UserProfile): WeeklyRoutine =
        ai.generateWeeklyRoutine(profile)

    override fun completedExerciseKeys(): Flow<Set<String>> = dao.observeCompleted().map { logs ->
        logs.map { exerciseKey(WeekDay.fromIndex(it.dayIndex), it.exerciseId) }.toSet()
    }

    override suspend fun setExerciseCompleted(
        day: WeekDay,
        exerciseId: String,
        completed: Boolean,
    ) {
        val log = ExerciseLogEntity(
            dayIndex = day.index,
            exerciseId = exerciseId,
            completed = completed,
            updatedAt = System.currentTimeMillis(),
        )
        dao.upsertLog(log)
        val uid = sync.currentUid() ?: return
        if (cloud.isEnabled) runCatching { cloud.upsertExerciseLog(uid, log) }
    }

    override suspend fun resetDay(day: WeekDay) {
        dao.clearDay(day.index)
        val uid = sync.currentUid() ?: return
        if (cloud.isEnabled) runCatching { cloud.clearExerciseDay(uid, day.index) }
    }

    override fun sessions(): Flow<List<WorkoutSession>> =
        dao.observeSessions().map { list -> list.map { it.toDomain() } }

    override fun stats(): Flow<TrainingStats> =
        combine(dao.observeSessions(), dao.observeCompleted()) { sessions, logs ->
            val weekStart = startOfWeekMillis()
            val thisWeek = sessions.filter { it.dateEpochMillis >= weekStart }
            TrainingStats(
                workoutsThisWeek = thisWeek.size,
                streakDays = streakOf(sessions.map { it.dateEpochMillis }),
                kcalThisWeek = thisWeek.sumOf { it.kcal },
                completedDays = thisWeek.map { dayOf(it.dateEpochMillis) }.toSet(),
            ).let { stats ->
                val partialDays = logs.map { WeekDay.fromIndex(it.dayIndex) }.toSet()
                stats.copy(completedDays = stats.completedDays + partialDays)
            }
        }

    override suspend fun finishWorkout(session: WorkoutSession) {
        val photo = sync.pushLocalFile(session.photoUri, "workouts")
        val entity = WorkoutSessionEntity(
            id = session.id,
            dateEpochMillis = session.dateEpochMillis,
            sportId = session.sportId,
            title = session.title,
            durationMinutes = session.durationMinutes,
            kcal = session.kcal,
            photoUri = photo,
            note = session.note,
        )
        dao.insertSession(entity)
        val uid = sync.currentUid() ?: return
        if (cloud.isEnabled) runCatching { cloud.upsertWorkoutSession(uid, entity) }
    }

    private fun WorkoutSessionEntity.toDomain() = WorkoutSession(
        id = id,
        dateEpochMillis = dateEpochMillis,
        sportId = sportId,
        title = title,
        durationMinutes = durationMinutes,
        kcal = kcal,
        photoUri = photoUri,
        note = note,
    )

    private fun dayOf(millis: Long): WeekDay {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        return WeekDay.fromIndex(calendar.get(Calendar.DAY_OF_WEEK) - 2)
    }

    private fun startOfWeekMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun streakOf(dates: List<Long>): Int {
        if (dates.isEmpty()) return 0
        val days = dates.map { TimeUnit.MILLISECONDS.toDays(it) }.distinct().sortedDescending()
        val today = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
        if (days.first() < today - 1) return 0
        var streak = 1
        for (i in 1 until days.size) {
            if (days[i - 1] - days[i] == 1L) streak++ else break
        }
        return streak
    }
}
