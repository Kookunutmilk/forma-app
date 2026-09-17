package com.forma.app.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.model.DayMealPlan
import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.UserProfile
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.repository.NutritionRepository
import com.forma.app.domain.repository.ProfileRepository
import com.forma.app.ui.home.todayWeekDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DietUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val profile: UserProfile? = null,
    val selectedDay: WeekDay = todayWeekDay(),
    val plan: DayMealPlan? = null,
    val expandedSlot: MealSlot? = null,
)

@HiltViewModel
class DietViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val nutritionRepository: NutritionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DietUiState())
    val state: StateFlow<DietUiState> = _state.asStateFlow()

    private val profile: StateFlow<UserProfile?> = profileRepository.profile
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            combine(
                profileRepository.profile.filterNotNull(),
                nutritionRepository.choicesChanged(),
            ) { profile, _ -> profile }.collect { current -> reload(current) }
        }
    }

    private suspend fun reload(current: UserProfile) {
        val day = _state.value.selectedDay
        runCatching { nutritionRepository.dayPlan(current, day) }
            .onSuccess { plan ->
                _state.update {
                    it.copy(loading = false, error = null, profile = current, plan = plan)
                }
            }
            .onFailure {
                _state.update {
                    it.copy(loading = false, error = "No pudimos generar tu plan de comidas.")
                }
            }
    }

    fun selectDay(day: WeekDay) {
        _state.update { it.copy(selectedDay = day, loading = true, expandedSlot = null) }
        viewModelScope.launch { profile.value?.let { reload(it) } }
    }

    fun toggleOptions(slot: MealSlot) = _state.update {
        it.copy(expandedSlot = if (it.expandedSlot == slot) null else slot)
    }

    fun chooseOption(slot: MealSlot, recipeId: String) {
        viewModelScope.launch {
            nutritionRepository.chooseOption(_state.value.selectedDay, slot, recipeId)
            _state.update { it.copy(expandedSlot = null) }
        }
    }

    fun retry() {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch { profile.value?.let { reload(it) } }
    }
}
