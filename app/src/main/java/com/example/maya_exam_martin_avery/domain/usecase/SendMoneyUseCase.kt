package com.example.maya_exam_martin_avery.domain.usecase

import com.example.maya_exam_martin_avery.domain.repository.WalletRepository
import kotlin.math.round
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendMoneyUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
) {
    /**
     * Sends money by deducting [amount] from the current wallet balance and persisting it.
     *
     * Returns the new persisted balance on success.
     */
    suspend operator fun invoke(userId: Long, amount: Double): Result<Double> {
        if (amount <= 0.0) {
            // Validation lives in the domain layer so presentation stays thin.
            return Result.failure(IllegalArgumentException("Amount must be greater than 0."))
        }

        val walletResult = walletRepository.getWalletByUserId(userId)
        return walletResult.fold(
            onSuccess = { wallet ->
                if (amount > wallet.balance) {
                    Result.failure(IllegalArgumentException("Insufficient balance."))
                } else {
                    // Persist a stable 2-decimal balance so the UI doesn't show floating point artifacts.
                    val newBalance = roundToCents(wallet.balance - amount)
                    walletRepository.updateWalletBalance(userId = userId, newBalance = newBalance).map { newBalance }
                }
            },
            onFailure = { t -> Result.failure(t) },
        )
    }

    private fun roundToCents(value: Double): Double = round(value * 100.0) / 100.0
}

