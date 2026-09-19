package com.forma.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forma.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isRegister: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val googleLoading: Boolean = false,
    val error: String? = null,
    val providerName: String = "",
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && (!isRegister || name.isNotBlank())
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState(providerName = authRepository.providerName))
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun toggleMode() = _state.update {
        it.copy(isRegister = !it.isRegister, error = null)
    }

    fun onName(value: String) = _state.update { it.copy(name = value, error = null) }
    fun onEmail(value: String) = _state.update { it.copy(email = value, error = null) }
    fun onPassword(value: String) = _state.update { it.copy(password = value, error = null) }

    fun submit() {
        val current = _state.value
        if (current.loading) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val result = if (current.isRegister) {
                authRepository.signUpWithEmail(current.name, current.email, current.password)
            } else {
                authRepository.signInWithEmail(current.email, current.password)
            }
            result.onFailure { error ->
                _state.update {
                    it.copy(loading = false, error = error.message ?: "No pudimos iniciar sesión.")
                }
            }
            // Si tiene éxito, RootViewModel cambia de pantalla al observar la sesión.
            result.onSuccess { _state.update { it.copy(loading = false) } }
        }
    }

    fun signInWithGoogle() {
        if (_state.value.googleLoading) return
        _state.update { it.copy(googleLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.signInWithGoogle()
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            googleLoading = false,
                            error = error.message ?: "No pudimos continuar con Google.",
                        )
                    }
                }
                .onSuccess { _state.update { it.copy(googleLoading = false) } }
        }
    }
}
