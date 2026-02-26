package com.example.maya_exam_martin_avery.presentation.my_wallet

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(): ViewModel(){
    private val _uiState = MutableStateFlow(WalletState())
    val uiState: StateFlow<WalletState> = _uiState
}