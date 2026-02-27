package com.example.maya_exam_martin_avery.presentation.send_money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetWalletByUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.PostTransactionUseCase
import com.example.maya_exam_martin_avery.domain.usecase.SaveLocalTransactionUseCase
import com.example.maya_exam_martin_avery.domain.usecase.SendMoneyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SendMoneyEffect {
    data object NavigateBackToWallet : SendMoneyEffect
}

@HiltViewModel
class SendMoneyViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getWalletByUserIdUseCase: GetWalletByUserIdUseCase,
    private val sendMoneyUseCase: SendMoneyUseCase,
    private val saveLocalTransactionUseCase: SaveLocalTransactionUseCase,
    private val postTransactionUseCase: PostTransactionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SendMoneyState())
    val uiState: StateFlow<SendMoneyState> = _uiState

    private val _effects = MutableSharedFlow<SendMoneyEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    init {
        loadBalance()
    }

    fun onAmountChanged(raw: String) {
        _uiState.update { current ->
            current.copy(
                amountInput = sanitizeAmountInput(raw),
                // Clear inline errors as the user edits.
                screenErrorMessage = "",
            )
        }
    }

    fun onSubmit() {
        val current = uiState.value
        if (current.isLoading) return

        val amount = current.amountInput.toDoubleOrNull()
        if (amount == null) {
            // Use the bottom sheet for submit failures to match the requirement.
            showFailureSheet("Please enter a valid amount.")
            return
        }

        if (amount <= 0.0) {
            showFailureSheet("Amount must be greater than 0.")
            return
        }

        if (amount > current.balance) {
            showFailureSheet("Amount must be less than or equal to your available balance.")
            return
        }

        val userId = getCurrentUserIdUseCase.invoke()
        if (userId == null) {
            showFailureSheet("No current user found. Please log in.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, screenErrorMessage = "") }

            val result = sendMoneyUseCase.invoke(userId = userId, amount = amount)
            result.onSuccess {
                val description = "Sent ₱%.2f".format(amount)
                val now = System.currentTimeMillis()

                // Local history is the source of truth because JSONPlaceholder does not retain writes.
                saveLocalTransactionUseCase.invoke(
                    userId = userId,
                    amount = amount,
                    description = description,
                    createdAtEpochMs = now,
                )

                // Best-effort POST to satisfy \"must use API\" requirement; do not block success UX on failure.
                postTransactionUseCase.invoke(
                    userId = userId,
                    amount = amount,
                    description = description,
                )

                // Show success sheet; navigation happens on Done.
                _uiState.update { it.copy(isLoading = false, sheet = SendMoneySheetState.Success(sentAmount = amount)) }
            }.onFailure { t ->
                val message = t.message?.takeIf { it.isNotBlank() } ?: "Transaction failed."
                _uiState.update { it.copy(isLoading = false, sheet = SendMoneySheetState.Failure(message = message)) }
            }
        }
    }

    fun onDismissSheet() {
        _uiState.update { it.copy(sheet = null) }
    }

    fun onDoneFromSheet() {
        val sheet = uiState.value.sheet
        _uiState.update { it.copy(sheet = null) }

        if (sheet is SendMoneySheetState.Success) {
            // Navigate only on success to match the requested flow.
            _effects.tryEmit(SendMoneyEffect.NavigateBackToWallet)
        }
    }

    fun refreshBalance() {
        // Exposed for lifecycle-driven refreshes (e.g., returning to this screen).
        loadBalance()
    }

    private fun loadBalance() {
        val userId = getCurrentUserIdUseCase.invoke()
        if (userId == null) {
            _uiState.update { it.copy(screenErrorMessage = "No current user found. Please log in.") }
            return
        }

        viewModelScope.launch {
            val result = getWalletByUserIdUseCase.invoke(userId)
            result.onSuccess { wallet ->
                _uiState.update { it.copy(balance = wallet.balance) }
            }.onFailure { t ->
                _uiState.update {
                    it.copy(screenErrorMessage = t.message ?: "Failed to load wallet.")
                }
            }
        }
    }

    private fun showFailureSheet(message: String) {
        _uiState.update { it.copy(sheet = SendMoneySheetState.Failure(message = message)) }
    }

    private fun sanitizeAmountInput(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""

        // Allow a single leading '-' so negative values validate correctly (e.g., show "greater than 0").
        val isNegative = trimmed.startsWith('-')
        val unsigned = trimmed.removePrefix("-")

        // Allow only digits and a single '.', with at most 2 decimals.
        val normalized = if (unsigned.startsWith('.')) "0$unsigned" else unsigned

        val sb = StringBuilder(normalized.length)
        var dotSeen = false
        var decimalsCount = 0

        for (ch in normalized) {
            when {
                ch.isDigit() -> {
                    if (dotSeen) {
                        if (decimalsCount >= 2) continue
                        decimalsCount++
                    }
                    sb.append(ch)
                }
                ch == '.' -> {
                    if (dotSeen) continue
                    dotSeen = true
                    sb.append(ch)
                }
                else -> {
                    // Ignore non-numeric characters.
                }
            }
        }

        val amount = sb.toString()
        return if (isNegative && amount.isNotEmpty()) "-$amount" else amount
    }
}

