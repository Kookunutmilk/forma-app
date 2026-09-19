package com.forma.app.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IngredientsUiState(
    val loading: Boolean = true,
    val selected: Set<String> = emptySet(),
    val saving: Boolean = false,
)

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(IngredientsUiState())
    val state: StateFlow<IngredientsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = profileRepository.current()
            _state.update {
                it.copy(loading = false, selected = profile?.likedIngredients.orEmpty())
            }
        }
    }

    fun toggle(id: String) = _state.update { current ->
        current.copy(
            selected = if (id in current.selected) current.selected - id else current.selected + id,
        )
    }

    fun save(onDone: () -> Unit) {
        if (_state.value.saving) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            val profile = profileRepository.current()
            if (profile != null) {
                profileRepository.save(profile.copy(likedIngredients = _state.value.selected))
            }
            _state.update { it.copy(saving = false) }
            onDone()
        }
    }
}
