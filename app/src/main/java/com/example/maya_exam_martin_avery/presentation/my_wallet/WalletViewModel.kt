package com.example.maya_exam_martin_avery.presentation.my_wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maya_exam_martin_avery.domain.usecase.ClearPreferencesUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetWalletByUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WalletEffect {
    data object NavigateToLogin : WalletEffect
}

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val getWalletByUserIdUseCase: GetWalletByUserIdUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val clearPreferencesUseCase: ClearPreferencesUseCase,
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(WalletState())
    val uiState: StateFlow<WalletState> = _uiState

    private val _effects = MutableSharedFlow<WalletEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    init {
        loadWallet()
    }

    fun refreshWallet() {
        // Allows the UI to explicitly refresh after returning from a screen that mutates balance (e.g., Send Money).
        loadWallet()
    }

    fun onToggleBalanceVisibility() {
        // Keep this in ViewModel state so it survives recomposition/config changes.
        _uiState.update { current ->
            current.copy(isBalanceVisible = !current.isBalanceVisible)
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            // Logout behavior: clear all app preferences (including stored session).
            clearPreferencesUseCase.invoke()
            _effects.emit(WalletEffect.NavigateToLogin)
        }
    }

    private fun loadWallet() {
        val currentUserId = getCurrentUserIdUseCase.invoke()
        if (currentUserId == null) {
            _uiState.update { it.copy(errorMessage = "No current user found. Please log in.") }
            return
        }

        viewModelScope.launch {
            val result = getWalletByUserIdUseCase.invoke(currentUserId)
            result.onSuccess { wallet ->
                _uiState.update { it.copy(balance = wallet.balance) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        errorMessage = error.localizedMessage ?: "Error occured"
                    )
                }
            }
        }
    }

}