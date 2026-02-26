package com.example.maya_exam_martin_avery.presentation.login

import androidx.lifecycle.ViewModel
import com.example.maya_exam_martin_avery.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginState>(LoginState())
    val uiState: StateFlow<LoginState> = _uiState


}