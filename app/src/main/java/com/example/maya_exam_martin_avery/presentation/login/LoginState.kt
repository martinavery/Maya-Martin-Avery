package com.example.maya_exam_martin_avery.presentation.login

data class LoginState(
    val screenErrorMessage: String = "",
    val isLoading: Boolean = false,
    val userName: String = "",
    val password: String = "",
    val isButtonDisabled: Boolean = true
)
