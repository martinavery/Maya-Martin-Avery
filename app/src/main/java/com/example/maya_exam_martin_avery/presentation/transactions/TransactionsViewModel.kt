package com.example.maya_exam_martin_avery.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import com.example.maya_exam_martin_avery.domain.usecase.GetRemoteTransactionsUseCase
import com.example.maya_exam_martin_avery.domain.usecase.ObserveLocalTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val observeLocalTransactionsUseCase: ObserveLocalTransactionsUseCase,
    private val getRemoteTransactionsUseCase: GetRemoteTransactionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsState())
    val uiState: StateFlow<TransactionsState> = _uiState

    private val userId: Long? = getCurrentUserIdUseCase.invoke()

    init {
        subscribeLocal()
        refreshRemote()
    }

    fun refreshRemote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRemoteLoading = true, screenErrorMessage = "") }

            val result = getRemoteTransactionsUseCase.invoke()
            result.onSuccess { items ->
                _uiState.update { it.copy(isRemoteLoading = false, remoteTransactions = items) }
            }.onFailure { t ->
                _uiState.update {
                    it.copy(
                        isRemoteLoading = false,
                        screenErrorMessage = t.message ?: "Failed to load remote transactions.",
                    )
                }
            }
        }
    }

    private fun subscribeLocal() {
        val uid = userId
        if (uid == null) {
            _uiState.update { it.copy(screenErrorMessage = "No current user found. Please log in.") }
            return
        }

        viewModelScope.launch {
            observeLocalTransactionsUseCase.invoke(uid).collect { local ->
                _uiState.update { it.copy(localTransactions = local) }
            }
        }
    }
}

