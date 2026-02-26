package com.example.maya_exam_martin_avery.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maya_exam_martin_avery.domain.error.InvalidCredentialsException
import com.example.maya_exam_martin_avery.domain.error.LoginAppException
import com.example.maya_exam_martin_avery.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginEffect {
    data object NavigateToNext : LoginEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginState>(LoginState())
    val uiState: StateFlow<LoginState> = _uiState

    private val _effects = MutableSharedFlow<LoginEffect>()
    val effects = _effects.asSharedFlow()

    fun setUsername(userName: String) {
        _uiState.update { currentState ->
            currentState
                .copy(userName = userName)
                .withDerivedUi()
        }
    }

    fun setPassword(password: String) {
        _uiState.update { currentState ->
            currentState
                .copy(password = password)
                .withDerivedUi()
        }
    }

    fun login() {
        val current = uiState.value
        if (current.isLoading) return

        val userName = current.userName.trim()
        val password = current.password

        if (userName.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(screenErrorMessage = "Username and password are required.")
                    .withDerivedUi()
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    screenErrorMessage = "",
                ).withDerivedUi()
            }

            val result = loginUseCase.invoke(userName = userName, password = password)

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false).withDerivedUi() }
                    _effects.tryEmit(LoginEffect.NavigateToNext)
                }
                .onFailure { t ->
                    val message = when (t) {
                        is InvalidCredentialsException -> "Invalid username or password."
                        is LoginAppException -> "Login failed. Please try again."
                        else -> t.message ?: "Login failed."
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            screenErrorMessage = message,
                        ).withDerivedUi()
                    }
                }
        }
    }

    private fun LoginState.withDerivedUi(): LoginState {
        // Button is disabled while loading or when required inputs are empty.
        val disabled = isLoading || userName.isBlank() || password.isBlank()
        return copy(isButtonDisabled = disabled)
    }
}