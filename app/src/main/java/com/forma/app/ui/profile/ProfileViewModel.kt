package com.forma.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.data.local.FormaPreferences
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.Post
import com.forma.app.domain.model.TrainingStats
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.repository.AuthRepository
import com.forma.app.domain.repository.CommunityRepository
import com.forma.app.domain.repository.ProfileRepository
import com.forma.app.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val loading: Boolean = true,
    val profile: UserProfile? = null,
    val stats: TrainingStats? = null,
    val posts: List<Post> = emptyList(),
    val totalWorkouts: Int = 0,
    val restSeconds: Int = 90,
    val remindersEnabled: Boolean = true,
    val provider: String = "",
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val trainingRepository: TrainingRepository,
    private val communityRepository: CommunityRepository,
    private val authRepository: AuthRepository,
    private val preferences: FormaPreferences,
) : ViewModel() {

    val restOptions = listOf(45, 60, 90, 120, 180)

    val state: StateFlow<ProfileUiState> = combine(
        profileRepository.profile,
        trainingRepository.stats(),
        trainingRepository.sessions(),
        communityRepository.myPosts(),
        combine(preferences.restSeconds, preferences.remindersEnabled) { rest, reminders ->
            rest to reminders
        },
    ) { profile, stats, sessions, posts, (rest, reminders) ->
        ProfileUiState(
            loading = false,
            profile = profile,
            stats = stats,
            posts = posts,
            totalWorkouts = sessions.size,
            restSeconds = rest,
            remindersEnabled = reminders,
            provider = authRepository.providerName,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun updatePhoto(uri: String?) {
        viewModelScope.launch {
            profileRepository.updatePhoto(uri)
            profileRepository.current()?.let { communityRepository.syncMeWithProfile(it) }
        }
    }

    fun updateBody(weightKg: Int, heightCm: Int, age: Int) {
        updateProfile { it.copy(weightKg = weightKg, heightCm = heightCm, age = age) }
    }

    fun updateGoal(goal: Goal) {
        updateProfile { it.copy(goal = goal) }
    }

    fun setRestSeconds(seconds: Int) {
        viewModelScope.launch { preferences.setRestSeconds(seconds) }
    }

    fun setReminders(enabled: Boolean) {
        viewModelScope.launch { preferences.setRemindersEnabled(enabled) }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            profileRepository.clear()
        }
    }

    private fun updateProfile(transform: (UserProfile) -> UserProfile) {
        viewModelScope.launch {
            val current = profileRepository.current() ?: return@launch
            val updated = transform(current)
            profileRepository.save(updated)
            communityRepository.syncMeWithProfile(updated)
        }
    }
}
