package com.forma.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.model.Article
import com.forma.app.domain.model.RoutineSession
import com.forma.app.domain.model.TrainingStats
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.repository.LearnRepository
import com.forma.app.domain.repository.ProfileRepository
import com.forma.app.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

fun todayWeekDay(): WeekDay =
    WeekDay.fromIndex(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Content(
        val profile: UserProfile,
        val today: WeekDay,
        val todaySession: RoutineSession?,
        val stats: TrainingStats,
        val articles: List<Article>,
        val greeting: String,
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val trainingRepository: TrainingRepository,
    learnRepository: LearnRepository,
) : ViewModel() {

    private val todaySession = MutableStateFlow<RoutineSession?>(null)
    private val failure = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            profileRepository.profile.filterNotNull().collect { profile ->
                runCatching { trainingRepository.weeklyRoutine(profile) }
                    .onSuccess { routine ->
                        failure.value = null
                        todaySession.value = routine.sessionFor(todayWeekDay())
                    }
                    .onFailure { failure.value = "No pudimos generar tu rutina de hoy." }
            }
        }
    }

    val state: StateFlow<HomeUiState> = combine(
        profileRepository.profile,
        trainingRepository.stats(),
        learnRepository.recommended(),
        todaySession,
        failure,
    ) { profile, stats, articles, session, error ->
        when {
            error != null -> HomeUiState.Error(error)
            profile == null -> HomeUiState.Loading
            else -> HomeUiState.Content(
                profile = profile,
                today = todayWeekDay(),
                todaySession = session,
                stats = stats,
                articles = articles,
                greeting = greeting(),
            )
        }
    }
        .catch { emit(HomeUiState.Error("No pudimos cargar tu inicio.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

    val unreadNotifications: StateFlow<Int> = trainingRepository.sessions()
        .map { sessions -> if (sessions.isEmpty()) 3 else 2 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 3)

    fun retry() {
        failure.value = null
        viewModelScope.launch {
            val profile = profileRepository.current() ?: return@launch
            runCatching { trainingRepository.weeklyRoutine(profile) }
                .onSuccess { todaySession.value = it.sessionFor(todayWeekDay()) }
                .onFailure { failure.value = "No pudimos generar tu rutina de hoy." }
        }
    }

    private fun greeting(): String =
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Buenos días,"
            in 12..18 -> "Buenas tardes,"
            else -> "Buenas noches,"
        }
}
