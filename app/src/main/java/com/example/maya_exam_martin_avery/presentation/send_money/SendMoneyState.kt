package com.example.maya_exam_martin_avery.presentation.send_money

/**
 * UI state for the Send Money screen.
 *
 * Note: bottom sheet state is kept here so it survives recomposition/config changes.
 */
data class SendMoneyState(
    val isLoading: Boolean = false,
    val balance: Double = 0.0,
    val amountInput: String = "",
    val screenErrorMessage: String = "",
    val sheet: SendMoneySheetState? = null,
)

sealed interface SendMoneySheetState {
    data class Success(val sentAmount: Double) : SendMoneySheetState
    data class Failure(val message: String) : SendMoneySheetState
}

