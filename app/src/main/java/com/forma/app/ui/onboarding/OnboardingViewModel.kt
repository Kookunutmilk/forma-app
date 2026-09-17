package com.forma.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.data.catalog.EquipmentCatalog
import com.forma.app.data.catalog.IngredientCatalog
import com.forma.app.domain.model.ExperienceLevel
import com.forma.app.domain.model.Goal
import com.forma.app.domain.model.Sport
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val step: Int = 0,
    val name: String = "",
    val photoUri: String? = null,
    val age: Int = 21,
    val weightKg: Int = 60,
    val heightCm: Int = 165,
    val goal: Goal = Goal.GAIN_MUSCLE,
    val sport: Sport = Sport.GYM,
    val level: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    val equipment: Set<String> = EquipmentCatalog.defaultSelection(Sport.GYM),
    val ingredients: Set<String> = IngredientCatalog.defaultSelection,
    val saving: Boolean = false,
) {
    val totalSteps = 5

    val canContinue: Boolean
        get() = when (step) {
            0 -> name.trim().length >= 2
            3 -> equipment.isNotEmpty()
            4 -> ingredients.size >= 3
            else -> true
        }
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun prefill(name: String, photoUrl: String?) {
        _state.update { current ->
            if (current.name.isBlank()) {
                current.copy(name = name, photoUri = current.photoUri ?: photoUrl)
            } else {
                current
            }
        }
    }

    fun onName(value: String) = _state.update { it.copy(name = value) }
    fun onPhoto(uri: String?) = _state.update { it.copy(photoUri = uri) }
    fun onAge(value: Int) = _state.update { it.copy(age = value) }
    fun onWeight(value: Int) = _state.update { it.copy(weightKg = value) }
    fun onHeight(value: Int) = _state.update { it.copy(heightCm = value) }
    fun onGoal(goal: Goal) = _state.update { it.copy(goal = goal) }
    fun onLevel(level: ExperienceLevel) = _state.update { it.copy(level = level) }

    fun onSport(sport: Sport) = _state.update {
        // Al cambiar de deporte, el equipo anterior ya no aplica: se reinicia al de ese deporte.
        it.copy(sport = sport, equipment = EquipmentCatalog.defaultSelection(sport))
    }

    fun toggleEquipment(id: String) = _state.update { current ->
        current.copy(
            equipment = if (id in current.equipment) {
                current.equipment - id
            } else {
                current.equipment + id
            },
        )
    }

    fun selectAllEquipment() = _state.update {
        it.copy(equipment = EquipmentCatalog.allIds(it.sport))
    }

    fun clearEquipment() = _state.update { it.copy(equipment = emptySet()) }

    fun toggleIngredient(id: String) = _state.update { current ->
        current.copy(
            ingredients = if (id in current.ingredients) {
                current.ingredients - id
            } else {
                current.ingredients + id
            },
        )
    }

    fun back() = _state.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }

    fun next(userId: String, email: String, onFinished: () -> Unit) {
        val current = _state.value
        if (!current.canContinue) return
        if (current.step < current.totalSteps - 1) {
            _state.update { it.copy(step = it.step + 1) }
            return
        }
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            profileRepository.save(
                UserProfile(
                    id = userId,
                    name = current.name.trim(),
                    email = email,
                    photoUri = current.photoUri,
                    age = current.age,
                    weightKg = current.weightKg,
                    heightCm = current.heightCm,
                    sport = current.sport,
                    level = current.level,
                    goal = current.goal,
                    equipment = current.equipment,
                    likedIngredients = current.ingredients,
                    onboardingCompleted = true,
                ),
            )
            _state.update { it.copy(saving = false) }
            onFinished()
        }
    }
}
