package com.example.maya_exam_martin_avery.presentation.start

import androidx.lifecycle.ViewModel
import com.example.maya_exam_martin_avery.domain.usecase.GetCurrentUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface StartDestination {
    data object Login : StartDestination
    data object Wallet : StartDestination
}

@HiltViewModel
class StartViewModel @Inject constructor(
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : ViewModel() {
    // Computes the initial destination synchronously from SharedPreferences.
    // This runs once per ViewModel instance and avoids flicker on app launch.
    val destination: StartDestination = run {
        val currentUserId = getCurrentUserIdUseCase.invoke()
        if (currentUserId != null && currentUserId > 0L) {
            StartDestination.Wallet
        } else {
            StartDestination.Login
        }
    }
}

