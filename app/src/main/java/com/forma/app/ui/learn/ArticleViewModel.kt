package com.forma.app.ui.learn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.model.Article
import com.forma.app.domain.repository.LearnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArticleUiState(
    val loading: Boolean = true,
    val article: Article? = null,
    val related: List<Article> = emptyList(),
)

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val learnRepository: LearnRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val articleId: String = savedStateHandle.get<String>("articleId").orEmpty()

    val state: StateFlow<ArticleUiState> = combine(
        learnRepository.article(articleId),
        learnRepository.articles(null),
    ) { article, all ->
        ArticleUiState(
            loading = false,
            article = article,
            related = all
                .filter { it.id != articleId && it.category == article?.category }
                .take(3),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ArticleUiState())

    fun toggleSaved() {
        viewModelScope.launch { learnRepository.toggleSaved(articleId) }
    }
}
