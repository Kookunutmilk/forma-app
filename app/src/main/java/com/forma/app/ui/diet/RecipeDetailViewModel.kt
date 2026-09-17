package com.forma.app.ui.diet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.data.catalog.RecipeCatalog
import com.forma.app.domain.model.MealSlot
import com.forma.app.domain.model.Recipe
import com.forma.app.domain.model.WeekDay
import com.forma.app.domain.repository.NutritionRepository
import com.forma.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailUiState(
    val loading: Boolean = true,
    val recipe: Recipe? = null,
    val slot: MealSlot = MealSlot.BREAKFAST,
    val plateUri: String? = null,
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nutritionRepository: NutritionRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val recipeId: String = savedStateHandle.get<String>("recipeId").orEmpty()
    private val day: WeekDay = WeekDay.fromIndex(savedStateHandle.get<Int>("dayIndex") ?: 0)
    private val slot: MealSlot = MealSlot.fromId(savedStateHandle.get<String>("slotId"))

    private val _state = MutableStateFlow(RecipeDetailUiState(slot = slot))
    val state: StateFlow<RecipeDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val recipe = RecipeCatalog.byId(recipeId)
            val profile = profileRepository.current()
            val plateUri = profile?.let {
                runCatching { nutritionRepository.dayPlan(it, day) }
                    .getOrNull()
                    ?.meals
                    ?.firstOrNull { meal -> meal.slot == slot }
                    ?.plateUri
            }
            _state.update {
                it.copy(loading = false, recipe = recipe, plateUri = plateUri)
            }
        }
    }

    fun onPlatePhoto(uri: String?) {
        _state.update { it.copy(plateUri = uri) }
        viewModelScope.launch {
            nutritionRepository.setPlatePhoto(day, slot, recipeId, uri)
        }
    }
}
