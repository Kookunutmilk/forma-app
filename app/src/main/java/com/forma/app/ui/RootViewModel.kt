package com.forma.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.repository.AuthRepository
import com.forma.app.domain.repository.AuthUser
import com.forma.app.domain.repository.CommunityRepository
import com.forma.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RootState {
    data object Loading : RootState
    data object NeedsAuth : RootState
    data class NeedsOnboarding(val user: AuthUser) : RootState
    data class Ready(val profile: UserProfile) : RootState
}

@HiltViewModel
class RootViewModel @Inject constructor(
    authRepository: AuthRepository,
    profileRepository: ProfileRepository,
    community: CommunityRepository,
) : ViewModel() {

    val state: StateFlow<RootState> =
        combine(authRepository.currentUser, profileRepository.profile) { user, profile ->
            when {
                user == null -> RootState.NeedsAuth
                profile == null || !profile.onboardingCompleted -> RootState.NeedsOnboarding(user)
                else -> RootState.Ready(profile)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RootState.Loading)

    init {
        // El perfil público de la comunidad se mantiene sincronizado con el perfil local.
        viewModelScope.launch {
            profileRepository.profile.filterNotNull().collect { profile ->
                community.syncMeWithProfile(profile)
            }
        }
    }
}
