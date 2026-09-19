package com.forma.app.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.model.Post
import com.forma.app.domain.model.Reaction
import com.forma.app.domain.model.Sport
import com.forma.app.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommunityUiState(
    val loading: Boolean = true,
    val posts: List<Post> = emptyList(),
    val selectedSport: Sport? = null,
    val reactionSheetPostId: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
) : ViewModel() {

    private val selectedSport = MutableStateFlow<Sport?>(null)
    private val reactionSheetPostId = MutableStateFlow<String?>(null)

    private val posts = selectedSport
        .flatMapLatest { sport -> communityRepository.feed(sport?.id) }

    val state: StateFlow<CommunityUiState> = combine(
        posts,
        selectedSport,
        reactionSheetPostId,
    ) { list, sport, sheet ->
        CommunityUiState(
            loading = false,
            posts = list,
            selectedSport = sport,
            reactionSheetPostId = sheet,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CommunityUiState())

    val followingCount: StateFlow<Int> = communityRepository.followingCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val sportsWithPosts: StateFlow<List<Sport>> = communityRepository.feed(null)
        .map { list -> list.map { it.sport }.distinct() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectSport(sport: Sport?) {
        selectedSport.value = sport
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch { communityRepository.toggleLike(postId) }
    }

    fun openReactions(postId: String) {
        reactionSheetPostId.value = postId
    }

    fun closeReactions() {
        reactionSheetPostId.value = null
    }

    fun react(postId: String, reaction: Reaction) {
        viewModelScope.launch {
            communityRepository.react(postId, reaction)
            reactionSheetPostId.value = null
        }
    }

    fun toggleFollow(authorId: String) {
        viewModelScope.launch { communityRepository.toggleFollow(authorId) }
    }
}
