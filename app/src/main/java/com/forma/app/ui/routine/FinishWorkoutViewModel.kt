package com.forma.app.ui.routine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.model.RoutineSession
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.model.WorkoutSession
import com.forma.app.domain.repository.CommunityRepository
import com.forma.app.domain.repository.ProfileRepository
import com.forma.app.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class FinishWorkoutUiState(
    val loading: Boolean = true,
    val profile: UserProfile? = null,
    val session: RoutineSession? = null,
    val photoUri: String? = null,
    val caption: String = "",
    val shareToCommunity: Boolean = true,
    val saving: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class FinishWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val trainingRepository: TrainingRepository,
    private val communityRepository: CommunityRepository,
) : ViewModel() {

    private val day: WeekDay =
        WeekDay.fromIndex(savedStateHandle.get<Int>("dayIndex") ?: 0)

    private val _state = MutableStateFlow(FinishWorkoutUiState())
    val state: StateFlow<FinishWorkoutUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = profileRepository.current()
            if (profile == null) {
                _state.update { it.copy(loading = false, error = "No encontramos tu perfil.") }
                return@launch
            }
            val session = runCatching { trainingRepository.weeklyRoutine(profile) }
                .getOrNull()
                ?.sessionFor(day)
            _state.update {
                it.copy(
                    loading = false,
                    profile = profile,
                    session = session,
                    caption = defaultCaption(profile, session),
                )
            }
        }
    }

    fun onPhoto(uri: String?) = _state.update { it.copy(photoUri = uri) }
    fun onCaption(value: String) = _state.update { it.copy(caption = value) }
    fun toggleShare() = _state.update { it.copy(shareToCommunity = !it.shareToCommunity) }

    fun save(onDone: (published: Boolean) -> Unit) {
        val current = _state.value
        val profile = current.profile ?: return
        if (current.saving) return
        _state.update { it.copy(saving = true) }

        viewModelScope.launch {
            val session = current.session
            trainingRepository.finishWorkout(
                WorkoutSession(
                    id = "ws_${UUID.randomUUID()}",
                    dateEpochMillis = System.currentTimeMillis(),
                    sportId = profile.sport.id,
                    title = session?.title ?: "Entrenamiento",
                    durationMinutes = session?.estimatedMinutes ?: 45,
                    kcal = session?.estimatedKcal ?: 350,
                    photoUri = current.photoUri,
                    note = current.caption,
                ),
            )

            val published = current.shareToCommunity && current.photoUri != null
            if (published) {
                communityRepository.publish(
                    imageUri = current.photoUri,
                    caption = current.caption.ifBlank { defaultCaption(profile, session) },
                    sportId = profile.sport.id,
                )
            }
            _state.update { it.copy(saving = false) }
            onDone(published)
        }
    }

    private fun defaultCaption(profile: UserProfile, session: RoutineSession?): String =
        "${session?.title ?: "Entrenamiento"} completado \uD83D\uDCAA #${profile.sport.displayName.lowercase()}"
}
