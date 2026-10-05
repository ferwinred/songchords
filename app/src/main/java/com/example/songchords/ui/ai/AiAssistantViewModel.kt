package com.example.songchords.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.songchords.ai.GeminiAiAssistantEngine
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class MessageSender {
    USER, ASSISTANT
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
)

data class AiAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val currentInput: String = "",
    val hasAccess: Boolean = false,
    val currentTier: UserTier = UserTier.REGISTERED_FREE,
    val quickPrompts: List<String> = emptyList()
)

class AiAssistantViewModel(
    private val engine: GeminiAiAssistantEngine = GeminiAiAssistantEngine(),
    tierFlow: StateFlow<UserTier> = SubscriptionManager.currentTier
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AiAssistantUiState(
            quickPrompts = engine.quickPrompts
        )
    )
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tierFlow.collect { tier ->
                _uiState.update { state ->
                    state.copy(
                        currentTier = tier,
                        hasAccess = SubscriptionManager.hasAiAssistantAccess(tier)
                    )
                }
            }
        }
    }

    fun onInputChange(newInput: String) {
        _uiState.update { it.copy(currentInput = newInput) }
    }

    fun sendMessage(prompt: String = _uiState.value.currentInput) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || _uiState.value.isGenerating || !_uiState.value.hasAccess) return

        val userMsg = ChatMessage(sender = MessageSender.USER, text = trimmed)
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg,
                currentInput = "",
                isGenerating = true
            )
        }

        viewModelScope.launch {
            val responseText = engine.generateResponse(trimmed)
            val aiMsg = ChatMessage(sender = MessageSender.ASSISTANT, text = responseText)
            _uiState.update { state ->
                state.copy(
                    messages = state.messages + aiMsg,
                    isGenerating = false
                )
            }
        }
    }

    fun onQuickPromptSelected(prompt: String) {
        sendMessage(prompt)
    }

    class Factory(
        private val engine: GeminiAiAssistantEngine = GeminiAiAssistantEngine()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AiAssistantViewModel(engine) as T
        }
    }
}
