package com.example.maya_exam_martin_avery.presentation.my_wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetWalletByUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.SaveCurrentUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val getWalletByUserIdUseCase: GetWalletByUserIdUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(WalletState())
    val uiState: StateFlow<WalletState> = _uiState

    init {
        loadWallet()
    }

    private fun loadWallet() {
        val currentUserId = getCurrentUserIdUseCase.invoke()
        if (currentUserId == null) {
            // Wallet requires a logged-in user; this prevents a crash if the screen is reached without one.
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