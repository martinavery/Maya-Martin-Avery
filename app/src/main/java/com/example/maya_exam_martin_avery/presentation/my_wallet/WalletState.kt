package com.example.maya_exam_martin_avery.presentation.my_wallet

data class WalletState(
    val errorMessage: String = "",
    val balance: Double = 0.0,
    // UI preference: whether the user wants their balance visible on-screen.
    val isBalanceVisible: Boolean = true
)
