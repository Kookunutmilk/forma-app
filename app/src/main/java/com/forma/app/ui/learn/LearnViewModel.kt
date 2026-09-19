package com.forma.app.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.model.Article
import com.forma.app.domain.model.ArticleCategory
import com.forma.app.domain.repository.LearnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearnUiState(
    val loading: Boolean = true,
    val featured: Article? = null,
    val articles: List<Article> = emptyList(),
    val category: ArticleCategory? = null,
    val query: String = "",
    val onlySaved: Boolean = false,
    val savedCount: Int = 0,
)

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val learnRepository: LearnRepository,
) : ViewModel() {

    private val category = MutableStateFlow<ArticleCategory?>(null)
    private val query = MutableStateFlow("")
    private val onlySaved = MutableStateFlow(false)

    val state: StateFlow<LearnUiState> = combine(
        learnRepository.articles(null),
        category,
        query,
        onlySaved,
    ) { all, selected, text, saved ->
        val normalized = text.trim().lowercase()
        val filtered = all
            .filter { selected == null || it.category == selected }
            .filter { !saved || it.saved }
            .filter {
                normalized.isBlank() ||
                    it.title.lowercase().contains(normalized) ||
                    it.summary.lowercase().contains(normalized)
            }
        // El destacado solo encabeza la lista sin filtros activos; si no, entra como una tarjeta más.
        val noFilters = selected == null && normalized.isBlank() && !saved
        val featured = filtered.firstOrNull { it.featured }.takeIf { noFilters }
        LearnUiState(
            loading = false,
            featured = featured,
            articles = filtered.filterNot { featured != null && it.id == featured.id },
            category = selected,
            query = text,
            onlySaved = saved,
            savedCount = all.count { it.saved },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LearnUiState())

    fun selectCategory(value: ArticleCategory?) {
        category.value = value
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun toggleOnlySaved() {
        onlySaved.value = !onlySaved.value
    }

    fun toggleSaved(id: String) {
        viewModelScope.launch { learnRepository.toggleSaved(id) }
    }
}
