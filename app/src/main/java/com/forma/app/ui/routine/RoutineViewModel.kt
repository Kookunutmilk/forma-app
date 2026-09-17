package com.forma.app.ui.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.data.repository.exerciseKey
import com.forma.app.domain.model.RoutineSession
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.model.WeeklyRoutine
import com.forma.app.domain.repository.ProfileRepository
import com.forma.app.domain.repository.TrainingRepository
import com.forma.app.ui.home.todayWeekDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestTimer(
    val totalSeconds: Int,
    val remainingSeconds: Int,
    val running: Boolean,
) {
    val progress: Float
        get() = if (totalSeconds == 0) 0f else remainingSeconds.toFloat() / totalSeconds
}

sealed interface RoutineUiState {
    data object Loading : RoutineUiState
    data class Error(val message: String) : RoutineUiState
    data class Content(
        val profile: UserProfile,
        val routine: WeeklyRoutine,
        val selectedDay: WeekDay,
        val session: RoutineSession?,
        val completedKeys: Set<String>,
        val restTimer: RestTimer?,
    ) : RoutineUiState {
        val doneCount: Int
            get() = session?.exercises?.count {
                exerciseKey(selectedDay, it.id) in completedKeys
            } ?: 0

        val progress: Float
            get() {
                val total = session?.exercises?.size ?: 0
                return if (total == 0) 0f else doneCount.toFloat() / total
            }

        val allDone: Boolean
            get() = session != null && session.exercises.isNotEmpty() &&
                doneCount == session.exercises.size
    }
}

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val trainingRepository: TrainingRepository,
) : ViewModel() {

    private val selectedDay = MutableStateFlow(todayWeekDay())
    private val routine = MutableStateFlow<WeeklyRoutine?>(null)
    private val failure = MutableStateFlow<String?>(null)
    private val restTimer = MutableStateFlow<RestTimer?>(null)

    private var timerJob: Job? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            profileRepository.profile.filterNotNull().collect { profile ->
                runCatching { trainingRepository.weeklyRoutine(profile) }
                    .onSuccess {
                        failure.value = null
                        routine.value = it
                    }
                    .onFailure { failure.value = "No pudimos generar tu rutina semanal." }
            }
        }
    }

    val state: StateFlow<RoutineUiState> = combine(
        profileRepository.profile,
        routine,
        selectedDay,
        trainingRepository.completedExerciseKeys(),
        combine(restTimer, failure) { timer, error -> timer to error },
    ) { profile, weekly, day, completed, (timer, error) ->
        when {
            error != null -> RoutineUiState.Error(error)
            profile == null || weekly == null -> RoutineUiState.Loading
            else -> RoutineUiState.Content(
                profile = profile,
                routine = weekly,
                selectedDay = day,
                session = weekly.sessionFor(day),
                completedKeys = completed,
                restTimer = timer,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RoutineUiState.Loading)

    fun selectDay(day: WeekDay) {
        selectedDay.value = day
        stopTimer()
    }

    fun toggleExercise(exerciseId: String, completed: Boolean, restSeconds: Int) {
        viewModelScope.launch {
            trainingRepository.setExerciseCompleted(selectedDay.value, exerciseId, completed)
        }
        if (completed) startTimer(restSeconds)
    }

    fun resetDay() {
        viewModelScope.launch { trainingRepository.resetDay(selectedDay.value) }
        stopTimer()
    }

    fun startTimer(seconds: Int) {
        timerJob?.cancel()
        restTimer.value = RestTimer(seconds, seconds, running = true)
        timerJob = viewModelScope.launch {
            while ((restTimer.value?.remainingSeconds ?: 0) > 0) {
                delay(1_000)
                restTimer.update { current ->
                    current?.copy(remainingSeconds = (current.remainingSeconds - 1).coerceAtLeast(0))
                }
            }
            restTimer.update { it?.copy(running = false) }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        restTimer.value = null
    }

    fun retry() {
        failure.value = null
        load()
    }
}
