package com.forma.app.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.model.ChatMessage
import com.forma.app.domain.repository.AiRepository
import com.forma.app.domain.repository.ChatRepository
import com.forma.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiUiState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val thinking: Boolean = false,
    val error: String? = null,
    val providerName: String = "",
    val userName: String = "",
)

@HiltViewModel
class AiViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val profileRepository: ProfileRepository,
    aiRepository: AiRepository,
) : ViewModel() {

    val suggestedQuestions: List<String> = chatRepository.suggestedQuestions

    private val input = MutableStateFlow("")
    private val thinking = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private val providerName = MutableStateFlow(aiRepository.providerName)

    val state: StateFlow<AiUiState> = combine(
        chatRepository.messages(),
        input,
        combine(thinking, error) { isThinking, failure -> isThinking to failure },
        profileRepository.profile,
        providerName,
    ) { messages, text, (isThinking, failure), profile, provider ->
        AiUiState(
            messages = messages,
            input = text,
            thinking = isThinking,
            error = failure,
            providerName = provider,
            userName = profile?.firstName.orEmpty(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AiUiState())

    fun onInput(value: String) {
        input.value = value
    }

    fun send(text: String = input.value) {
        val message = text.trim()
        if (message.isBlank() || thinking.value) return
        input.value = ""
        thinking.value = true
        error.value = null
        viewModelScope.launch {
            runCatching { chatRepository.send(message) }
                .onFailure { error.value = "El asistente no pudo responder. Intenta de nuevo." }
            thinking.value = false
        }
    }

    fun clear() {
        viewModelScope.launch { chatRepository.clear() }
    }
}
